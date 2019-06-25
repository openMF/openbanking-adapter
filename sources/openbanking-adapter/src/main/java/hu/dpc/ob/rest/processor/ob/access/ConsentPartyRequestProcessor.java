/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.PartyResponseDto;
import hu.dpc.ob.rest.dto.psp.PspClientResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-consent-party-processor")
public class ConsentPartyRequestProcessor implements Processor {

    private final PspRestClient pspRestClient;

    @Autowired
    public ConsentPartyRequestProcessor(PspRestClient pspRestClient) {
        this.pspRestClient = pspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        PspClientResponseDto pspUser = pspRestClient.callClient(pspUserId, pspId);
        PartyResponseDto response = PartyResponseDto.transform(pspUser);
        exchange.getIn().setBody(response);
    }
}
