/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.PaymentTransfer;
import hu.dpc.ob.domain.type.LocalInstrumentCode;
import hu.dpc.ob.domain.type.TransferReasonCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class TransferStatusData {

    @JsonProperty(value = "LocalInstrument")
    private LocalInstrumentCode localInstrument;

    @JsonProperty(value = "Status", required = true)
    @Size(max = 128)
    @NotEmpty
    private String status; // Status of a transfer, as assigned by the transaction administrator

    @JsonProperty(value = "StatusReason")
    private TransferReasonCode statusReason; // Reason Code provided for the status of a transfer

    @JsonProperty(value = "StatusReasonDescription")
    @Size(max = 256)
    private String reasonDescription; // Reason provided for the status of a transfer

    public TransferStatusData(LocalInstrumentCode localInstrument, @Size(max = 128) @NotEmpty String status, TransferReasonCode statusReason,
                              @Size(max = 256) String reasonDescription) {
        this.localInstrument = localInstrument;
        this.status = status;
        this.statusReason = statusReason;
        this.reasonDescription = reasonDescription;
    }

    @NotNull
    static TransferStatusData create(@NotNull PaymentTransfer transfer) {
        return new TransferStatusData(transfer.getLocalInstrument(), transfer.getTransferStatus(), transfer.getReasonCode(),
                transfer.getReasonDesc());
    }
}
