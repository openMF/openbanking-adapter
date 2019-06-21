/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import static hu.dpc.ob.util.ContextUtils.PARAM_PARTY_ID;


@Component("access-ob-party-validate-processor")
public class PartyValidateProcessor extends AccessValidateProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        String partyId = ContextUtils.getPathParam(exchange, PARAM_PARTY_ID);
        ContextUtils.assertEq(apiUserId, partyId);
    }
}
