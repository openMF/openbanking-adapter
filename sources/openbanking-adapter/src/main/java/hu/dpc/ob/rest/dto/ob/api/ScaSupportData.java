/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.ScaSupport;
import hu.dpc.ob.domain.type.AuthenticationApproachCode;
import hu.dpc.ob.domain.type.ScaExemptionCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ScaSupportData {

    @JsonProperty(value = "RequestedSCAExemptionType")
    private ScaExemptionCode scaExemptionType;

    @JsonProperty(value = "AppliedAuthenticationApproach")
    private AuthenticationApproachCode authenticationApproach;

    @JsonProperty(value = "ReferencePaymentOrderId")
    @Size(max = 128)
    private String referencePaymentOrderId;

    ScaSupportData(ScaExemptionCode scaExemptionType, AuthenticationApproachCode authenticationApproach, @Size(max = 128) String referencePaymentOrderId) {
        this.scaExemptionType = scaExemptionType;
        this.authenticationApproach = authenticationApproach;
        this.referencePaymentOrderId = referencePaymentOrderId;
    }

    @NotNull
    public static ScaSupportData create(ScaSupport scaSupport) {
        return scaSupport == null ? null : new ScaSupportData(scaSupport.getScaExemptionCode(), scaSupport.getAuthenticationApproach(),
                scaSupport.getReferencePaymentOrderId());
    }

    public ScaSupport mapToEntity(@NotNull Payment payment) {
        if (scaExemptionType == null && authenticationApproach == null && referencePaymentOrderId == null)
            return null;
        return new ScaSupport(payment, scaExemptionType, authenticationApproach, referencePaymentOrderId);
    }
}
