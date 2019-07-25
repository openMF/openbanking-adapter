/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.EventStatusCode;
import hu.dpc.ob.domain.type.PaymentActionCode;
import hu.dpc.ob.domain.type.PaymentStatusCode;
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
@Table(name = "payment_event", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seq_no"}, name = "uk_payment_event.seq")})
public final class PaymentEvent extends AbstractEntity implements Comparable<PaymentEvent> {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_code", nullable = false)
    private PaymentActionCode action;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", length = 128, nullable = false)
    private EventStatusCode status;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "seq_no", nullable = false)
    private long seqNo;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status_code", length = 128)
    private PaymentStatusCode paymentStatus;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "cause_id")
    private ConsentEvent cause;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason", length = 64)
    private String reason;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_desc", length = 256)
    private String reasonDesc;


    PaymentEvent(@NotNull Payment payment, @NotNull PaymentActionCode action, @NotNull EventStatusCode status, @NotNull LocalDateTime createdOn,
                 long seqNo, PaymentStatusCode paymentStatus, ConsentEvent cause, String reason, String reasonDesc) {
        this.payment = payment;
        this.action = action;
        this.status = status;
        this.createdOn = createdOn;
        this.seqNo = seqNo;
        this.paymentStatus = paymentStatus;
        this.cause = cause;
        this.reason = reason;
        this.reasonDesc = reasonDesc;
    }

    PaymentEvent(@NotNull Payment payment, @NotNull PaymentActionCode action, @NotNull EventStatusCode status, @NotNull LocalDateTime createdOn,
                 long seqNo, PaymentStatusCode paymentStatus, ConsentEvent cause) {
        this(payment, action, status, createdOn, seqNo, paymentStatus, cause, null, null);
    }

    PaymentEvent(@NotNull Payment payment, @NotNull PaymentActionCode action, @NotNull EventStatusCode status, @NotNull LocalDateTime createdOn,
                 long seqNo) {
        this(payment, action, status, createdOn, seqNo, null, null);
    }

    PaymentEvent merge(PaymentEvent newEvent) {
        if (newEvent.isAccepted())
            return newEvent;
        if (isAccepted())
            return newEvent;
        if (!this.getPayment().equals(newEvent.getPayment()))
            return newEvent;
        if (!getAction().equals(newEvent.getAction()))
            return newEvent;
        if (getPaymentStatus() == null ? newEvent.getPaymentStatus() != null : !getPaymentStatus().equals(newEvent.getPaymentStatus()))
            return newEvent;
        if (getCause() == null ? newEvent.getCause() != null : !getCause().equals(newEvent.getCause()))
            return newEvent;
        if (getReason() == null ? newEvent.getReason() != null : !getReason().equals(newEvent.getReason()))
            return newEvent;

        return this; // no need to add new event, maybe we should increase an occurance number later or log all requests
    }

    public boolean isAccepted() {
        return getStatus().isAccepted();
    }

    @Override
    public int compareTo(PaymentEvent o) {
        return Long.signum(getSeqNo() - o.getSeqNo());
    }
}
