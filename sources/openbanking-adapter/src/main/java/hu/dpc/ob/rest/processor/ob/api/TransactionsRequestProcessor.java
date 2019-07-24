/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.domain.entity.ConsentAccount;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.TransactionsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspTransactionsResponseDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component("api-ob-transactions-processor")
public class TransactionsRequestProcessor extends ApiRequestProcessor {

    private PspRestClient pspRestClient;
    private ApiService apiService;

    @Autowired
    public TransactionsRequestProcessor(PspRestClient pspRestClient, ApiService apiService, ConsentService consentService) {
        this.pspRestClient = pspRestClient;
        this.apiService = apiService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        List<ConsentAccount> accounts = apiService.getAccounts(apiUserId, clientId);

        boolean debit = apiService.hasPermission(apiUserId, clientId, PermissionCode.READ_TRANSACTIONS_DEBITS, null);
        boolean credit = apiService.hasPermission(apiUserId, clientId, PermissionCode.READ_TRANSACTIONS_CREDITS, null);
        LocalDateTime fromBookingDateTime = null;
        LocalDateTime toBookingDateTime = null; // TODO

        boolean detail = apiService.hasPermission(apiUserId, clientId, ApiSettings.ApiBinding.TRANSACTION, true);

        ArrayList<PspTransactionsResponseDto> transList = new ArrayList<>(accounts.size());
        if (detail) {
            for (ConsentAccount account : accounts) {
                String accountId = account.getAccountId();
                if (accountId != null) {
                    PspTransactionsResponseDto transResponse = pspRestClient.callTransactions(accountId, pspId, debit, credit, fromBookingDateTime, toBookingDateTime);
                    if (transResponse != null)
                        transList.add(transResponse);
                }
            }
        }

        TransactionsResponseDto response = TransactionsResponseDto.transform(transList, detail);
        exchange.getIn().setBody(response);
    }
}
