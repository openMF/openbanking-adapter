/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;


@Component("api-ob-accounts-validate-processor")
public class AccountsValidateProcessor extends ApiValidateProcessor {

    protected ApiSettings.ApiBinding getBinding() {
        return ApiSettings.ApiBinding.ACCOUNTS;
    }

    protected boolean isUserRequest() {
        return true;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

    }
}
