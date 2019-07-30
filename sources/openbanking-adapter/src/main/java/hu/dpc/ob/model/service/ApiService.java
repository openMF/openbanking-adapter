/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.service;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.domain.entity.*;
import hu.dpc.ob.domain.repository.TrustedUserBeneficiaryRepository;
import hu.dpc.ob.domain.type.*;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.AisConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.access.PisConsentUpdateData;
import hu.dpc.ob.rest.dto.ob.access.PisConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.*;
import hu.dpc.ob.rest.processor.ob.access.AccessRequestProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class ApiService {

    private static Logger log = LoggerFactory.getLogger(ApiService.class);

    private ApiSettings apiSettings;

    private final UserService userService;
    private final ConsentService consentService;

    private final TrustedUserBeneficiaryRepository trustedUserBeneficiaryRepository;

    @Autowired
    public ApiService(ApiSettings apiSettings, UserService userService, ConsentService consentService, TrustedUserBeneficiaryRepository trustedUserBeneficiaryRepository) {
        this.apiSettings = apiSettings;
        this.userService = userService;
        this.consentService = consentService;
        this.trustedUserBeneficiaryRepository = trustedUserBeneficiaryRepository;
    }

    @Transactional
    public Consent requestConsent(String clientId, @NotNull AisConsentCreateRequestDto request, @NotNull ApiScope scope, @NotNull ApiSchema schema) {
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");

        @NotNull List<PermissionCode> validPermissions = apiSettings.getValidPermissions(schema, scope);

        @NotNull AisConsentCreateData consentData = request.getData();
        List<PermissionCode> permissions = consentData.getPermissions();
        if (permissions == null)
            permissions = validPermissions;
        else
            permissions = permissions.stream().filter(validPermissions::contains).collect(Collectors.toList());

        return consentService.createConsent(clientId, scope, consentData.getExpirationDateTime(), consentData.getTransactionFromDateTime(),
                consentData.getTransactionToDateTime(), permissions);
    }

    @Transactional
    public Consent requestConsent(String clientId, @NotNull PisConsentCreateRequestDto request, @NotNull ApiScope scope) {
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        return consentService.createConsent(clientId, request, scope);
    }

    @Transactional
    public Consent initConsent(@NotNull String apiUserId, @NotNull String clientId, @NotNull String consentId, @NotNull AccessRequestProcessor.DebtorInit debtorInit,
                               @NotNull ApiScope scope) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");

        User user = userService.getUserByApiId(apiUserId);
        return consentService.initConsent(user, consentId, debtorInit, scope);
    }

    @Transactional
    @NotNull
    public Consent updateConsent(@NotNull String apiUserId, @NotNull String clientId, @NotNull AisConsentUpdateRequestDto request,
                                 AccountsData accountsData, boolean test) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        User user = userService.getUserByApiId(apiUserId);

        @NotNull ConsentActionCode action = request.getData().getAction();
        switch (action) {
            case AUTHORIZE:
                return consentService.authorizeConsent(user, request, accountsData, test);
            case REJECT:
                return consentService.rejectConsent(user, request, test);
            case REVOKE:
                return consentService.revokeConsent(user, request, test);
            default:
                throw new UnsupportedOperationException("Unsupported Consent update action " + action);
        }
    }

    @Transactional
    @NotNull
    public Consent updateConsent(@NotNull String apiUserId, @NotNull String clientId, @NotNull PisConsentUpdateRequestDto request,
                                 AccessRequestProcessor.DebtorInit debtorInit, @NotNull ApiScope scope, boolean test) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        User user = userService.getUserByApiId(apiUserId);

        @NotNull PisConsentUpdateData data = request.getData();

        @NotNull Consent consent = consentService.getConsentById(data.getConsentId());
        Payment payment = consent.getPayment();

        consentService.initDebtorIdentifiers(payment, debtorInit);

        @Valid CreditorTrustedData trustedData = data.getCreditorTrusted();
        if (trustedData != null) {
            BigDecimal limit = trustedData.getLimit();
            LocalDateTime expiresOn = trustedData.getExpirationDateTime();
            @NotNull AccountIdentification account = payment.getCreditorIdentification();

            TrustedUserBeneficiary trusted = trustedUserBeneficiaryRepository.findByClientIdAndUserAndAccount(clientId, user, account);
            if (trusted == null) {
                trusted = account.addTrustedUserBeneficiary(clientId, user, limit, expiresOn);
            }
            else {
                trusted.setLimit(limit);
                trusted.setExpiresOn(expiresOn);
            }
            trustedUserBeneficiaryRepository.save(trusted);
        }

        @NotNull ConsentActionCode action = data.getAction();
        if (action == ConsentActionCode.REJECT)
            return consentService.rejectConsent(user, request, test);
        if (action == ConsentActionCode.REVOKE)
            return consentService.revokeConsent(user, request, test);

        if (debtorInit != null) {
            consent = consentService.initConsent(user, data.getConsentId(), debtorInit, scope);
            if (!consent.isAlive())
                return consent;
        }
        return consentService.authorizeConsent(user, request, test);
    }

    @Transactional
    @NotNull
    public PaymentEvent createPayment(@NotNull String apiUserId, @NotNull String clientId, @NotNull PaymentCreateRequestDto request,
                                      @NotNull ApiScope scope, boolean test) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        User user = userService.getUserByApiId(apiUserId);

        return consentService.createPayment(user, request, test);
    }

    @Transactional(REQUIRES_NEW) // failed actions should be saved
    public EventReasonCode validateAndRegisterAction(String apiUserId, @NotNull String clientId, @NotNull Binding binding,
                                                     String consentId, String accountId, String resourceId, boolean test) {
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        if (binding == null)
            throw new UnsupportedOperationException("Operation is not specified");

        if (!binding.isUserRequest())
            return null;
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");

        @NotNull RequestSource source = binding.getSource();
        @NotNull ApiScope scope = binding.getScope();
        @NotNull ConsentActionCode actionCode = binding.getActionCode();

        if (consentId == null || scope == ApiScope.AIS) // for existing PIS consent we do not check AIS permissions any more
            checkPermission(apiUserId, clientId, binding, accountId); // throws exception if not authorized

        User user = userService.getUserByApiId(apiUserId);

        EventReasonCode eventReasonCode = consentService.calcEventRejectReason(user, clientId, source, scope, actionCode,
                resourceId, test);

        if (source != RequestSource.API)
            return eventReasonCode;

        Consent consent;
        if (consentId == null)
            consent = consentService.getActiveConsent(user, clientId, scope);
        else {
            consent = consentService.getConsentById(consentId);
            if (!consent.isActive() || consent.isExpired())
                consent = null;
        }

        if (consent == null)
            throw new UnsupportedOperationException("User '" + apiUserId + "' has no active consent to " + binding.getDisplayText());

        if (eventReasonCode == null)
            eventReasonCode = consentService.calcConsentRejectReason(source, scope, consent, actionCode, resourceId, test);

        if (eventReasonCode != null)
            consentService.rejectAction(consent, actionCode, resourceId, null, eventReasonCode);
        else if (!actionCode.isUpdate()) // update actions are registered in separated methods
            consentService.acceptAction(consent, actionCode, resourceId, null);

        return eventReasonCode;
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, Binding binding, boolean detail, String accountId) {
        if (!hasPermission(apiUserId, clientId, binding, detail, accountId))
            throw new UnsupportedOperationException("User '" + apiUserId + "' has no rights to " + binding.getDisplayText());
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, Binding binding, String accountId) {
        checkPermission(apiUserId, clientId, binding, false, accountId);
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, Binding binding, boolean detail) {
        checkPermission(apiUserId, clientId, binding, detail, null);
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, Binding binding) {
        checkPermission(apiUserId, clientId, binding, false);
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, Binding binding, boolean detail, String accountId) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        if (binding == null)
            throw new UnsupportedOperationException("Operation is not specified");

        User user = userService.getUserByApiId(apiUserId);
        Consent consent = consentService.getActiveConsent(user, clientId, ApiScope.AIS); // PIS actions must have AIS permissions
        if (consent == null) {
            log.info("No active consent exist for user: " + apiUserId + ", client: " + clientId + ", scope: " + binding.getScope());
            return false;
        }

        PermissionCode[] permissions = binding.getPermissions(detail);
        if (permissions == null)
            return true;
        for (PermissionCode permission : permissions) {
            if (consentService.hasPermission(consent, permission, accountId))
                return true;
        }
        return false;
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, PermissionCode permission, String accountId) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");

        User user = userService.getUserByApiId(apiUserId);
        Consent consent = consentService.getActiveConsent(user, clientId, ApiScope.AIS); // PIS actions must have AIS permissions
        if (consent == null) {
            log.info("No active consent exist for user: " + apiUserId + ", client: " + clientId + ", scope: " + ApiScope.AIS);
            return false;
        }

        return consentService.hasPermission(consent, permission, accountId);
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, Binding binding, boolean detail) {
        return hasPermission(apiUserId, clientId, binding, detail, null);
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding) {
        return hasPermission(apiUserId, clientId, binding, false);
    }

    @Transactional
    public List<ConsentAccount> getAccounts(String apiUserId, String clientId) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");

        User user = userService.getUserByApiId(apiUserId);
        Consent consent = consentService.getActiveConsent(user, clientId, ApiScope.AIS);
        if (consent == null) {
            log.info("No active consent exist for user: " + apiUserId + ", client: " + clientId + ", scope: " + ApiScope.AIS);
            return null;
        }
        return consent.getAccounts();
    }

    @Transactional
    public void populateUserProps(Exchange exchange, String apiUserId, String clientId) {
        exchange.setProperty(ExchangeHeader.CLIENT_ID.getKey(), clientId);
        if (apiUserId != null) {
            @NotNull User user = userService.getUserByApiId(apiUserId);
            exchange.setProperty(ExchangeHeader.USER_ID.getKey(), user.getId());
            exchange.setProperty(ExchangeHeader.API_USER_ID.getKey(), user.getApiUserId());
            exchange.setProperty(ExchangeHeader.PSP_USER_ID.getKey(), user.getPspUserId());
        }
    }

    @Transactional
    public User getMockUser() {
        return userService.getUserByApiId("bankuser1");
    }

    public String getMockClientId() {
        return "tpp1";
    }
}
