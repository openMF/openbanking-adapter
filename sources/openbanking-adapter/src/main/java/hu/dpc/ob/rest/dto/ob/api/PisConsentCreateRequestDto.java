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
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.model.service.SeqNoGenerator;
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
public class PisConsentCreateRequestDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private PisConsentCreateData data;

    @JsonProperty(value = "Risk", required = true)
    @NotNull
    private RiskData risk;

    PisConsentCreateRequestDto(@NotNull PisConsentCreateData data, @NotNull RiskData risk) {
        this.data = data;
        this.risk = risk;
    }

    public Payment mapToEntity(@NotNull Consent consent, @NotNull SeqNoGenerator seqNoGenerator) {
        Payment payment = data.mapToEntity(consent, seqNoGenerator);
        payment.setRisk(risk.mapToEntity(payment));
        return payment;
    }
}
