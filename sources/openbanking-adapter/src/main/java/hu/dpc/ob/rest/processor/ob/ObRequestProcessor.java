/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob;

import hu.dpc.ob.rest.processor.RequestProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ObRequestProcessor extends RequestProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }
}
