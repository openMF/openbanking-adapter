/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentTransfer;
import hu.dpc.ob.domain.type.EventStatusCode;
import hu.dpc.ob.domain.type.PaymentActionCode;
import hu.dpc.ob.model.PaymentStateMachine;
import hu.dpc.ob.rest.dto.psp.type.TransferState;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PspPaymentResponseDto {

    @JsonProperty("clientRefId")
    @Size(max = 40)
    @NotEmpty
    private String paymentId;

    @Size(max = 36)
    @NotEmpty
    private String transactionId;

    private String transferId;

    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime completedTimestamp;

    private TransferState transferState;


    @NotNull
    public String updateEntity(@NotNull Payment payment) {
        PaymentTransfer transfer = payment.getTransfer(transferId);
        if (transfer == null && getTransferId() != null) {
            transfer = payment.addTransfer(transferId);
        }
        if (transfer == null)
            return null;

        if (getTransferState() != null) {
            PaymentActionCode paymentAction = TransferState.getPaymentAction(transferState);
            if (PaymentStateMachine.isValidAction(transfer.getStatus(), paymentAction))
                transfer.setStatus(PaymentStateMachine.handleTransition(transfer.getStatus(), paymentAction, EventStatusCode.ACCEPTED));

            transfer.setTransferStatus(transferState.toString());
        }
        if (transfer.getStatus().isComplete())
            transfer.setPerformedOn(completedTimestamp);

        return null;
    }
}
