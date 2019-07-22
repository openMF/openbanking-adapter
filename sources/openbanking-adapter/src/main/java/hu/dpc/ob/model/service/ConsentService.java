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
import hu.dpc.ob.domain.repository.AccountIdentificationRepository;
import hu.dpc.ob.domain.repository.ConsentEventRepository;
import hu.dpc.ob.domain.repository.ConsentRepository;
import hu.dpc.ob.domain.repository.PaymentRepository;
import hu.dpc.ob.domain.type.*;
import hu.dpc.ob.model.ConsentStateMachine;
import hu.dpc.ob.rest.dto.ob.access.*;
import hu.dpc.ob.rest.dto.ob.api.PaymentCreateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.PisConsentCreateRequestDto;
import hu.dpc.ob.rest.processor.ob.access.AccessRequestProcessor;
import hu.dpc.ob.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.AuthenticationApproachCode.CA;
import static hu.dpc.ob.domain.type.EventReasonCode.*;
import static hu.dpc.ob.domain.type.PermissionCode.*;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
public class ConsentService {

    private static Logger log = LoggerFactory.getLogger(ConsentService.class);

    private final AdapterSettings adapterSettings;

    private final PaymentService paymentService;

    private final ConsentRepository consentRepository;
    private final ConsentEventRepository consentEventRepository;
    private final PaymentRepository paymentRepository;
    private final AccountIdentificationRepository accountIdentificationRepository;

    private final SeqNoGenerator seqNoGenerator;

    @Autowired
    public ConsentService(AdapterSettings adapterSettings, PaymentService paymentService, ConsentRepository consentRepository,
                          ConsentEventRepository consentEventRepository, PaymentRepository paymentRepository, AccountIdentificationRepository accountIdentificationRepository,
                          SeqNoGenerator seqNoGenerator) {
        this.adapterSettings = adapterSettings;
        this.paymentService = paymentService;
        this.consentRepository = consentRepository;
        this.consentEventRepository = consentEventRepository;
        this.paymentRepository = paymentRepository;
        this.accountIdentificationRepository = accountIdentificationRepository;
        this.seqNoGenerator = seqNoGenerator;
    }

    @NotNull
    @Transactional
    public Consent getConsentById(String consentId) {
        Consent consent = consentRepository.findByConsentId(consentId);
        if (consent == null)
            throw new UnsupportedOperationException("Consent not found for consent_id " + consentId);

        return consent;
    }

    @Transactional
    public Consent getActiveConsent(User user, String clientId, ApiScope scope) {
        List<Consent> consents = user.getConsents();
        for (Consent consent : consents) {
            if (consent.getClientId().equals(clientId) && consent.getScope() == scope && consent.getStatus().isActive())
                return consent;
        }
        return null;
    }

    @Transactional
    public Consent getAliveConsent(User user, String clientId, ApiScope scope) {
        List<Consent> consents = user.getConsents();
        for (Consent consent : consents) {
            if (consent.getClientId().equals(clientId) && consent.getScope() == scope && consent.getStatus().isAlive())
                return consent;
        }
        return null;
    }

    @Transactional(MANDATORY)
    Consent createConsent(@NotNull String clientId, @NotNull ApiScope scope, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                                 LocalDateTime transactionToDateTime, List<PermissionCode> permissions) {
        @NotNull Consent consent = Consent.create(clientId, scope, expirationDateTime, transactionFromDateTime, transactionToDateTime,
                permissions, seqNoGenerator);
        consentRepository.save(consent);
        return consent;
    }

    @Transactional(MANDATORY)
    Consent createConsent(@NotNull String clientId, @NotNull PisConsentCreateRequestDto request, @NotNull ApiScope scope) {
        @NotNull Consent consent = Consent.create(clientId, scope, seqNoGenerator);
        consentRepository.save(consent);
        Payment payment = request.mapToEntity(consent, seqNoGenerator);

        AccountIdentification debtorIdentification = payment.getDebtorIdentification();
        if (debtorIdentification != null) {
            AccountIdentification accountIdentification = findPersistedAccountIdentification(debtorIdentification);
            if (accountIdentification != null)
                payment.setDebtorIdentification(accountIdentification);
        }

        AccountIdentification creditorIdentification = payment.getCreditorIdentification();
        AccountIdentification accountIdentification = findPersistedAccountIdentification(creditorIdentification);
        if (accountIdentification != null) {
            payment.setCreditorIdentification(accountIdentification);
        }

        if (payment.getExpiresOn() == null) {
            LocalDateTime expiresOn = adapterSettings.calcExpiresOn(AdapterSettings.LIMIT_PAYMENT, payment.getCreatedOn());
            payment.setExpiresOn(expiresOn);
        }

        consent.setPayment(payment);
        paymentRepository.save(payment);
        return consent;
    }

