/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.type.IdentificationCode;
import hu.dpc.ob.domain.type.InteropIdentifierType;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class PspIdentifierData {

    @NotNull
    @Size(max = 32)
    private InteropIdentifierType idType;

    @NotNull
    @Size(max = 128)
    private String idValue;

    @Size(max = 128)
    private String subIdOrType;

    AccountIdentification mapToEntity() {
        return new AccountIdentification(IdentificationCode.forInteropIdType(idType), idValue, subIdOrType, null);
    }
}
