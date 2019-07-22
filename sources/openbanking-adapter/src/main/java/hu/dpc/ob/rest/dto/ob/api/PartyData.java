/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.ob.api.type.PartyAccountRole;
import hu.dpc.ob.rest.dto.ob.api.type.PartyLegalStructure;
import hu.dpc.ob.rest.dto.ob.api.type.PartyType;
import hu.dpc.ob.rest.dto.psp.PspClientResponseDto;
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
@SuppressWarnings("unused")
public class PartyData {

    @JsonProperty(value = "PartyId", required = true)
    @NotEmpty
    @Size(max = 40)
    private String partyId;

    @JsonProperty(value = "PartyType")
    private PartyType partyType;

    @JsonProperty(value = "Name")
    @Size(max = 70)
    private String name;

    @JsonProperty(value = "FullLegalName")
    @Size(max = 350)
    private String fullLegalName;

    @JsonProperty(value = "LegalStructure")
    private PartyLegalStructure legalStructure;

    @JsonProperty(value = "BeneficialOwnership")
    private boolean beneficialOwnership;

    @JsonProperty(value = "AccountRole")
    private PartyAccountRole accountRole;

    @JsonProperty(value = "EmailAddress")
    @Size(max = 256)
    private String emailAddress;

    @JsonProperty(value = "Phone")
    @JsonFormat(pattern = "\\+[0-9]{1,3}-[0-9()+\\-]{1,30}")
    private String phone;

    @JsonProperty(value = "Mobile")
    @JsonFormat(pattern = "\\+[0-9]{1,3}-[0-9()+\\-]{1,30}")
    private String mobile;

//    Relationships RelationshipsObject;
//    ArrayList < Object > DeliveryAddressData = new ArrayList < Object > ();


    public PartyData(@NotEmpty @Size(max = 40) String partyId, PartyType partyType, @Size(max = 70) String name,
                     @Size(max = 350) String fullLegalName, PartyLegalStructure legalStructure, boolean beneficialOwnership,
                     PartyAccountRole accountRole, @Size(max = 256) String emailAddress, String phone, String mobile) {
        this.partyId = partyId;
        this.partyType = partyType;
        this.name = name;
        this.fullLegalName = fullLegalName;
        this.legalStructure = legalStructure;
        this.beneficialOwnership = beneficialOwnership;
        this.accountRole = accountRole;
        this.emailAddress = emailAddress;
        this.phone = phone;
        this.mobile = mobile;
    }

    public PartyData(@NotEmpty @Size(max = 40) String partyId) {
        this(partyId, null, null, null, null, false, null, null, null, null);
    }

    @NotNull
    static PartyData transform(@NotNull PspClientResponseDto pspClient) {
        return new PartyData(String.valueOf(pspClient.getId()), null, pspClient.getDisplayName(), pspClient.getFullname(), null,
                false, null, pspClient.getEmailAddress(), null, pspClient.getMobileNo());
    }
}
