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
import hu.dpc.ob.rest.dto.ob.api.ApiConsentResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.service.ConsentService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

@Component("api-ob-ais-consent-processor")
public class AisConsentRequestProcessor implements Processor {

    private ConsentService consentService;

    @Autowired
    public AisConsentRequestProcessor(ConsentService consentService) {
        this.consentService = consentService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        @NotNull Consent consent = consentService.getConsentById(consentId);
        if (consent.getScope() != ApiScope.AIS)
            throw new EntityNotFoundException("AIS Consent does not exists");

        ApiConsentResponseDto response = ApiConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
