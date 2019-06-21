/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.service;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.ConsentCreateRequestDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import static hu.dpc.ob.domain.type.ApiPermission.*;

@Service
public class ApiService {

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
        if (consent == null)
            return false;

        switch (binding) {
            case ACCOUNTS:
            case ACCOUNT:
                return consentService.hasPermission(consent, READ_ACCOUNTS_DETAIL, accountId)
                        || (!detail && consentService.hasPermission(consent, READ_ACCOUNTS_BASIC, accountId));
            case ACCOUNT_BALANCES:
                return consentService.hasPermission(consent, READ_BALANCES, accountId);
            case ACCOUNT_TRANSACTIONS:
                return consentService.hasPermission(consent, READ_TRANSACTIONS_DETAIL, accountId)
                        || (!detail && consentService.hasPermission(consent, READ_TRANSACTIONS_BASIC, accountId));
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
    public Consent requestConsent(String clientId, @NotNull ConsentCreateRequestDto request, @NotNull ApiScope scope) {
        if (clientId == null)
            throw new UnsupportedOperationException("User is not specified");
        if (clientId == null)
            throw new UnsupportedOperationException("Client is not specified");
        return Consent.create(request.getData(), scope, clientId);
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
}
