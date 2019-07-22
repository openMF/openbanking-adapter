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
import hu.dpc.ob.domain.entity.Address;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
/** PostalAddress18 */
public class AddressData {

    @JsonProperty(value = "StreetName")
    @Size(max = 70)
    private String streetName;

    @JsonProperty(value = "BuildingNumber")
    @Size(max = 16)
    private String buildingNumber;

    @JsonProperty(value = "PostCode")
    @Size(max = 16)
    private String postCode;

    public AddressData(@Size(max = 70) String streetName, @Size(max = 16) String buildingNumber, @Size(max = 16) String postCode) {
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.postCode = postCode;
    }

    String updateEntity(@NotNull Address address) {
        if (streetName != null)
            address.setStreet(streetName);
        if (buildingNumber != null)
            address.setBuilding(buildingNumber);
        if (postCode != null)
            address.setPostCode(postCode);
        return null;
    }}
