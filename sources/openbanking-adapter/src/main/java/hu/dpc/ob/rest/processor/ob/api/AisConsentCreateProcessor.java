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
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.AccessConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.ApiConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.api.ConsentCreateRequestDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

@Component("api-ob-ais-consent-create-processor")
public class AisConsentCreateProcessor implements Processor {

    private ApiService apiService;

    @Autowired
    public AisConsentCreateProcessor(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        ConsentCreateRequestDto request = exchange.getIn().getBody(ConsentCreateRequestDto.class);

        Consent consent = apiService.requestConsent(clientId, request, ApiScope.AIS);

        ApiConsentResponseDto response = ApiConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
