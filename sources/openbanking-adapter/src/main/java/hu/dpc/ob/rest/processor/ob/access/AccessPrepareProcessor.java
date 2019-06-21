/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.rest.processor.ob.ObPrepareProcessor;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-prepare-processor")
public class AccessPrepareProcessor extends ObPrepareProcessor {

    private AccessSettings accessSettings;
    private ApiService apiService;

    @Autowired
    public AccessPrepareProcessor(AccessSettings accessSettings, ApiService apiService) {
        this.accessSettings = accessSettings;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        Message in = exchange.getIn();
        String clientId = in.getHeader(accessSettings.getHeader(getSchema(), AccessSettings.AccessHeader.CLIENT).getKey(), String.class);
        String apiUserId = in.getHeader(accessSettings.getHeader(getSchema(), AccessSettings.AccessHeader.USER).getKey(), String.class);
        ContextUtils.assertNotNull(apiUserId);
        apiService.populateUserProps(exchange, apiUserId, clientId);
    }
}
