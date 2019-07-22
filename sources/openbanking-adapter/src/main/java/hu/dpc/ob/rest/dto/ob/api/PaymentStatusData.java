/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.domain.entity.PaymentTransfer;
import hu.dpc.ob.domain.type.PaymentStatusCode;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
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
public class PaymentStatusData {

    @JsonProperty(value = "PaymentTransactionId", required = true)
    @NotEmpty
    @Size(max = 210)
    private String transactionId;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private PaymentStatusCode status;

    @JsonProperty(value = "StatusUpdateDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime statusUpdateDateTime;

    @JsonProperty(value = "StatusDetail")
    private TransferStatusData transferStatus;

    public PaymentStatusData(@NotEmpty @Size(max = 210) String transactionId, @NotNull PaymentStatusCode status, @NotNull LocalDateTime statusUpdateDateTime,
                             TransferStatusData transferStatus) {
        this.transactionId = transactionId;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime;
        this.transferStatus = transferStatus;
    }

    @NotNull
    static PaymentStatusData create(@NotNull PaymentTransfer transfer) {
        return new PaymentStatusData(transfer.getPayment().getTransactionId(), transfer.getStatus(), transfer.getUpdatedOn(), TransferStatusData.create(transfer));
    }
}
