/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AuthenticationApproachCode;
import hu.dpc.ob.domain.type.ScaExemptionCode;
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
@Table(name = "sca_support", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_authorization.payment")})
public final class ScaSupport extends AbstractEntity {

    @NotNull
    @OneToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "sca_exemption_code", length = 32)
    private ScaExemptionCode scaExemptionCode;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_approach_code", length = 32)
    private AuthenticationApproachCode authenticationApproach;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 128)
    @Column(name = "reference_payment_id", length = 128)
    private String referencePaymentOrderId;

    public ScaSupport(@NotNull Payment payment, ScaExemptionCode scaExemptionCode, AuthenticationApproachCode authenticationApproach,
                      @Size(max = 128) String referencePaymentOrderId) {
        this.payment = payment;
        this.scaExemptionCode = scaExemptionCode;
        this.authenticationApproach = authenticationApproach;
        this.referencePaymentOrderId = referencePaymentOrderId;
    }
}
