/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.access.PisAccessConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.access.PisConsentUpdateRequestDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("access-ob-pis-consent-update-processor")
public class PisConsentUpdateProcessor extends AccessRequestProcessor {

    private final AdapterSettings adapterSettings;
    private final ApiService apiService;
    private final ConsentService consentService;

    @Autowired
    public PisConsentUpdateProcessor(PspRestClient pspRestClient, AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService) {
        super(pspRestClient);
        this.adapterSettings = adapterSettings;
        this.apiService = apiService;
        this.consentService = consentService;
    }


    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        @NotNull Consent consent = consentService.getConsentById(consentId);
        Payment payment = consent.getPayment();
        boolean init = payment.getDebtorAccountId() == null;

        PisConsentUpdateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), PisConsentUpdateRequestDto.class);
        String failedReason = request.updateEntity(consent);
        if (failedReason != null)
            throw new UnsupportedOperationException(failedReason);


        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        ApiScope scope = exchange.getProperty(ExchangeHeader.SCOPE.getKey(), ApiScope.class); // PIS

        DebtorInit debtorInit = init ? calcDebtorInit(payment, pspId) : null;
        apiService.updateConsent(apiUserId, clientId, request, debtorInit, scope, adapterSettings.isTestEnv());

        PisAccessConsentResponseDto response = PisAccessConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
