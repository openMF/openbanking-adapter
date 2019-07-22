/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Consent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PisConsentUpdateRequestDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private PisConsentUpdateData data;

    PisConsentUpdateRequestDto(@NotNull PisConsentUpdateData data) {
        this.data = data;
    }

    public String updateEntity(@NotNull Consent consent) {
        return data.updateEntity(consent);
    }
}
