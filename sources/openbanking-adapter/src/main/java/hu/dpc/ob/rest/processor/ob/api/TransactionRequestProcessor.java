/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.TransactionsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspTransactionsResponseDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("api-ob-transaction-processor")
public class TransactionRequestProcessor extends ApiRequestProcessor {

    private PspRestClient pspRestClient;
    private ApiService apiService;

    @Autowired
    public TransactionRequestProcessor(PspRestClient pspRestClient, ApiService apiService, ConsentService consentService) {
        this.pspRestClient = pspRestClient;
        this.apiService = apiService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        String accountId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_ACCOUNT_ID);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);

        boolean debit = apiService.hasPermission(apiUserId, clientId, PermissionCode.READ_TRANSACTIONS_DEBITS, accountId);
        boolean credit = apiService.hasPermission(apiUserId, clientId, PermissionCode.READ_TRANSACTIONS_CREDITS, accountId);
        LocalDateTime fromBookingDateTime = null;
        LocalDateTime toBookingDateTime = null; // TODO

        PspTransactionsResponseDto transactionResponse = pspRestClient.callTransactions(accountId, pspId, debit, credit, fromBookingDateTime, toBookingDateTime);

        boolean detail = apiService.hasPermission(apiUserId, clientId, ApiSettings.ApiBinding.TRANSACTION, true);

        TransactionsResponseDto response = TransactionsResponseDto.transform(transactionResponse, detail);
        exchange.getIn().setBody(response);
    }
}
