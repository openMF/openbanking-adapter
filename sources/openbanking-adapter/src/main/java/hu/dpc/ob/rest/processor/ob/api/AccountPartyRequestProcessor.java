/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.PartyResponseDto;
import hu.dpc.ob.rest.dto.psp.PspClientResponseDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("api-ob-account-party-processor")
public class AccountPartyRequestProcessor extends ApiRequestProcessor {

    private final PspRestClient pspRestClient;

    @Autowired
    public AccountPartyRequestProcessor(PspRestClient pspRestClient) {
        this.pspRestClient = pspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        // TODO: query accountId and owner of the accountId - not the login user
        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        PspClientResponseDto pspUser = pspRestClient.callClient(pspUserId, pspId);
        PartyResponseDto response = PartyResponseDto.transform(pspUser);
        exchange.getIn().setBody(response);
    }
}
