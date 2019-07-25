/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.processor.ValidateProcessor;
import org.apache.camel.Exchange;


public abstract class ObValidateProcessor extends ValidateProcessor {

    public ObValidateProcessor(AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService, PaymentService paymentService) {
        super(adapterSettings, apiService, consentService, paymentService);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }
}
