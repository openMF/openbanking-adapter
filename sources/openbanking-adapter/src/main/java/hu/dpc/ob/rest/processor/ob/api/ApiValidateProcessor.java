/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.processor.ValidateProcessor;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("api-ob-validate-processor")
public class ApiValidateProcessor extends ValidateProcessor {

    @Autowired
    private ApiService apiService;

    protected ApiSettings.ApiBinding getBinding() {
        return null;
    }

    protected boolean isUserRequest() {
        return false;
    }

    protected String getAccountId(Exchange exchange) {
        return ContextUtils.getPathParam(exchange, ContextUtils.PARAM_ACCOUNT_ID);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
        if (isUserRequest()) {
            String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
            String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);

            apiService.checkPermission(apiUserId, clientId, getBinding(), getAccountId(exchange));
        }
    }
}
