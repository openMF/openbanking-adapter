/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.rest.internal.ApiSchema;
import hu.dpc.ob.rest.processor.ValidateProcessor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;


@Component("access-ob-validate-processor")
public class AccessValidateProcessor extends ValidateProcessor {


    public ApiSchema getSchema() {
        return ApiSchema.OB;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // nothing
    }
}
