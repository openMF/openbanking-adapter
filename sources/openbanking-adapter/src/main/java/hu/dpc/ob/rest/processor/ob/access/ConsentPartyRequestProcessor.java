/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.PartyResponseDto;
import hu.dpc.ob.rest.dto.psp.PspClientResponseDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-consent-party-processor")
public class ConsentPartyRequestProcessor extends AccessRequestProcessor {

    @Autowired
    public ConsentPartyRequestProcessor(PspRestClient pspRestClient) {
        super(pspRestClient);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        PspClientResponseDto pspUser = getPspRestClient().callClient(pspUserId, pspId);

        PartyResponseDto response = PartyResponseDto.transform(pspUser);
        exchange.getIn().setBody(response);
    }
}
