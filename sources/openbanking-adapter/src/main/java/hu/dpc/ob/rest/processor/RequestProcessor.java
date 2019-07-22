/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RequestProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // nothing
    }
}
