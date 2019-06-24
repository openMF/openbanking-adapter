/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.ApiConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.api.ConsentCreateRequestDto;
import hu.dpc.ob.rest.internal.ApiSchema;
import hu.dpc.ob.service.ApiService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component("api-ob-ais-consent-create-processor")
public class AisConsentCreateProcessor implements Processor {

    private ApiSettings apiSettings;
    private ApiService apiService;

    @Autowired
    public AisConsentCreateProcessor(ApiSettings apiSettings, ApiService apiService) {
        this.apiSettings = apiSettings;
        this.apiService = apiService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        ConsentCreateRequestDto request = exchange.getIn().getBody(ConsentCreateRequestDto.class);

        ApiScope scope = ApiScope.AIS;
        Consent consent = apiService.requestConsent(clientId, request, scope, apiSettings.getValidPermissions(ApiSchema.OB, scope));

        ApiConsentResponseDto response = ApiConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
