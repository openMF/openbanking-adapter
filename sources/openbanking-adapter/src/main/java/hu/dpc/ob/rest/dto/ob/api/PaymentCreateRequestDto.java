/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Payment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PaymentCreateRequestDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private PaymentCreateData data;

    @JsonProperty(value = "Risk", required = true)
    @NotNull
    private RiskData risk;

    PaymentCreateRequestDto(@NotNull PaymentCreateData data, @NotNull RiskData risk) {
        this.data = data;
        this.risk = risk;
    }

    public String updateEntity(@NotNull Payment payment) {
        String failedReason = data.updateEntity(payment);
        if (failedReason == null)
            risk.updateEntity(payment);
        return failedReason;
    }
}
