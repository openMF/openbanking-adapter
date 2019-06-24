/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.ob.api.type.IdentificationType;
import hu.dpc.ob.rest.dto.psp.PspIdentifierData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountIdentificationData {

    @JsonProperty(value = "SchemeName", required = true)
    @NotEmpty
    @Length(max = 40)
    private IdentificationType schemeName;

    @JsonProperty(value = "Identification", required = true)
    @NotEmpty
    @Length(max = 256)
    private String identification;

    @JsonProperty(value = "SecondaryIdentification")
    @Length(max = 34)
    private String secondaryIdentification;

    @JsonProperty(value = "Name")
    @Length(max = 70)
    private String name;


    public AccountIdentificationData(@NotEmpty @Length(max = 40) IdentificationType schemeName, @NotEmpty @Length(max = 256) String identification,
                                     @Length(max = 34) String secondaryIdentification, @Length(max = 70) String name) {
        this.schemeName = schemeName;
        this.identification = identification;
        this.name = name;
        this.secondaryIdentification = secondaryIdentification;
    }

    public AccountIdentificationData(@NotEmpty @Length(max = 40) IdentificationType schemeName, @NotEmpty @Length(max = 256) String identification,
                                     @Length(max = 34) String secondaryIdentification) {
        this(schemeName, identification, secondaryIdentification, null);
    }

    static AccountIdentificationData transform(PspIdentifierData identity) {
        if (identity == null)
            return null;

        IdentificationType schemeName = IdentificationType.forInteropIdType(identity.getIdType());
        return schemeName == null ? null : new AccountIdentificationData(schemeName, identity.getIdValue(), identity.getSubIdOrType(), null);
    }
}
