/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.InteropAmountType;
import hu.dpc.ob.domain.type.InteropInitiatorType;
import hu.dpc.ob.domain.type.InteropScenario;
import hu.dpc.ob.domain.type.InteropTransactionRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "interop_payment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_interop_payment.payment")})
public final class InteropPayment extends AbstractEntity {

    @NotNull
    @OneToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "amount_type_code", nullable = false, length = 32)
    private InteropAmountType amountType;

    // -- TransactionType

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scenario_code", nullable = false, length = 32)
    private InteropScenario scenario;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "subscenario_code", length = 32)
    private String subScenario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "initiator_code", nullable = false, length = 32)
    private InteropTransactionRole initiator;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "initiator_type_code", nullable = false, length = 32)
    private InteropInitiatorType initiatorType;

    // -- Refund

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "refund_transaction_id", length = 36)
    private String refundTransactionId;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "refund_reason", length = 128)
    private String refundReason;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "balance_of_payments", length = 3)
    private String balanceOfPayments; // String of three characters, consisting of digits only, see https://www.imf.org/external/np/sta/bopcode/

    // -- GeoCode

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "geo_longitude", length = 32)
    private String longitude;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "geo_latitude", length = 32)
    private String latitude;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "note", length = 128)
    private String note;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "interopPayment")
    private List<InteropExtension> extensions = new ArrayList<>();


    public InteropPayment(@NotNull Payment payment, @NotNull InteropAmountType amountType, @NotNull InteropScenario scenario,
                          String subScenario, @NotNull InteropTransactionRole initiator, @NotNull InteropInitiatorType initiatorType,
                          String refundTransactionId, String refundReason, String balanceOfPayments, String longitude, String latitude,
                          String note) {
        this.payment = payment;
        this.amountType = amountType;
        this.scenario = scenario;
        this.subScenario = subScenario;
        this.initiator = initiator;
        this.initiatorType = initiatorType;
        this.refundTransactionId = refundTransactionId;
        this.refundReason = refundReason;
        this.balanceOfPayments = balanceOfPayments;
        this.longitude = longitude;
        this.latitude = latitude;
        this.note = note;
    }

    public InteropPayment(@NotNull Payment payment, @NotNull InteropAmountType amountType, @NotNull InteropScenario scenario,
                          String subScenario, @NotNull InteropTransactionRole initiator, @NotNull InteropInitiatorType initiatorType,
                          String longitude, String latitude, String note) {
        this(payment, amountType, scenario, subScenario, initiator, initiatorType, null, null, null, longitude, latitude, note);
    }

    public InteropPayment(@NotNull Payment payment, @NotNull InteropAmountType amountType, @NotNull InteropScenario scenario,
                          String subScenario, @NotNull InteropTransactionRole initiator, @NotNull InteropInitiatorType initiatorType) {
        this(payment, amountType, scenario, subScenario, initiator, null, null, null, null);
    }

    public boolean addExtension(@NotNull String key, @NotNull String value) {
        InteropExtension extension = new InteropExtension(this, key, value);
        getExtensions().add(extension);
        return true;
    }

    public boolean removeExtension(@NotNull InteropExtension extension) {
        extension.setInteropPayment(null);
        return getExtensions().remove(extension);
    }
}
