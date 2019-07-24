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
import hu.dpc.ob.rest.dto.ob.api.BalancesResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountResponseDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("api-ob-balance-processor")
public class BalanceRequestProcessor extends ApiRequestProcessor {

    private PspRestClient pspRestClient;

    @Autowired
    public BalanceRequestProcessor(PspRestClient pspRestClient) {
        this.pspRestClient = pspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String accountId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_ACCOUNT_ID);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

        PspAccountResponseDto accountResponse = pspRestClient.callAccount(accountId, pspId);

        BalancesResponseDto response = BalancesResponseDto.transform(accountResponse);
        exchange.getIn().setBody(response);
    }
}