    @Transactional(MANDATORY)
    AccountIdentification findPersistedAccountIdentification(AccountIdentification search) {
        @NotNull IdentificationCode scheme = search.getScheme();
        @NotEmpty String identification = search.getIdentification();
        String secondaryIdentification = search.getSecondaryIdentification();
        AccountIdentification accountIdentification = secondaryIdentification == null
                ? accountIdentificationRepository.findBySchemeAndIdentificationAndSecondaryIdentificationIsNull(scheme, identification)
                : accountIdentificationRepository.findBySchemeAndIdentificationAndSecondaryIdentification(scheme, identification, secondaryIdentification);
        if (accountIdentification != null) {
            @Size(max = 70) String name = search.getName();
            if (name != null)
                accountIdentification.setName(name); // TODO, what to do with different names, fail?
        }
        return accountIdentification;
    }

    @Transactional(MANDATORY)
    Consent initConsent(@NotNull User user, @NotNull String consentId, @NotNull AccessRequestProcessor.DebtorInit debtorInit,
                        @NotNull ApiScope scope) {
        @NotNull Consent consent = getConsentById(consentId);
        Payment payment = consent.getPayment();

        ConsentEvent consentEvent;
        if (debtorInit.failedReason != null) {
            rejectAction(consent, ConsentActionCode.PREPARE, consentId, debtorInit.failedReason);
            consentEvent = registerAction(consent, ConsentActionCode.REJECT, EventStatusCode.ACCEPTED, consentId, debtorInit.failedReason);
            paymentService.rejectAction(payment, PaymentActionCode.PAYMENT_REJECT, consentEvent, debtorInit.failedReason);
        }
        else {
            consentEvent = acceptAction(consent, ConsentActionCode.PREPARE, consentId);
            paymentService.acceptAction(payment, PaymentActionCode.PAYMENT_VALIDATE, consentEvent);

            if (debtorInit.accountId != null)
                consent.addAccount(debtorInit.accountId, consentEvent);

            if (payment.getDebtorAccountId() != null)
                paymentService.acceptAction(payment, PaymentActionCode.DEBTOR_ACCOUNT_RESOLVE, consentEvent);

            if (debtorInit.charge != null)
                payment.addCharge(debtorInit.charge);

            paymentService.acceptAction(payment, PaymentActionCode.DEBTOR_FOUNDS_CHECK, consentEvent);
            paymentService.acceptAction(payment, PaymentActionCode.DEBTOR_QUOTES, consentEvent);

            EventReasonCode scaReason = paymentService.calcScaReason(user, payment);
            ScaExemptionCode exemptionCode = null;
            AuthenticationApproachCode approachCode = CA;
            if (scaReason != null) {
                exemptionCode = ScaExemptionCode.PARTY_TO_PARTY; // TODO: what code?
                approachCode = AuthenticationApproachCode.SCA;
            }
            ScaSupport scaSupport = payment.getScaSupport();

            if (scaSupport == null)
                payment.setScaSupport(new ScaSupport(payment, exemptionCode, approachCode, null));
            else if (approachCode == CA && approachCode != scaSupport.getAuthenticationApproach()) {
                scaSupport.setAuthenticationApproach(CA);
                scaSupport.setScaExemptionCode(exemptionCode);
            }
        }
        if (consentEvent != null) {
            @NotNull LocalDateTime updatedOn = consentEvent.getCreatedOn();
            consent.setUpdatedOn(updatedOn);
            payment.setUpdatedOn(updatedOn);
        }

        // sca calc
        consentRepository.save(consent);
        return consent;
    }

    @Transactional
    public void deleteConsent(@NotNull String clientId, @NotNull ApiScope scope, String consentId) {
        @NotNull Consent consent = getConsentById(consentId);
        if (consent.getScope() != scope)
            throw new EntityNotFoundException(scope + " Consent does not exists");

        if (!consent.getClientId().equals(clientId))
            throw new UnsupportedOperationException("Client " + clientId + " is not allowed to delete consent " + consentId + " of " + consent.getClientId());

        consentRepository.delete(consent);
    }

