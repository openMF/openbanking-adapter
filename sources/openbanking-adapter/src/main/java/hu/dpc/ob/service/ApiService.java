/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.service;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.ConsentCreateData;
import hu.dpc.ob.rest.dto.ob.api.ConsentCreateRequestDto;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.ApiPermission.*;

@Service
public class ApiService {

    private static Logger log = LoggerFactory.getLogger(ApiService.class);

    private final UserService userService;
    private final ConsentService consentService;

    @Autowired
    public ApiService(UserService userService, ConsentService consentService) {
        this.userService = userService;
        this.consentService = consentService;
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding, boolean detail, String accountId) {
        if (!hasPermission(apiUserId, clientId, binding, detail, accountId))
            throw new UnsupportedOperationException("User '" + apiUserId + "' has no rights to " + binding.getDisplayText());
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding, String accountId) {
        checkPermission(apiUserId, clientId, binding, false, accountId);
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding, boolean detail) {
        checkPermission(apiUserId, clientId, binding, detail, null);
    }

    @Transactional
    public void checkPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding) {
        checkPermission(apiUserId, clientId, binding, false);
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding, boolean detail, String accountId) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        if (binding == null)
            throw new UnsupportedOperationException("Operation is not specified");

        User user = userService.getUserByApiId(apiUserId);
        Consent consent = consentService.getActiveConsent(user, clientId, binding.getScope());
        if (consent == null) {
            log.info("No consent exist for user: " + apiUserId + ", client: " + clientId + ", scope: " + binding.getScope());
        }

        switch (binding) {
            case ACCOUNTS:
            case ACCOUNT:
                return consentService.hasPermission(consent, READ_ACCOUNTS_DETAIL, accountId)
                        || (!detail && consentService.hasPermission(consent, READ_ACCOUNTS_BASIC, accountId));
            case BALANCES:
            case BALANCE:
                return consentService.hasPermission(consent, READ_BALANCES, accountId);
            case PARTY_PSU:
                return consentService.hasPermission(consent, READ_PARTY_PSU, accountId);
            case PARTY:
                return consentService.hasPermission(consent, READ_PARTY, accountId);
            case AIS_CONSENT:
            case AIS_CONSENT_CREATE:
                return true;
            default:
                return false;
        }
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding, boolean detail) {
        return hasPermission(apiUserId, clientId, binding, detail, null);
    }

    @Transactional
    public boolean hasPermission(String apiUserId, String clientId, ApiSettings.ApiBinding binding) {
        return hasPermission(apiUserId, clientId, binding, false);
    }

    @Transactional
    public Consent requestConsent(String clientId, @NotNull ConsentCreateRequestDto request, @NotNull ApiScope scope, List<ApiPermission> allowedPermissions) {
        if (clientId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        @NotNull ConsentCreateData consentData = request.getData();
        List<ApiPermission> permissions = consentData.getPermissions().stream().filter(allowedPermissions::contains).collect(Collectors.toList());
        return consentService.createConsent(clientId, scope, consentData.getExpirationDateTime(), consentData.getTransactionFromDateTime(),
                consentData.getTransactionToDateTime(), permissions);
    }

    @Transactional
    public Consent authorizeConsent(String apiUserId, String clientId, @NotNull ConsentUpdateRequestDto request) {
        if (apiUserId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        User user = userService.getUserByApiId(apiUserId);
        return consentService.authorizeConsent(user, request);
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
