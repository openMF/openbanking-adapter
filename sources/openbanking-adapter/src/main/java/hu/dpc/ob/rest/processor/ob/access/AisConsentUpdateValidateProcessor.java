/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.AisConsentUpdateData;
import hu.dpc.ob.rest.dto.ob.access.AisConsentUpdateRequestDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("access-ob-ais-consent-update-validate-processor")
public class AisConsentUpdateValidateProcessor extends AccessValidateProcessor {

    @Autowired
    public AisConsentUpdateValidateProcessor(AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService, PaymentService paymentService) {
        super(adapterSettings, apiService, consentService, paymentService);
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        AisConsentUpdateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), AisConsentUpdateRequestDto.class);
        @NotNull AisConsentUpdateData data = request.getData();
        ContextUtils.assertEq(consentId, data.getConsentId());

        if (data.isAuthorize()) {
            if (data.getAccounts() == null && !consentService.isTrustedClient(clientId))
                throw new UnsupportedOperationException("Consent accounts must be specified");

            consentService.validatePermissions(clientId, data.getPermissions());
        }
    }
}
