/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.model.service.SeqNoGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class PisData {

    @JsonProperty(value = "Initiation", required = true)
    @NotNull
    @Valid
    private PisInitiationData initiation;

    PisData(@NotNull PisInitiationData initiation) {
        this.initiation = initiation;
    }

    public Payment mapToEntity(@NotNull Consent consent, @NotNull SeqNoGenerator seqNoGenerator) {
        return initiation.mapToEntity(consent, seqNoGenerator);
    }

    String updateEntity(@NotNull Payment payment) {
        return initiation.updateEntity(payment);
    }
}
