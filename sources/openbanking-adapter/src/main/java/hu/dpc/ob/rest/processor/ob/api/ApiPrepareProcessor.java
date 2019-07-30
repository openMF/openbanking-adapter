/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.AccessRestClient;
import hu.dpc.ob.rest.dto.ob.access.IntrospectResponseDto;
import hu.dpc.ob.rest.dto.ob.access.UserInfoResponseDto;
import hu.dpc.ob.rest.processor.ob.ObPrepareProcessor;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component("api-ob-prepare-processor")
public class ApiPrepareProcessor extends ObPrepareProcessor {

    AdapterSettings adapterSettings;

    AccessRestClient accessRestClient;

    ApiService apiService;

    @Autowired
    public ApiPrepareProcessor(AdapterSettings adapterSettings, AccessRestClient accessRestClient, ApiService apiService) {
        this.adapterSettings = adapterSettings;
        this.accessRestClient = accessRestClient;
        this.apiService = apiService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        Binding binding = exchange.getProperty(ExchangeHeader.BINDING.getKey(), Binding.class);
        String tenant = pspId.getTenant();
        Message in = exchange.getIn();
        String accessCode = in.getHeader(HttpHeader.AUTHORIZATION.asString(), String.class);
        String apiUserId = null;

        ApiSchema schema = exchange.getProperty(ExchangeHeader.SCHEMA.getKey(), ApiSchema.class);

        String clientId;
        if (adapterSettings.isTestEnv()) {
            String pspS = pspId.getId();
            clientId = pspS + "_client";
            apiUserId = pspS + "_user";
        } else {
            IntrospectResponseDto clientResponse = accessRestClient.callIntrospect(schema, tenant, accessCode);
            if (!clientResponse.isActive())
                throw new UnsupportedOperationException("Client access token is not valid");

            clientId = clientResponse.getClientId();
            ContextUtils.assertNotNull(clientId);
            if (binding.isUserRequest()) {
                UserInfoResponseDto userResponse = accessRestClient.callUserInfo(schema, tenant, accessCode);
                apiUserId = userResponse.getSub();
                ContextUtils.assertNotNull(apiUserId);
            }
        }

        apiService.populateUserProps(exchange, apiUserId, clientId);
    }
}
