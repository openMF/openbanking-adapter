/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.rest.dto.ob.access.AccessConsentResponseDto;
import hu.dpc.ob.service.ConsentService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("access-ob-consent-processor")
public class ConsentRequestProcessor implements Processor {

    private final ConsentService consentService;

    @Autowired
    public ConsentRequestProcessor(ConsentService consentService) {
        this.consentService = consentService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        @NotNull Consent consent = consentService.getConsentById(consentId);
        AccessConsentResponseDto response = AccessConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
