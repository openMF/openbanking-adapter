/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component("api-ob-ais-consent-delete-processor")
public class AisConsentDeleteProcessor extends ApiRequestProcessor {

    private ConsentService consentService;

    @Autowired
    public AisConsentDeleteProcessor(ConsentService consentService) {
        this.consentService = consentService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);

        consentService.deleteConsent(clientId, ApiScope.AIS, consentId);
    }
}
