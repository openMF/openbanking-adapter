/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.LocalInstrumentCode;
import hu.dpc.ob.domain.type.PaymentStatusCode;
import hu.dpc.ob.domain.type.TransferReasonCode;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "payment_transfer", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"transfer_id"}, name = "uk_payment_transfer.transfer")})
public final class PaymentTransfer extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @Column(name = "transfer_id", nullable = false, length = 36)
    private String transferId;

    @Setter(AccessLevel.PUBLIC)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false, length = 128)
    private PaymentStatusCode status;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "local_instrument_code", length = 32)
    private LocalInstrumentCode localInstrument;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "transfer_status", length = 128)
    private String transferStatus; // Status of a transfer, as assigned by the transaction administrator

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_code", length = 32)
    private TransferReasonCode reasonCode; // Reason Code provided for the status of a transfer

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_desc", length = 256)
    private String reasonDesc; // Reason provided for the status of a transfer

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "performed_on")
    private LocalDateTime performedOn;

    PaymentTransfer(@NotNull Payment payment, @NotNull String transferId, @NotNull PaymentStatusCode status, LocalInstrumentCode localInstrument,
                    String transferStatus, TransferReasonCode reasonCode, String reasonDesc, LocalDateTime updatedOn,
                    LocalDateTime performedOn) {
        this.payment = payment;
        this.transferId = transferId;
        this.status = status;
        this.localInstrument = localInstrument;
        this.transferStatus = transferStatus;
        this.reasonCode = reasonCode;
        this.reasonDesc = reasonDesc;
        this.updatedOn = updatedOn;
        this.performedOn = performedOn;
    }

    PaymentTransfer(@NotNull Payment payment, @NotNull String transferId) {
        this(payment, transferId, payment.getStatus(), null, null, null, null, null, null);
    }

    public void setStatus(PaymentStatusCode status) {
        if (status != null && status != this.status) {
            setUpdatedOn(DateUtils.getLocalDateTimeOfTenant());
            this.status = status;
        }
    }

    public void setTransferStatus(String transferStatus) {
        if (transferStatus != null && !transferStatus.equals(this.transferStatus)) {
            setUpdatedOn(DateUtils.getLocalDateTimeOfTenant());
            this.transferStatus = transferStatus;
        }
    }
}
