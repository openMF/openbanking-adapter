/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.BalancesResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.service.ApiService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("api-ob-balances-processor")
public class BalancesRequestProcessor implements Processor {

    private PspRestClient pspRestClient;
    private ApiService apiService;

    @Autowired
    public BalancesRequestProcessor(PspRestClient pspRestClient, ApiService apiService) {
        this.pspRestClient = pspRestClient;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        PspAccountsResponseDto response = pspRestClient.callAccounts(pspUserId, pspId);

        BalancesResponseDto transform = BalancesResponseDto.transform(response);
        exchange.getIn().setBody(transform);
    }
}
