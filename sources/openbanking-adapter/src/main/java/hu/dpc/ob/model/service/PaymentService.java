/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.service;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.domain.entity.*;
import hu.dpc.ob.domain.repository.PaymentEventRepository;
import hu.dpc.ob.domain.repository.PaymentRepository;
import hu.dpc.ob.domain.repository.TrustedBeneficiaryRepository;
import hu.dpc.ob.domain.repository.TrustedUserBeneficiaryRepository;
import hu.dpc.ob.domain.type.EventReasonCode;
import hu.dpc.ob.domain.type.EventStatusCode;
import hu.dpc.ob.domain.type.PaymentActionCode;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.PaymentCreateRequestDto;
import hu.dpc.ob.rest.dto.psp.PspTransactionResponseDto;
import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.EventReasonCode.*;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
public class PaymentService {

    private static Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final AdapterSettings adapterSettings;

    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final TrustedBeneficiaryRepository trustedBeneficiaryRepository;
    private final TrustedUserBeneficiaryRepository trustedUserBeneficiaryRepository;

    private final SeqNoGenerator seqNoGenerator;

    @Autowired
    public PaymentService(AdapterSettings adapterSettings, PaymentRepository paymentRepository, PaymentEventRepository paymentEventRepository,
                          TrustedBeneficiaryRepository trustedBeneficiaryRepository, TrustedUserBeneficiaryRepository trustedUserBeneficiaryRepository,
                          SeqNoGenerator seqNoGenerator) {
        this.adapterSettings = adapterSettings;
        this.paymentRepository = paymentRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.trustedBeneficiaryRepository = trustedBeneficiaryRepository;
        this.trustedUserBeneficiaryRepository = trustedUserBeneficiaryRepository;
        this.seqNoGenerator = seqNoGenerator;
    }

    @NotNull
    @Transactional
    public Payment getPaymentByEndToEndId(String endToEndId) {
        Payment payment = paymentRepository.findByEndToEndId(endToEndId);
        if (payment == null)
            throw new UnsupportedOperationException("Payment not found for endToEndId " + endToEndId);

        return payment;
    }

    @NotNull
    @Transactional
    public Payment getPaymentByInstructionId(String instructionId) {
        Payment payment = paymentRepository.findByInstructionId(instructionId);
        if (payment == null)
            throw new UnsupportedOperationException("Payment not found for instructionId " + instructionId);

        return payment;
    }

    @NotNull
    @Transactional
    public Payment getPaymentByPaymentId(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null)
            throw new UnsupportedOperationException("Payment not found for paymentId " + paymentId);

