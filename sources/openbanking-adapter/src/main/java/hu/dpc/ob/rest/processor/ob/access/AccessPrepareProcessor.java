/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.processor.ob.ObPrepareProcessor;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("access-ob-prepare-processor")
public class AccessPrepareProcessor extends ObPrepareProcessor {

    private AccessSettings accessSettings;
    private ApiService apiService;
    private ConsentService consentService;

    @Autowired
    public AccessPrepareProcessor(AccessSettings accessSettings, ApiService apiService, ConsentService consentService) {
        this.accessSettings = accessSettings;
        this.apiService = apiService;
        this.consentService = consentService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        @NotNull Consent consent = consentService.getConsentById(consentId);

        String clientId = consent.getClientId();
        ContextUtils.assertNotNull(clientId);

        Message in = exchange.getIn();
        ApiSchema schema = exchange.getProperty(ExchangeHeader.SCHEMA.getKey(), ApiSchema.class);
        String apiUserId = in.getHeader(accessSettings.getHeaderProps(schema, AccessSettings.AccessHeader.USER).getKey(), String.class);
        ContextUtils.assertNotNull(apiUserId);
        User user = consent.getUser();
        if (user != null)
            ContextUtils.assertEq(apiUserId, user.getApiUserId());

        apiService.populateUserProps(exchange, apiUserId, clientId);
    }
}
