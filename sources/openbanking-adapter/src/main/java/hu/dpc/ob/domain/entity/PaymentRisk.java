/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.PaymentContextCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "payment_risk", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_payment_risk.payment"),
        @UniqueConstraint(columnNames = {"delivery_address_id"}, name = "uk_payment_risk.address")})
public final class PaymentRisk extends AbstractEntity {

    @NotNull
    @OneToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_context_code")
    private PaymentContextCode paymentContext;

    @Setter(AccessLevel.PUBLIC)
    @Size(min = 3, max = 4)
    @Column(name = "merchant_category", length = 4)
    private String merchantCategory; // ISO 18245 is not free

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 70)
    @Column(name = "merchant_customer_identification", length = 70)
    private String merchantCustomerIdentification;

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private Address deliveryAddress;

    public PaymentRisk(@NotNull Payment payment, PaymentContextCode paymentContext, @Size(min = 3, max = 4) String merchantCategory,
                       @Size(max = 70) String merchantCustomerIdentification, Address deliveryAddress) {
        this.payment = payment;
        this.paymentContext = paymentContext;
        this.merchantCategory = merchantCategory;
        this.merchantCustomerIdentification = merchantCustomerIdentification;
        this.deliveryAddress = deliveryAddress;
    }
}
