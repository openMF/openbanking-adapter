/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.*;
import hu.dpc.ob.model.PaymentStateMachine;
import hu.dpc.ob.model.service.SeqNoGenerator;
import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.MathUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.PaymentActionCode.PAYMENT_CREATE;
import static hu.dpc.ob.util.MathUtils.MATHCONTEXT;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_payment.payment"),
        @UniqueConstraint(columnNames = {"instruction_id"}, name = "uk_payment.instruction"),
        @UniqueConstraint(columnNames = {"end_to_end_id"}, name = "uk_payment.endtoend"),
        @UniqueConstraint(columnNames = {"consent_id"}, name = "uk_payment.consent"),
        @UniqueConstraint(columnNames = {"creditor_address_id"}, name = "uk_payment.address")})
public final class Payment extends AbstractEntity implements Comparable<Payment> {

    @NotNull
    @OneToOne(fetch= FetchType.EAGER, optional = false)
    @JoinColumn(name = "consent_id", nullable = false)
    private Consent consent;

    @NotNull
    @Column(name = "payment_id", nullable = false, length = 40)
    private String paymentId;

    @NotEmpty
    @Size(max = 36)
    @Column(name = "instruction_id", length = 36, nullable = false)
    private String instructionId;

    @NotEmpty
    @Size(max = 36)
    @Column(name = "end_to_end_id", length = 36, nullable = false)
    private String endToEndId;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "local_instrument_code", length = 32)
    private LocalInstrumentCode localInstrument;

    @NotNull
    @Column(name = "instructed_amount", precision = 23, scale = 5, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Setter(AccessLevel.PUBLIC)
    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "creditor_identification_id", nullable = false)
    private AccountIdentification creditorIdentification;

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "creditor_address_id")
    private Address creditorPostalAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false, length = 128)
    private PaymentStatusCode status;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expected_execution_on")
    private LocalDateTime expectedExecutionOn; // Expected execution date and time for the payment resourceId.

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expected_settlement_on")
    private LocalDateTime expectedSettlementOn; // Expected settlement  date and time for the payment resourceId.

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "performed_on")
    private LocalDateTime performedOn;

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "payment")
    private Remittance remittanceInformation; // Information supplied to enable the matching of an entry with the items that the transfer is intended to settle, such as commercial invoices in an accounts' receivable system

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "payment")
    private PaymentAuthorization authorization; // The authorisation type request from the TPP.

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "payment")
    private ScaSupport scaSupport; // Supporting Data provided by TPP, when requesting SCA Exemption.

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "payment")
    private PaymentRisk risk;

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "payment")
    private InteropPayment interopPayment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    private List<Charge> charges = new ArrayList<>(); // Set of elements used to provide details of a charge for the payment initiation.

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    private List<PaymentAccountIdentification> debtorIdentifications = new ArrayList<>(); // Used only for debtor identifiers, no need to filter

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    @OrderBy("seqNo")
    private List<PaymentEvent> events = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    private List<PaymentTransfer> transfers = new ArrayList<>();

    Payment(@NotNull Consent consent, @NotNull String paymentId, @NotEmpty @Size(max = 36) String instructionId, @NotEmpty @Size(max = 36) String endToEndId, String transactionId,
            LocalInstrumentCode localInstrument, @NotNull BigDecimal amount, @NotNull String currency, @NotNull AccountIdentification creditorIdentification, Address creditorPostalAddress,
            Remittance remittanceInformation, PaymentAuthorization authorization, ScaSupport scaSupport, PaymentRisk risk, PaymentStatusCode status, @NotNull LocalDateTime createdOn,
            LocalDateTime updatedOn, LocalDateTime expiresOn, LocalDateTime expectedExecutionOn, LocalDateTime expectedSettlementOn, LocalDateTime performedOn) {
        this.consent = consent;
        this.paymentId = paymentId;
        this.instructionId = instructionId;
        this.endToEndId = endToEndId;
        this.transactionId = transactionId;
        this.localInstrument = localInstrument;
        this.amount = amount;
        this.currency = currency;
        this.creditorIdentification = creditorIdentification;
        this.creditorPostalAddress = creditorPostalAddress;
        this.remittanceInformation = remittanceInformation;
        this.authorization = authorization;
        this.scaSupport = scaSupport;
        this.risk = risk;
        this.status = status;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.expiresOn = expiresOn;
        this.expectedExecutionOn = expectedExecutionOn;
        this.expectedSettlementOn = expectedSettlementOn;
        this.performedOn = performedOn;
    }

    Payment(@NotNull Consent consent, @NotNull String paymentId, @NotEmpty @Size(max = 36) String instructionId, @NotEmpty @Size(max = 36) String endToEndId, LocalInstrumentCode localInstrument,
            @NotNull BigDecimal amount, @NotNull String currency, @NotNull AccountIdentification creditorIdentification, Address creditorPostalAddress, Remittance remittanceInformation,
            PaymentAuthorization authorization, ScaSupport scaSupport, PaymentRisk risk, PaymentStatusCode status, @NotNull LocalDateTime createdOn, LocalDateTime expiresOn,
            LocalDateTime expectedExecutionOn, LocalDateTime expectedSettlementOn) {
        this(consent, paymentId, instructionId, endToEndId, null, localInstrument, amount, currency, creditorIdentification, creditorPostalAddress,
                remittanceInformation, authorization, scaSupport, risk, status, createdOn, null, expiresOn, expectedExecutionOn, expectedSettlementOn,
                null);
    }

    Payment(@NotNull Consent consent, @NotNull String paymentId, @NotEmpty @Size(max = 36) String instructionId, @NotEmpty @Size(max = 36) String endToEndId, @NotNull BigDecimal amount,
            @NotNull String currency, @NotNull AccountIdentification creditorIdentification, PaymentAuthorization authorization, ScaSupport scaSupport, PaymentStatusCode status,
            @NotNull LocalDateTime createdOn) {
        this(consent, paymentId, instructionId, endToEndId, null, amount, currency, creditorIdentification, null, null, authorization, scaSupport, null, status,
                createdOn, null, null, null);
    }

    Payment(@NotNull Consent consent, @NotNull String paymentId, @NotEmpty @Size(max = 36) String instructionId, @NotEmpty @Size(max = 36) String endToEndId, @NotNull BigDecimal amount,
            @NotNull String currency, @NotNull AccountIdentification creditorIdentification, @NotNull PaymentStatusCode status, @NotNull LocalDateTime createdOn) {
        this(consent, paymentId, instructionId, endToEndId, amount, currency, creditorIdentification, null, null, status, createdOn);
    }

    @NotNull
    public static Payment create(@NotNull Consent consent, @NotEmpty @Size(max = 36) String instructionIdentification, @NotEmpty @Size(max = 36) String endToEndIdentification,
                                 @NotNull BigDecimal amount, @NotNull String currency, @NotNull AccountIdentification creditorAccount, @NotNull SeqNoGenerator seqNoGenerator) {
        @NotNull Payment payment = new Payment(consent, UUID.randomUUID().toString(), instructionIdentification, endToEndIdentification, amount, currency, creditorAccount,
                null, DateUtils.getLocalDateTimeOfTenant());
        payment.action(PaymentActionCode.PAYMENT_CREATE, EventStatusCode.ACCEPTED, consent.getLastEvent(ConsentActionCode.CREATE), null, null, seqNoGenerator);
        return payment;
    }

    @NotNull
    public static Payment create(@NotNull Consent consent, @NotEmpty @Size(max = 36) String instructionIdentification, @NotEmpty @Size(max = 36) String endToEndIdentification,
                                 LocalInstrumentCode localInstrument, @NotNull BigDecimal amount, @NotNull String currency, @NotNull AccountIdentification creditorAccount,
                                 Address creditorPostalAddress, @NotNull SeqNoGenerator seqNoGenerator) {
        @NotNull Payment payment = create(consent, instructionIdentification, endToEndIdentification, amount, currency, creditorAccount, seqNoGenerator);
        payment.setLocalInstrument(localInstrument);
        payment.setCreditorPostalAddress(creditorPostalAddress);
        return payment;
    }

    public PaymentEvent action(@NotNull PaymentActionCode action, @NotNull EventStatusCode status, ConsentEvent cause, String reasonCode,
                               String reasonDesc, @NotNull SeqNoGenerator seqNoGenerator) {
        PaymentStatusCode newStatus = PaymentStateMachine.handleTransition(this.status, action, status);
        PaymentEvent event = mergeEvent(action, status, this.status == newStatus ? null : newStatus, cause,
                reasonCode, reasonDesc, seqNoGenerator);
        if (event != null) {
            if (this.status != newStatus) {
                event.setPaymentStatus(newStatus);
                if (this.status != null)
                    setUpdatedOn(event.getCreatedOn());
                setStatus(newStatus);
            }
            addEvent(event, seqNoGenerator);
            return event;
        }
        return null;
    }

    public PaymentEvent action(@NotNull PaymentActionCode action, @NotNull EventStatusCode status, String reasonCode, String reasonDesc, 
                               @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, status, null, reasonCode, reasonDesc, seqNoGenerator);
    }

    public PaymentEvent action(@NotNull PaymentActionCode action, @NotNull EventStatusCode status, EventReasonCode reasonCode,
                               @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, status, reasonCode == null ? null : reasonCode.getId(), reasonCode == null ? null : reasonCode.getDisplayText(),
                seqNoGenerator);
    }

    public PaymentEvent action(@NotNull PaymentActionCode action, @NotNull EventStatusCode status, ConsentEvent cause, EventReasonCode reasonCode,
                               @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, status, cause, reasonCode == null ? null : reasonCode.getId(), reasonCode == null ? null : reasonCode.getDisplayText(),
                seqNoGenerator);
    }

    private PaymentEvent mergeEvent(@NotNull PaymentActionCode action, @NotNull EventStatusCode status, PaymentStatusCode paymentStatus,
                                    ConsentEvent cause, String reasonCode, String reasonDesc, @NotNull SeqNoGenerator seqNoGenerator) {
        PaymentEvent lastEvent = getLastEvent(action);
        PaymentEvent newEvent = new PaymentEvent(this, action, status, cause == null ? DateUtils.getLocalDateTimeOfTenant() : cause.getCreatedOn(),
                seqNoGenerator.getPaymentEventNextSeqNo(), paymentStatus, cause, reasonCode, reasonDesc);
        if (lastEvent == null)
            return newEvent;
        newEvent = lastEvent.merge(newEvent);
        return newEvent == lastEvent ? null : newEvent;
    }

    public LocalDateTime getCreatedOn() {
        return getEvents(PAYMENT_CREATE).get(0).getCreatedOn(); // must have one and only one
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }

    public PaymentEvent getLastEvent() {
        List<PaymentEvent> events = getEvents();
        int size = events.size();
        return size == 0 ? null : events.get(size - 1);
    }

    public PaymentEvent getLastEvent(PaymentActionCode action) {
        List<PaymentEvent> events = getEvents(action);
        int size = events.size();
        return size == 0 ? null : this.events.get(size - 1);
    }

    public List<PaymentEvent> getEvents(@NotNull PaymentActionCode action) {
        return getEvents().stream().filter(e -> e.getAction() == action).sorted().collect(Collectors.toList());
    }

    private boolean addEvent(@NotNull PaymentEvent event, @NotNull SeqNoGenerator seqNoGenerator) {
        boolean added = getEvents().add(event);
        event.setPayment(this);
        if (added) {
            if (getAuthorization() != null)
                authorization.paymentEventAdded(event);
            consent.paymentEventAdded(event, seqNoGenerator);
        }
        return added;
    }

    boolean consentEventAdded(@NotNull ConsentEvent event, @NotNull SeqNoGenerator seqNoGenerator) {
        if (!event.isAccepted())
            return false;

        PaymentActionCode action = PaymentActionCode.forAction(event.getAction());
        if (action != null) {
            action(action, EventStatusCode.ACCEPTED, event, null, seqNoGenerator);
        }
        if (getAuthorization() != null) {
            authorization.consentEventAdded(event);
        }
        return false;
    }

    public boolean transferStateChanged(@NotNull SeqNoGenerator seqNoGenerator) {
        boolean hasFailed = false;
        boolean allCompleted = !getTransfers().isEmpty();
        for (PaymentTransfer transfer : transfers) {
            allCompleted &= transfer.getStatus().isComplete();
            hasFailed |= transfer.getStatus().isFailed();
        }
        if (allCompleted) { // TODO valid action?
            if (hasFailed)
                action(PaymentActionCode.PAYMENT_REJECT, EventStatusCode.ACCEPTED, null, EventReasonCode.TRANSFER_FAILED, seqNoGenerator);
            else
                action(PaymentActionCode.PAYMENT_EXECUTE, EventStatusCode.ACCEPTED, null, null, null, seqNoGenerator);
            return true;
        }
        return false;
    }

    public boolean addCharge(@NotNull Charge charge) {
        boolean added = getCharges().add(charge);
        charge.setPayment(this);
        return added;
    }

    public PaymentTransfer addTransfer(String transferId) {
        if (transferId == null)
            return null;

        PaymentTransfer transfer = new PaymentTransfer(this, transferId);
        getTransfers().add(transfer);
        return transfer;
    }

    public PaymentTransfer getTransfer(String transferId) {
        if (transferId == null)
            return null;

        for (PaymentTransfer transfer : getTransfers()) {
            if (transfer.getTransferId().equals(transferId))
                return transfer;
        }
        return null;
    }

    public AccountIdentification getOrigDebtorIdentification() {
        for (PaymentAccountIdentification accountIdentification : getDebtorIdentifications()) {
            if (accountIdentification.isOrig())
                return accountIdentification.getAccountIdentification();
        }
        return null;
    }

    public PaymentAccountIdentification getPaymentIdentification(IdentificationCode code) {
        if (code == null)
            return null;
        for (PaymentAccountIdentification accountIdentification : getDebtorIdentifications()) {
            @NotNull AccountIdentification identification = accountIdentification.getAccountIdentification();
            if (identification.getScheme() == code)
                return accountIdentification;
        }
        return null;
    }

    public AccountIdentification getDebtorIdentification(IdentificationCode code) {
        PaymentAccountIdentification paymentIdentification = getPaymentIdentification(code);
        return paymentIdentification == null ? null : paymentIdentification.getAccountIdentification();
    }

    public String getDebtorIdentificationValue(IdentificationCode code) {
        AccountIdentification debtorIdentification = getDebtorIdentification(code);
        return debtorIdentification == null ? null : debtorIdentification.getIdentification();
    }

    public AccountIdentification getDebtorIdentification(InteropIdentifierType identifierType) {
        return getDebtorIdentification(IdentificationCode.forInteropIdType(identifierType));
    }

    public String getDebtorIdentificationValue(InteropIdentifierType identifierType) {
        AccountIdentification debtorIdentification = getDebtorIdentification(identifierType);
        return debtorIdentification == null ? null : debtorIdentification.getIdentification();
    }

    public String getDebtorAccountId() {
        return getDebtorIdentificationValue(InteropIdentifierType.ACCOUNT_ID);
    }

    public boolean removeDebtorIdentification(AccountIdentification identification) {
        if (identification == null)
            return false;

        PaymentAccountIdentification paymentIdentification = getPaymentIdentification(identification.getScheme());
        if (paymentIdentification == null)
            return false;
        paymentIdentification.setPayment(null);
        return debtorIdentifications.remove(paymentIdentification);
    }

    public PaymentAccountIdentification addDebtorIdentification(AccountIdentification identification, boolean orig) {
        if (identification == null)
            return null;

        PaymentAccountIdentification pai = new PaymentAccountIdentification(this, identification, true, orig);
        getDebtorIdentifications().add(pai);
        return pai;
    }

    public BigDecimal getRequiredAmount() {
        @NotNull BigDecimal amount = getAmount();
        for (Charge charge : getCharges()) {
            amount = MathUtils.add(amount, charge.getAmount(), MATHCONTEXT);
        }
        return amount;
    }

    @Override
    public int compareTo(Payment o) {
        return getId() == null
                ? o.getId() == null ? 0 : 1
                : o.getId() == null ? -1 : getId().compareTo(o.getId());
    }
}
