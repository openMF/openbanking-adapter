/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.ChargeBearerCode;
import hu.dpc.ob.domain.type.ChargeTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "charge")
public final class Charge extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "bearer_code", nullable = false, length = 32)
    private ChargeBearerCode bearer;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_code", nullable = false)
    private ChargeTypeCode type;

    @NotNull
    @Column(name = "amount", precision = 23, scale = 5, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    public Charge(@NotNull Payment payment, ChargeBearerCode bearer, ChargeTypeCode type, @NotNull BigDecimal amount,
                  @NotNull String currency) {
        this.payment = payment;
        this.bearer = bearer;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }
}
