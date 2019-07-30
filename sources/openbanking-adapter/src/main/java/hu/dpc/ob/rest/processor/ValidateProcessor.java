/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.EventReasonCode;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

public class ValidateProcessor implements Processor {

    protected final AdapterSettings adapterSettings;

    protected final ApiService apiService;
    protected final ConsentService consentService;
    protected final PaymentService paymentService;

    public ValidateProcessor(AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService, PaymentService paymentService) {
        this.adapterSettings = adapterSettings;
        this.apiService = apiService;
        this.consentService = consentService;
        this.paymentService = paymentService;
    }

    protected String getPaymentId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_PAYMENT_ID);
    }

    protected String getClientPaymentId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CLIENT_PAYMENT_ID);
    }

    protected String getPartyId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_PARTY_ID);
    }

    protected String getConsentId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
    }

    protected String getAccountId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_ACCOUNT_ID);
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        ContextUtils.assertNotNull(clientId);

        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        Binding binding = exchange.getProperty(ExchangeHeader.BINDING.getKey(), Binding.class);
        if (binding != null && binding.isUserRequest())
            ContextUtils.assertNotNull(apiUserId);

        @NotNull ApiScope scope = binding.getScope();

        String resourceId = null;
        String accountId = getAccountId(exchange);

        String consentId = getConsentId(exchange);
        if (consentId != null) {
            resourceId = consentId;
            @NotNull Consent consent = consentService.getConsentById(consentId);
            checkConsent(apiUserId, clientId, scope, consent);
        }

        String paymentId = getPaymentId(exchange);
        if (paymentId != null) {
            resourceId = paymentId;
            @NotNull Payment payment = paymentService.getPaymentByPaymentId(paymentId);
            @NotNull Consent consent = payment.getConsent();
            checkConsent(apiUserId, clientId, scope, consent);

            String debtorAccountId = payment.getDebtorAccountId();
            if (debtorAccountId != null)
                accountId = debtorAccountId;
        }

        String clientPaymentId = getClientPaymentId(exchange);
        if (clientPaymentId != null) {
            @NotNull Payment payment = paymentService.getPaymentByEndToEndId(clientPaymentId);
            resourceId = payment.getPaymentId();
            @NotNull Consent consent = payment.getConsent();
            checkConsent(apiUserId, clientId, scope, consent);

            String debtorAccountId = payment.getDebtorAccountId();
            if (debtorAccountId != null)
                accountId = debtorAccountId;
        }
        if (resourceId == null)
            resourceId = getPartyId(exchange);
        if (resourceId == null)
            resourceId = accountId;

        EventReasonCode reasonCode = apiService.validateAndRegisterAction(apiUserId, clientId, binding, consentId, accountId,
                resourceId, adapterSettings.isTestEnv());
        if (reasonCode != null )
            throw new UnsupportedOperationException(reasonCode.getDisplayText() + " for " + binding);
    }

    private void checkConsent(String apiUserId, String clientId, @NotNull ApiScope scope, @NotNull Consent consent) {
        ContextUtils.assertEq(clientId, consent.getClientId());
        ContextUtils.assertEq(scope, consent.getScope());
        if (apiUserId != null && consent.getUser() != null)
            ContextUtils.assertEq(apiUserId, consent.getUser().getApiUserId());
    }
}
