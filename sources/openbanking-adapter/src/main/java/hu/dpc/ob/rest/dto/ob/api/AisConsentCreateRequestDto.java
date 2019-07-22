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
public class AisConsentCreateRequestDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private AisConsentCreateData data;

    @JsonProperty(value = "Risk", required = true)
    @NotNull
    private AisRiskData risk;

    AisConsentCreateRequestDto(@NotNull AisConsentCreateData data, @NotNull AisRiskData risk) {
        this.data = data;
        this.risk = risk;
    }
}
