/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.type.IdentificationCode;
import hu.dpc.ob.rest.dto.psp.PspIdentifierData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountIdentificationData {

    @JsonProperty(value = "SchemeName", required = true)
    @NotEmpty
    @Size(max = 40)
    private IdentificationCode schemeName;

    @JsonProperty(value = "Identification", required = true)
    @NotEmpty
    @Size(max = 256)
    private String identification;

    @JsonProperty(value = "SecondaryIdentification")
    @Size(max = 128) // OB: 34, interoperation: 128
    private String secondaryIdentification;

    @JsonProperty(value = "Name")
    @Size(max = 70)
    private String name;


    public AccountIdentificationData(@NotEmpty @Size(max = 40) IdentificationCode schemeName, @NotEmpty @Size(max = 256) String identification,
                                     @Size(max = 34) String secondaryIdentification, @Size(max = 70) String name) {
        this.schemeName = schemeName;
        this.identification = identification;
        this.name = name;
        this.secondaryIdentification = secondaryIdentification;
    }

    public AccountIdentificationData(@NotEmpty @Size(max = 40) IdentificationCode schemeName, @NotEmpty @Size(max = 256) String identification,
                                     @Size(max = 34) String secondaryIdentification) {
        this(schemeName, identification, secondaryIdentification, null);
    }

    static AccountIdentificationData transform(PspIdentifierData identity) {
        if (identity == null)
            return null;

        IdentificationCode schemeName = IdentificationCode.forInteropIdType(identity.getIdType());
        return schemeName == null ? null : new AccountIdentificationData(schemeName, identity.getIdValue(), identity.getSubIdOrType(), null);
    }

    static AccountIdentificationData create(AccountIdentification identification) {
        return identification == null ? null : new AccountIdentificationData(identification.getScheme(), identification.getIdentification(),
                identification.getSecondaryIdentification(), identification.getName());
    }

    @NotNull
    public AccountIdentification mapToEntity() {
        return new AccountIdentification(schemeName, identification, secondaryIdentification, name);
    }

    public String updateEntity(AccountIdentification identification) {
        if (identification == null)
            return null;
        if (!schemeName.equals(identification.getScheme()))
            return "Consent schemeName " + identification.getScheme() + " does not match requested schemeName " + schemeName;
        if (!this.identification.equals(identification.getIdentification()))
            return "Consent identification " + identification.getIdentification() + " does not match requested identification " + this.identification;
        if (secondaryIdentification == null ? identification.getSecondaryIdentification() != null : !secondaryIdentification.equals(identification.getSecondaryIdentification()))
            return "Consent secondaryIdentification " + identification.getSecondaryIdentification() + " does not match requested secondaryIdentification " + secondaryIdentification;
        if (name != null && !name.equals(identification.getName()))
            return "Consent name " + identification.getName() + " does not match requested name " + name;
        return null;
    }
}
