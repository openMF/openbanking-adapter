/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.PaymentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.PaymentResponseDto;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("api-ob-payment-processor")
public class PaymentRequestProcessor extends ApiRequestProcessor {

    private final PspRestClient pspRestClient;
    private final PaymentService paymentService;

    @Autowired
    public PaymentRequestProcessor(PspRestClient pspRestClient, PaymentService paymentService) {
        this.pspRestClient = pspRestClient;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String paymentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_PAYMENT_ID);
        @NotNull Payment payment = paymentService.getPaymentByPaymentId(paymentId);

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        boolean updated = updateTransferState(pspRestClient, payment, pspId);
        if (updated)
            paymentService.transferStateChanged(payment);

        PaymentResponseDto response = PaymentResponseDto.create(payment);
        exchange.getIn().setBody(response);
    }
}
