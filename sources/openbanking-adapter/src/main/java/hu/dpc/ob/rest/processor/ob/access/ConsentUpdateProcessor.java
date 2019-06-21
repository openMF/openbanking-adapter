/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.AccessConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-consent-update-processor")
public class ConsentUpdateProcessor implements Processor {

    private final ApiService apiService;

    @Autowired
    public ConsentUpdateProcessor(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);

        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        ConsentUpdateRequestDto request = exchange.getIn().getBody(ConsentUpdateRequestDto.class);
        ContextUtils.assertEq(consentId, request.getData().getConsentId());

        Consent consent = apiService.authorizeConsent(apiUserId, clientId, request);
        AccessConsentResponseDto response = AccessConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
