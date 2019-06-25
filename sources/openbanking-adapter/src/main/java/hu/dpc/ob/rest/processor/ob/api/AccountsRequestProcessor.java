/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.dto.ob.api.AccountsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspIdentifiersResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.service.ApiService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component("api-ob-accounts-processor")
public class AccountsRequestProcessor implements Processor {

    private PspRestClient pspRestClient;
    private ApiService apiService;

    @Autowired
    public AccountsRequestProcessor(PspRestClient pspRestClient, ApiService apiService) {
        this.pspRestClient = pspRestClient;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        PspAccountsResponseDto response = pspRestClient.callAccounts(pspUserId, pspId);

        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);

        boolean detail = apiService.hasPermission(apiUserId, clientId, ApiSettings.ApiBinding.ACCOUNTS, true);

        HashMap<String, PspIdentifiersResponseDto> idMap = new HashMap<>();
        if (detail) {
            for (PspAccountsSavingsData account : response.getSavingsAccounts()) {
                String accountId = account.getExternalId();
                if (accountId != null) {
                    PspIdentifiersResponseDto ids = pspRestClient.callIdentifiers(pspUserId, accountId, pspId);
                    if (ids != null)
                        idMap.put(accountId, ids);
                }
            }
        }

        AccountsResponseDto transform = AccountsResponseDto.transform(response, idMap, detail);
        exchange.getIn().setBody(transform);
    }
}
