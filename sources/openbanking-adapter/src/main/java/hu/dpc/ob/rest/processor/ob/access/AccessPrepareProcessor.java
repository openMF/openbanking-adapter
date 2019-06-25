/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.rest.component.AccessRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.IntrospectResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.rest.processor.ob.ObPrepareProcessor;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-prepare-processor")
public class AccessPrepareProcessor extends ObPrepareProcessor {

    private AccessRestClient accessRestClient;
    private AccessSettings accessSettings;
    private ApiService apiService;

    @Autowired
    public AccessPrepareProcessor(AccessRestClient accessRestClient, AccessSettings accessSettings, ApiService apiService) {
        this.accessRestClient = accessRestClient;
        this.accessSettings = accessSettings;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

        String tenant = pspId.getTenant();
        Message in = exchange.getIn();
        String accessCode = in.getHeader(HttpHeader.AUTHORIZATION.asString(), String.class);

        IntrospectResponseDto clientResponse = accessRestClient.callIntrospect(getSchema(), tenant, accessCode);
        if (!clientResponse.isActive())
            throw new UnsupportedOperationException("Client access token is not valid");

        String clientId = clientResponse.getClientId();
        ContextUtils.assertNotNull(clientId);
        String apiUserId = in.getHeader(accessSettings.getHeader(getSchema(), AccessSettings.AccessHeader.USER).getKey(), String.class);
        ContextUtils.assertNotNull(apiUserId);

        apiService.populateUserProps(exchange, apiUserId, clientId);
    }
}