        return payment;
    }

    @Transactional(MANDATORY)
    @NotNull
    PaymentEvent createPayment(@NotNull Payment payment, @NotNull PaymentCreateRequestDto request) {
        String reasonCode = null;
        String reasonDesc = request.updateEntity(payment);
        if (reasonDesc == null) {
            EventReasonCode rejectReason = calcPaymentRejectReason(payment, PaymentActionCode.PAYMENT_ACCEPT);
            if (rejectReason != null) {
                reasonCode = rejectReason.getId();
                reasonDesc = rejectReason.getDisplayText();
            }
        }
        else {
            reasonCode = INVALID_REQUEST.getId();
        }
        return reasonCode == null
                ? acceptAction(payment, PaymentActionCode.PAYMENT_ACCEPT, null)
                : registerAction(payment, PaymentActionCode.PAYMENT_ACCEPT, null, EventStatusCode.REJECTED, reasonCode, reasonDesc);
    }

    @Transactional
    public boolean updateTransferState(PspRestClient pspRestClient, @NotNull Payment payment, @NotNull PspId pspId) {
        String transactionId = payment.getTransactionId();
        if (transactionId != null && !payment.getStatus().isComplete()) {
            PspTransactionResponseDto transactionResponse = pspRestClient.callTransaction(transactionId, pspId);
            @NotNull String failedReason = transactionResponse.updateEntity(payment); // TODO
            return payment.transferStateChanged(seqNoGenerator);
        }
        return false;
    }

    /** Should call each time when an action is performed on an existing payment to validate the event.
     *  Internal use. Limit is calculated /payment/action */
    @Transactional(MANDATORY)
    EventReasonCode calcPaymentRejectReason(@NotNull Payment payment, @NotNull PaymentActionCode action) {
        if (payment.isExpired())
            return TRANSACTION_EXPIRED;

        List<PaymentEvent> limitEvents = payment.getEvents(action);

        BigDecimal maxAmount = adapterSettings.getMaxAmount(AdapterSettings.LIMIT_PAYMENT, payment.getCurrency());
        if (maxAmount != null && MathUtils.isGreaterThan(payment.getAmount(), maxAmount))
            return LIMIT_AMOUNT;

        Short maxNo = adapterSettings.getMaxNumber(AdapterSettings.LIMIT_PAYMENT);
        Long expiration = adapterSettings.getExpiration(AdapterSettings.LIMIT_PAYMENT);

        if (maxNo != null && limitEvents.size() >= maxNo)
            return LIMIT_NUMBER;
        if (expiration != null && !limitEvents.isEmpty() && DateUtils.isBeforeDateTimeOfTenant(limitEvents.get(0).getCreatedOn().plusSeconds(expiration)))
            return LIMIT_EXPIRATION;

        Short maxFrequency = adapterSettings.getMaxFrequency(AdapterSettings.LIMIT_PAYMENT);
        if (maxFrequency != null)  {
            @NotNull LocalDateTime now = DateUtils.getLocalDateTimeOfTenant();
            LocalDateTime from = now.with(ChronoField.NANO_OF_DAY, LocalTime.MIN.toNanoOfDay());
            LocalDateTime to = now.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
            List<PaymentEvent> dateEvents = limitEvents.stream().filter(e -> DateUtils.isAfter(e.getCreatedOn(), from) && !DateUtils.isAfter(e.getCreatedOn(), to))
                    .collect(Collectors.toList());
            if (dateEvents.size() >= maxFrequency)
                return LIMIT_FREQUENCY;
        }
        return null;
    }

    /** Should call each time when a consent authorization method is calcuated.
     * If the client requests simple authorization, the reason has to be specified and the server can not change it.
     * In case of not specified authorization we can calculate it using this method,
     * or we can override the SCA authorization request with the excuse of the returned reason here.
     * Limit is calculated /client/user/scope */
    @Transactional(MANDATORY)
    EventReasonCode calcScaReason(@NotNull User user, @NotNull Payment payment) {
        if (payment == null) // can not happen
            return null;

        if (isTrusted(user, payment))
            return null;

        BigDecimal maxAmount = adapterSettings.getMaxAmount(AdapterSettings.LIMIT_SCA, payment.getCurrency());
        if (MathUtils.isGreaterThan(payment.getAmount(), maxAmount))
            return LIMIT_AMOUNT;

        @NotNull Consent consent = payment.getConsent();
        List<Payment> payments = user.getTransactionsTillLastSca(consent.getClientId()); // scope is always PIS
        if (payments == null)
            return SCA_NEEDED;

        Short maxNo = adapterSettings.getMaxNumber(AdapterSettings.LIMIT_SCA);
        if (maxNo != null && payments.size() >= maxNo)
            return LIMIT_NUMBER;

        Long expiration = adapterSettings.getExpiration(AdapterSettings.LIMIT_SCA);
        if (expiration != null && !payments.isEmpty() && DateUtils.isBeforeDateTimeOfTenant(payments.get(0).getCreatedOn().plusSeconds(expiration)))
            return LIMIT_EXPIRATION;

        Short maxFrequency = adapterSettings.getMaxFrequency(AdapterSettings.LIMIT_SCA);
        if (maxFrequency != null)  {
            @NotNull LocalDateTime now = DateUtils.getLocalDateTimeOfTenant();
            LocalDateTime from = now.with(ChronoField.NANO_OF_DAY, LocalTime.MIN.toNanoOfDay());
            LocalDateTime to = now.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());

            List<Payment> datePayments = payments.stream().filter(e -> DateUtils.isAfter(e.getCreatedOn(), from) && !DateUtils.isAfter(e.getCreatedOn(), to))
                    .collect(Collectors.toList());
            if (datePayments.size() >= maxFrequency)
                return LIMIT_FREQUENCY;
        }

        return maxAmount == null && maxNo == null && expiration == null && maxFrequency == null ? SCA_NEEDED : null;
    }

    @Transactional(MANDATORY)
    boolean isTrusted(@NotNull Payment payment) {
        TrustedBeneficiary trusted = trustedBeneficiaryRepository.findByAccount(payment.getCreditorIdentification());
        return trusted != null && !trusted.isExpired();
    }

    @Transactional(MANDATORY)
    boolean isTrusted(@NotNull User user, @NotNull Payment payment) {
        if (isTrusted(payment))
            return true;

        TrustedUserBeneficiary userTrusted = trustedUserBeneficiaryRepository
                .findByClientIdAndUserAndAccount(payment.getConsent().getClientId(), user, payment.getCreditorIdentification());
        return userTrusted != null && !userTrusted.isExpired() && !userTrusted.isAboveLimit(payment.getAmount());
    }

    @Transactional(MANDATORY)
    PaymentEvent registerAction(@NotNull Payment payment, @NotNull PaymentActionCode actionCode, ConsentEvent cause, @NotNull EventStatusCode status, String reasonCode,
                                String reasonDesc) {
        PaymentEvent event = payment.action(actionCode, status, cause, reasonCode, reasonDesc, seqNoGenerator);

        if (event != null)
            paymentEventRepository.save(event);
        return event;
    }

    @Transactional(MANDATORY)
    PaymentEvent rejectAction(@NotNull Payment payment, @NotNull PaymentActionCode actionCode, ConsentEvent cause, @NotNull EventReasonCode reasonCode) {
        return registerAction(payment, actionCode, cause, EventStatusCode.REJECTED, reasonCode.getId(), reasonCode.getDisplayText());
    }

    @Transactional(MANDATORY)
    PaymentEvent acceptAction(@NotNull Payment payment, @NotNull PaymentActionCode actionCode, ConsentEvent cause) {
        return registerAction(payment, actionCode, cause, EventStatusCode.ACCEPTED, null, null);
    }
}
