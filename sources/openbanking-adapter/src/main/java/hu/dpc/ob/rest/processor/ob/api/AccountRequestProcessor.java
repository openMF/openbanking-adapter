/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.AccountsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountResponseDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("api-ob-account-processor")
public class AccountRequestProcessor extends ApiRequestProcessor {

    private PspRestClient pspRestClient;
    private ApiService apiService;

    @Autowired
    public AccountRequestProcessor(PspRestClient pspRestClient, ApiService apiService) {
        this.pspRestClient = pspRestClient;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String accountId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_ACCOUNT_ID);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

        PspAccountResponseDto accountResponse = pspRestClient.callAccount(accountId, pspId);

        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);

        boolean detail = apiService.hasPermission(apiUserId, clientId, ApiSettings.ApiBinding.ACCOUNT, true);

        AccountsResponseDto response = AccountsResponseDto.transform(accountResponse, detail);
        exchange.getIn().setBody(response);
    }
}
