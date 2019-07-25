/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.processor.ob.ObValidateProcessor;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component("api-ob-validate-processor")
public class ApiValidateProcessor extends ObValidateProcessor {

    @Autowired
    public ApiValidateProcessor(AdapterSettings adapterSettings, ApiService apiService, ConsentService consentService, PaymentService paymentService) {
        super(adapterSettings, apiService, consentService, paymentService);
    }


    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }
}
