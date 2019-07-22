/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.AisApiConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.api.AisConsentCreateRequestDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component("api-ob-ais-consent-create-processor")
public class AisConsentCreateProcessor extends ApiRequestProcessor {

    private ApiService apiService;

    @Autowired
    public AisConsentCreateProcessor(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        AisConsentCreateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), AisConsentCreateRequestDto.class);

        ApiScope scope = exchange.getProperty(ExchangeHeader.SCOPE.getKey(), ApiScope.class); // AIS
        Consent consent = apiService.requestConsent(clientId, request, scope, ApiSchema.OB);

        AisApiConsentResponseDto response = AisApiConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
