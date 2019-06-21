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
import hu.dpc.ob.rest.dto.ob.access.AccessConsentData;
import hu.dpc.ob.rest.dto.ob.access.AccessConsentResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ApiConsentResponseDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private ApiConsentData data;

    @JsonProperty(value = "Risk", required = true)
    @NotNull
    private RiskData risk;

    ApiConsentResponseDto(@NotNull ApiConsentData data, @NotNull RiskData risk) {
        this.data = data;
        this.risk = risk;
    }

    public static ApiConsentResponseDto create(Consent consent) {
        return consent == null ? null : new ApiConsentResponseDto(ApiConsentData.create(consent), RiskData.create(consent));
    }
}
