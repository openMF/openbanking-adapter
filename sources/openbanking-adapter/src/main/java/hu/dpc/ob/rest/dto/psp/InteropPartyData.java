/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import hu.dpc.ob.domain.entity.AccountIdentification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class InteropPartyData {

    @NotNull
    private InteropPartyIdInfoData partyIdInfo; // mandatory
    private String merchantClassificationCode; // optional
    private String name; // optional
    private InteropPersonalInfoData personalInfo; // optional

    public InteropPartyData(@NotNull InteropPartyIdInfoData partyIdInfo, String merchantClassificationCode, String name, InteropPersonalInfoData personalInfo) {
        this.partyIdInfo = partyIdInfo;
        this.merchantClassificationCode = merchantClassificationCode;
        this.name = name;
        this.personalInfo = personalInfo;
    }

    static InteropPartyData create(AccountIdentification identification, String merchantClassificationCode) {
        return identification == null ? null : new InteropPartyData(InteropPartyIdInfoData.create(identification), merchantClassificationCode,
                identification.getName(), null);
    }
}
