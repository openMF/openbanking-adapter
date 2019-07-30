/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.AisConsentCreateRequestDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component("api-ob-ais-consent-create-validate-processor")
public class AisConsentCreateValidateProcessor extends ApiValidateProcessor {

    @Autowired
    public AisConsentCreateValidateProcessor(AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService, PaymentService paymentService) {
        super(adapterSettings, apiService, consentService, paymentService);
    }


    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        AisConsentCreateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), AisConsentCreateRequestDto.class);
        consentService.validatePermissions(clientId, request.getData().getPermissions());
    }
}
