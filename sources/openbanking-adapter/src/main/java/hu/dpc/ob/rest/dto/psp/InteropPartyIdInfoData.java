/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class InteropPartyIdInfoData {

    @NotNull
    private InteropIdentifierType partyIdType; // mandatory, immutable
    @NotNull
    private String partyIdentifier; // mandatory, immutable
    private String partySubIdOrType; // optional, immutable
    private String fspId; // optional

    InteropPartyIdInfoData(@NotNull InteropIdentifierType partyIdType, @NotNull String partyIdentifier, String partySubIdOrType,
                                  String fspId) {
        this.partyIdType = partyIdType;
        this.partyIdentifier = partyIdentifier;
        this.partySubIdOrType = partySubIdOrType;
        this.fspId = fspId;
    }

    static InteropPartyIdInfoData create(AccountIdentification identification) {
        return identification == null ? null : new InteropPartyIdInfoData(identification.getScheme().getInteropType(), identification.getIdentification(),
                identification.getSecondaryIdentification(), null);
    }
}