    @Transactional
    public Consent authorizeConsent(@NotNull User user, @NotNull AisConsentUpdateRequestDto request) {
        @NotNull AisConsentUpdateData data = request.getData();
        @NotNull Consent consent = getConsentById(data.getConsentId());

        List<PermissionCode> permissions = data.getPermissions();
        for (PermissionCode permission : permissions) {
            if (!consent.hasPermission(permission))
                throw new UnsupportedOperationException("Attempt to add unsupported Permission " + permission + " to Consent " + consent.getConsentId());
        }

        @NotNull ConsentEvent event = registerUpdateAction(ApiScope.AIS, user, data);
        if (event == null) {
            return consent;
        }
        if (event.isAccepted()) {
            // no need to implement update of already authorized consent - revoke and create new
            for (Consent userConsent : user.getConsents()) {
                if (userConsent.isAlive() && consent.getScope() == userConsent.getScope() && !userConsent.getId().equals(consent.getId()) && userConsent.getClientId().equals(consent.getClientId())) {
                    log.info("Revoke user consent with id " + userConsent.getConsentId() + ", new consent was accepted " + consent.getConsentId());
                    if (userConsent.action(ConsentActionCode.REVOKE, event, seqNoGenerator) != null)
                        consentRepository.save(userConsent);
                }
            }

            consent.setUser(user);
            if (consent.isActive()) {
                consent.mergePermissions(permissions, event);
                if (data.getAccounts() != null) {
                    List<String> apiAccounts = data.getAccounts().stream().map(ConsentAccountData::getAccountId).collect(Collectors.toList());
                    consent.mergeAccounts(apiAccounts, event);
                }
            }
        }
        consentRepository.save(consent);
        return consent;
    }

    @Transactional(MANDATORY)
    Consent rejectConsent(@NotNull User user, @NotNull AisConsentUpdateRequestDto request) {
        @NotNull AisConsentUpdateData data = request.getData();
        ConsentEvent event = registerUpdateAction(ApiScope.AIS, user, data);
        return event == null ? getConsentById(data.getConsentId()) : event.getConsent();
    }

    @Transactional(MANDATORY)
    Consent revokeConsent(@NotNull User user, @NotNull AisConsentUpdateRequestDto request) {
        @NotNull AisConsentUpdateData data = request.getData();
        ConsentEvent event = registerUpdateAction(ApiScope.AIS, user, data);
        return event == null ? getConsentById(data.getConsentId()) : event.getConsent();
    }

    @Transactional(MANDATORY)
    Consent authorizeConsent(@NotNull User user, @NotNull PisConsentUpdateRequestDto request) {
        @NotNull PisConsentUpdateData data = request.getData();
        @NotNull Consent consent = getConsentById(data.getConsentId());

        @NotNull ConsentEvent event = registerUpdateAction(ApiScope.PIS, user, data);
        if (event == null)
            return consent;

        if (event.isAccepted())
            consent.setUser(user);

        consentRepository.save(consent);
        return consent;
    }

    @Transactional(MANDATORY)
    Consent rejectConsent(@NotNull User user, @NotNull PisConsentUpdateRequestDto request) {
        @NotNull PisConsentUpdateData data = request.getData();
        ConsentEvent event = registerUpdateAction(ApiScope.PIS, user, data);
        return event == null ? getConsentById(data.getConsentId()) : event.getConsent();
    }

    @Transactional(MANDATORY)
    Consent revokeConsent(@NotNull User user, @NotNull PisConsentUpdateRequestDto request) {
        @NotNull PisConsentUpdateData data = request.getData();
        ConsentEvent event = registerUpdateAction(ApiScope.PIS, user, data);
        return event == null ? getConsentById(data.getConsentId()) : event.getConsent();
    }

    @Transactional(MANDATORY)
    @NotNull
    PaymentEvent createPayment(@NotNull User user, @NotNull PaymentCreateRequestDto request) {
        @NotEmpty String consentId = request.getData().getConsentId();
        @NotNull Consent consent = getConsentById(consentId);
        @NotNull PaymentEvent event = paymentService.createPayment(consent.getPayment(), request);
        if (!event.isAccepted())
            registerAction(consent, ConsentActionCode.REJECT, EventStatusCode.ACCEPTED, consentId, event.getReason(), event.getReasonDesc());

        return event;
    }

    public void validatePermissions(@NotNull List<PermissionCode> permissions) {
        if (!permissions.contains(READ_ACCOUNTS_BASIC) && !permissions.contains(READ_ACCOUNTS_DETAIL))
            throw new UnsupportedOperationException("The permissions must contain at least " + READ_ACCOUNTS_BASIC + " or " + READ_ACCOUNTS_DETAIL);

        boolean hasTransaction = permissions.contains(READ_TRANSACTIONS_BASIC) || permissions.contains(READ_TRANSACTIONS_DETAIL);
        boolean hasCreditOrDebit = permissions.contains(READ_TRANSACTIONS_CREDITS) || permissions.contains(READ_TRANSACTIONS_DEBITS);
        if (hasTransaction ^ hasCreditOrDebit)
            throw new UnsupportedOperationException("The permissions must " + (hasTransaction ? "" : "NOT") + " contain one of " + READ_TRANSACTIONS_CREDITS + " or " + READ_TRANSACTIONS_DEBITS);
    }

