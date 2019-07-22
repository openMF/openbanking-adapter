/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Address;
import hu.dpc.ob.domain.entity.AddressLine;
import hu.dpc.ob.domain.entity.PaymentRisk;
import hu.dpc.ob.domain.type.AddressTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
/** PostalAddress18 */
public class DeliveryAddressData extends AddressData {

    @JsonProperty(value = "AddressLine")
    @Size(max = 2)
    private @Size(max = 70) String[] addressLines;

    @JsonProperty(value = "TownName", required = true)
    @Size(max = 35)
    private String townName;

    @JsonProperty(value = "Country", required = true)
    @JsonFormat(pattern = "^[A-Z]{2,2}$")
    private String country;

    @JsonProperty(value = "CountrySubDivision")
    @Size(max = 2)
    private @Size(max = 35) String[] countrySubDivisions;

    public DeliveryAddressData(@Size(max = 70) String streetName, @Size(max = 16) String buildingNumber, @Size(max = 16) String postCode,
                               @Size(max = 2) @Size(max = 70) String[] addressLines, @Size(max = 35) String townName, String country,
                               @Size(max = 2) @Size(max = 35) String[] countrySubDivisions) {
        super(streetName, buildingNumber, postCode);
        this.addressLines = addressLines;
        this.townName = townName;
        this.country = country;
        this.countrySubDivisions = countrySubDivisions;
    }

    static DeliveryAddressData create(Address address) {
        if (address == null)
            return null;

        List<AddressLine> lines = address.getAddressLines();
        String[] addressLines = lines.isEmpty() ? null : lines.stream().map(AddressLine::getLine).toArray(String[]::new);

        String[] subDivisions = null;
        @Size(max = 35) String countryDivision1 = address.getCountryDivision1();
        @Size(max = 35) String countryDivision2 = address.getCountryDivision2();
        if (countryDivision2 != null) {
            subDivisions = new String[2];
            subDivisions[2] = countryDivision2;
            subDivisions[1] = countryDivision1;
        }
        else if (countryDivision1 != null) {
            subDivisions = new String[1];
            subDivisions[1] = countryDivision1;
        }

        return new DeliveryAddressData(address.getStreet(), address.getBuilding(), address.getPostCode(), addressLines,
                address.getTown(), address.getCountry(), subDivisions);
    }

    public Address mapToEntity() {
        Address address = new Address(AddressTypeCode.DELIVERY_TO, country, townName, getPostCode(), getStreetName(), getBuildingNumber());
        if (countrySubDivisions != null) {
            if (countrySubDivisions.length > 0)
                address.setCountryDivision1(countrySubDivisions[0]);
            if (countrySubDivisions.length > 1)
                address.setCountryDivision2(countrySubDivisions[1]);
        }
        if (addressLines != null) {
            for (String addressLine : addressLines) {
                address.addLine(addressLine);
            }
        }
        return address;
    }

    String updateEntity(@NotNull PaymentRisk risk) {
        Address address = risk.getDeliveryAddress();
        if (address == null) {
            risk.setDeliveryAddress(mapToEntity());
            return null;
        }
        super.updateEntity(address);

        if (townName != null)
            address.setTown(townName);
        if (country != null)
            address.setCountry(country);
        if (countrySubDivisions != null) {
            if (countrySubDivisions.length > 0)
                address.setCountryDivision1(countrySubDivisions[0]);
            else {
                address.setCountryDivision1(null);
                address.setCountryDivision2(null);
            }

            if (countrySubDivisions.length > 1)
                address.setCountryDivision2(countrySubDivisions[1]);
            else if (countrySubDivisions.length > 0)
                address.setCountryDivision2(null);
        }
        if (addressLines != null) {
            List<AddressLine> lines = address.getAddressLines();
            int size = lines.size();
            for (int i = 0; i < addressLines.length; i++) {
                String addressLine = addressLines[i];
                if (size > i)
                    lines.get(i).setLine(addressLine);
                else
                    address.addLine(addressLine);
            }
            if (size > addressLines.length) {
                for (int i = size; --i >= addressLines.length;)
                    address.removeLine(lines.get(i));
            }
        }
        return null;
    }
}
