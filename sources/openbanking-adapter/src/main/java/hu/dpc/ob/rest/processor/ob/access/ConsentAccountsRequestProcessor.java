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
import hu.dpc.ob.rest.dto.ob.access.AccountsHeldResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("access-ob-consent-accounts-processor")
public class ConsentAccountsRequestProcessor extends AccessRequestProcessor {

    @Autowired
    public ConsentAccountsRequestProcessor(PspRestClient pspRestClient) {
        super(pspRestClient);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

        PspAccountsResponseDto pspAccounts = getPspRestClient().callAccounts(pspUserId, pspId);

        AccountsHeldResponseDto response = AccountsHeldResponseDto.transform(pspAccounts);
        exchange.getIn().setBody(response);
    }
}
