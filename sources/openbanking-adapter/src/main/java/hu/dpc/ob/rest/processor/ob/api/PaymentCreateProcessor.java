/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentEvent;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.PaymentCreateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.PaymentResponseDto;
import hu.dpc.ob.rest.dto.psp.PspPaymentCreateRequestDto;
import hu.dpc.ob.rest.dto.psp.PspPaymentCreateResponseDto;
import hu.dpc.ob.rest.processor.ob.access.AccessRequestProcessor;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("api-ob-payment-create-processor")
public class PaymentCreateProcessor extends AccessRequestProcessor {

    private final AdapterSettings adapterSettings;
    private final ApiService apiService;

    @Autowired
    public PaymentCreateProcessor(PspRestClient pspRestClient, AdapterSettings adapterSettings, ApiService apiService) {
        super(pspRestClient);
        this.adapterSettings = adapterSettings;
        this.apiService = apiService;
    }


    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        PaymentCreateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), PaymentCreateRequestDto.class);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);
        ApiScope scope = exchange.getProperty(ExchangeHeader.SCOPE.getKey(), ApiScope.class); // PIS

        @NotNull PaymentEvent event = apiService.createPayment(apiUserId, clientId, request, scope, adapterSettings.isTestEnv());
        PaymentResponseDto response = null;
        if (event.isAccepted()) {
            @NotNull Payment payment = event.getPayment();
            PspPaymentCreateRequestDto transactionRequest = PspPaymentCreateRequestDto.create(payment);
            PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);

            PspPaymentCreateResponseDto transactionResponse = getPspRestClient().callPaymentCreate(transactionRequest, pspId);
            //TODO handle failed response
            transactionResponse.mapToEntity(payment);
            response = PaymentResponseDto.create(payment);
        }

        exchange.getIn().setBody(response);
    }
}
