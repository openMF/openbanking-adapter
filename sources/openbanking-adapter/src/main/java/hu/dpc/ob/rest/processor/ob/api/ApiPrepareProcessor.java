/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.rest.component.AccessRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.access.IntrospectResponseDto;
import hu.dpc.ob.rest.dto.ob.access.UserInfoResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.rest.processor.ob.ObPrepareProcessor;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("api-ob-prepare-processor")
public class ApiPrepareProcessor extends ObPrepareProcessor {

    AccessRestClient accessRestClient;

    ApiService apiService;

    @Autowired
    public ApiPrepareProcessor(AccessRestClient accessRestClient, ApiService apiService) {
        this.accessRestClient = accessRestClient;
        this.apiService = apiService;
    }

    protected boolean isUserRequest() {
        return true;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        String tenant = pspId.getTenant();
        Message in = exchange.getIn();
        String accessCode = in.getHeader(HttpHeader.AUTHORIZATION.asString(), String.class);

        String clientId;
        String apiUserId;
        if (isUserRequest()) {
            IntrospectResponseDto response = accessRestClient.callIntrospect(getSchema(), tenant, accessCode);
            if (!response.isActive())
                throw new UnsupportedOperationException("User access token is not valid");

            clientId = response.getClient_id();
            apiUserId = response.getSub();
            ContextUtils.assertNotNull(apiUserId);
        }
        else {
            UserInfoResponseDto response = accessRestClient.callUserInfo(getSchema(), tenant, accessCode);
            clientId = response.getSub();
            apiUserId = null;
        }
        ContextUtils.assertNotNull(clientId);
        apiService.populateUserProps(exchange, apiUserId, clientId);
    }
}
