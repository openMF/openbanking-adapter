/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.rest.component.AccessRestClient;
import hu.dpc.ob.service.ApiService;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("api-ob-ais-consent-create-prepare-processor")
public class AisConsentCreatePrepareProcessor extends ApiPrepareProcessor {

    @Autowired
    public AisConsentCreatePrepareProcessor(AccessRestClient accessRestClient, ApiService apiService) {
        super(accessRestClient, apiService);
    }

    protected boolean isUserRequest() {
        return false;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }
}