    public static LocalDateTime getTransactionFromDateTime(Consent consent) {
        if (consent == null)
            return null;
        if (consent.getTransactionFrom() != null)
            return consent.getTransactionFrom();

        Payment payment = consent.getPayment();
        if (payment == null)
            return consent.getCreatedOn();

        return consent.getFirstEvent().getCreatedOn(); // must have at least one event
    }

    public static LocalDateTime getTransactionToDateTime(Consent consent) {
        if (consent == null)
            return null;
        if (consent.getTransactionTo() != null)
            return consent.getTransactionTo();

        Payment payment = consent.getPayment();
        if (payment == null)
            return consent.getCreatedOn();

        return consent.getLastEvent().getCreatedOn(); // must have at least one event
    }

    public static List<PermissionCode> getPermissions(Consent consent) {
        return consent == null ? null : consent.getPermissions().stream().map(ConsentPermission::getPermission).collect(Collectors.toList());
    }

    @Transactional
    public boolean hasPermission(Consent consent, @NotNull PermissionCode permission, String accountId) {
        if (consent == null) {
            log.info("No consent exist for permission: " + permission + ", accountId:" + accountId);
            return false;
        }
        if (!consent.isActive()) {
            log.info("Consent is not active: " + consent);
            return false;
        }
        if (consent.isExpired()) {
            log.info("Consent expired: " + consent);
            return false;
        }

        if (accountId != null) {
            boolean hasAccount = false;
            for (ConsentAccount account : consent.getAccounts()) {
                if (accountId.equals(account.getAccountId())) {
                    hasAccount = true;
                    break;
                }
            }
            if (!hasAccount) {
                log.info("Consent has no accountId permission: " + permission + ", accountId:" + accountId);
                return false;
            }
        }

        return consent.getPermission(permission) != null;
    }

    /** Should call each time when an API action is performed to validate the event.
     * Limit is calculated /client/user/scope/action/[resourceId] */
    @Transactional(MANDATORY)
    EventReasonCode calcEventRejectReason(@NotNull User user, @NotNull String clientId, @NotNull RequestSource source, @NotNull ApiScope scope, @NotNull ConsentActionCode action, String resourceId) {
        if (!user.isActive())
            return USER_INACTIVE;

        if (source != RequestSource.API)
            return null;

        // TODO: amount is not supported because money change is not supported
        Short maxNo = adapterSettings.getMaxNumber(AdapterSettings.LIMIT_EVENT);
        Long expiration = adapterSettings.getExpiration(AdapterSettings.LIMIT_EVENT);
        List<ConsentEvent> limitEvents = null;
        if (maxNo != null || expiration != null)  {
            limitEvents = resourceId == null
                    ? consentEventRepository.findLimitEvents(user, clientId, scope, action)
                    : consentEventRepository.findLimitEvents(user, clientId, scope, action, resourceId);
            if (maxNo != null && limitEvents.size() >= maxNo)
                return LIMIT_NUMBER;
            if (expiration != null && !limitEvents.isEmpty() && DateUtils.isBeforeDateTimeOfTenant(limitEvents.get(0).getCreatedOn().plusSeconds(expiration)))
                return LIMIT_EXPIRATION;
        }
        Short maxFrequency = adapterSettings.getMaxFrequency(AdapterSettings.LIMIT_EVENT);
        if (maxFrequency != null)  {
            if (limitEvents == null) {
                @NotNull LocalDateTime now = DateUtils.getLocalDateTimeOfTenant();
                LocalDateTime from = now.with(ChronoField.NANO_OF_DAY, LocalTime.MIN.toNanoOfDay());
                LocalDateTime to = now.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
                limitEvents = resourceId == null
                        ? consentEventRepository.findLimitEvents(user, clientId, scope, action, from, to)
                        : consentEventRepository.findLimitEvents(user, clientId, scope, action, from, to, resourceId);
            }
            if (limitEvents.size() >= maxFrequency)
                return LIMIT_FREQUENCY;
        }
        return null;
    }

