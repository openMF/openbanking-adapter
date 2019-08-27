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
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.psp.PspPaymentResponseDto;
import hu.dpc.ob.rest.processor.ob.ObRequestProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ApiRequestProcessor extends ObRequestProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }

    @Transactional
    public boolean updateTransferState(PspRestClient pspRestClient, @NotNull Payment payment, @NotNull PspId pspId) {
        String transactionId = payment.getTransactionId();
        if (transactionId != null && !payment.getStatus().isComplete()) {
            PspPaymentResponseDto transactionResponse = pspRestClient.callPayment(transactionId, pspId);
            @NotNull String failedReason = transactionResponse.updateEntity(payment); // TODO
            if (failedReason != null)
                throw new UnsupportedOperationException("Can not refresh payment state: " + failedReason);
            return true;
        }
        return false;
    }
}
