/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "payment_account_identification", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id", "identification_id"}, name = "uk_payment_account_identification.link")})
public final class PaymentAccountIdentification extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "identification_id", nullable = false)
    private AccountIdentification accountIdentification;

    @Column(name = "debtor", nullable = false)
    private boolean debtor;

    @Column(name = "orig", nullable = false)
    private boolean orig;

    public PaymentAccountIdentification(@NotNull Payment payment, @NotNull AccountIdentification accountIdentification, boolean debtor, boolean orig) {
        this.payment = payment;
        this.accountIdentification = accountIdentification;
        this.debtor = debtor;
        this.orig = orig;
    }

}