    /** Should call each time when an action is performed on an existing consent to validate the event.
     * Limit is calculated /consent/action/[resourceId] */
    @Transactional(MANDATORY)
    EventReasonCode calcConsentRejectReason(@NotNull RequestSource source, @NotNull ApiScope scope, @NotNull Consent consent, @NotNull ConsentActionCode action, String resourceId) {
        @NotNull ConsentStatusCode status = consent.getStatus();
        if (!ConsentStateMachine.isValidAction(status, action))
            return ACTION_STATE_INVALID;

        if (consent.isExpired())
            return CONSENT_EXPIRED;

        if (action == ConsentActionCode.AUTHORIZE && scope == ApiScope.PIS) {
            PaymentAuthorization authorization = consent.getPayment().getAuthorization();
            if (authorization != null && authorization.isExpired())
                return CONSENT_EXPIRED;
        }

        if (source != RequestSource.API)
            return null;

        List<ConsentEvent> limitEvents = consent.getEvents(action);

        Short maxNo = adapterSettings.getMaxNumber(AdapterSettings.LIMIT_CONSENT);
        Long expiration = adapterSettings.getExpiration(AdapterSettings.LIMIT_CONSENT);
        if (resourceId != null)
            limitEvents = limitEvents.stream().filter(e -> resourceId.equals(e.getResourceId())).collect(Collectors.toList());

        if (maxNo != null && limitEvents.size() >= maxNo)
            return LIMIT_NUMBER;
        if (expiration != null && !limitEvents.isEmpty() && DateUtils.isBeforeDateTimeOfTenant(limitEvents.get(0).getCreatedOn().plusSeconds(expiration)))
            return LIMIT_EXPIRATION;

        Short maxFrequency = adapterSettings.getMaxFrequency(AdapterSettings.LIMIT_CONSENT);
        if (maxFrequency != null)  {
            @NotNull LocalDateTime now = DateUtils.getLocalDateTimeOfTenant();
            LocalDateTime from = now.with(ChronoField.NANO_OF_DAY, LocalTime.MIN.toNanoOfDay());
            LocalDateTime to = now.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
            List<ConsentEvent> dateEvents = limitEvents.stream().filter(e -> DateUtils.isAfter(e.getCreatedOn(), from) && !DateUtils.isAfter(e.getCreatedOn(), to))
                    .collect(Collectors.toList());
            if (dateEvents.size() >= maxFrequency)
                return LIMIT_FREQUENCY;
        }
        return null;
    }

    @Transactional(MANDATORY)
    ConsentEvent registerUpdateAction(@NotNull ApiScope scope, @NotNull User user, @NotNull ConsentUpdateData request) {
        @NotEmpty String consentId = request.getConsentId();
        if (user == null)
            throw new UnsupportedOperationException("User is not specified on Reject Consent " + consentId);

        @NotNull Consent consent = getConsentById(consentId);
        @NotNull ConsentActionCode action = request.getAction();

        EventReasonCode eventReasonCode = calcConsentRejectReason(RequestSource.ACCESS, scope, consent, action, consentId);
        if (eventReasonCode == null)
            return registerAction(consent, action, EventStatusCode.ACCEPTED, consentId, request.getReasonCode(), request.getReasonDesc());
        else
            return registerAction(consent, action, EventStatusCode.REJECTED, consentId, eventReasonCode.getId(), request.getReasonDesc());
    }

    @Transactional(MANDATORY)
    ConsentEvent registerAction(@NotNull Consent consent, @NotNull ConsentActionCode actionCode, @NotNull EventStatusCode status,
                                String resourceId, String reasonCode, String reasonDesc) {
        ConsentEvent event = consent.action(actionCode, status, resourceId, reasonCode, reasonDesc, seqNoGenerator);
        consentRepository.save(consent);
        return event;
    }

    @Transactional(MANDATORY)
    ConsentEvent registerAction(@NotNull Consent consent, @NotNull ConsentActionCode actionCode, @NotNull EventStatusCode status,
                                String resourceId, @NotNull EventReasonCode reasonCode) {
        return registerAction(consent, actionCode, status, resourceId, reasonCode.getId(), reasonCode.getDisplayText());
    }

    @Transactional(MANDATORY)
    ConsentEvent rejectAction(@NotNull Consent consent, @NotNull ConsentActionCode actionCode, String resourceId, @NotNull EventReasonCode reasonCode) {
        return registerAction(consent, actionCode, EventStatusCode.REJECTED, resourceId, reasonCode.getId(), reasonCode.getDisplayText());
    }

    @Transactional(MANDATORY)
    ConsentEvent acceptAction(@NotNull Consent consent, @NotNull ConsentActionCode actionCode, String resourceId) {
        return registerAction(consent, actionCode, EventStatusCode.ACCEPTED, resourceId, null, null);
    }
}
