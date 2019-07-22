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
import hu.dpc.ob.domain.entity.Payment;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
/** OBPostalAddress6 */
public class PostalAddressData extends AddressData {

    @JsonProperty(value = "AddressType")
    private AddressTypeCode addressType;

    @JsonProperty(value = "Department")
    @Size(max = 70)
    private String department;

    @JsonProperty(value = "SubDepartment")
    @Size(max = 70)
    private String subDepartment;

    @JsonProperty(value = "TownName")
    @Size(max = 35)
    private String townName;

    @JsonProperty(value = "Country")
    @JsonFormat(pattern = "^[A-Z]{2,2}$")
    private String country;

    @JsonProperty(value = "CountrySubDivision")
    @Size(max = 35)
    private String countrySubDivision;

    @JsonProperty(value = "AddressLine")
    @Size(max = 7)
    private @Size(max = 70) String[] addressLines;

    public PostalAddressData(@Size(max = 70) String streetName, @Size(max = 16) String buildingNumber, @Size(max = 16) String postCode,
                             AddressTypeCode addressType, @Size(max = 70) String department, @Size(max = 70) String subDepartment,
                             @Size(max = 35) String townName, String country, @Size(max = 35) String countrySubDivision,
                             @Size(max = 7) @Size(max = 70) String[] addressLines) {
        super(streetName, buildingNumber, postCode);
        this.addressType = addressType;
        this.department = department;
        this.subDepartment = subDepartment;
        this.townName = townName;
        this.country = country;
        this.countrySubDivision = countrySubDivision;
        this.addressLines = addressLines;
    }

    static PostalAddressData create(Address address) {
        if (address == null)
            return null;
        List<AddressLine> lines = address.getAddressLines();
        String[] addressLines = lines.isEmpty() ? null : lines.stream().map(AddressLine::getLine).toArray(String[]::new);

        return new PostalAddressData(address.getStreet(), address.getBuilding(), address.getPostCode(), address.getAddressType(),
                address.getDepartment(), address.getSubDepartment(), address.getTown(), address.getCountry(), address.getCountryDivision1(),
                addressLines);
    }

    public Address mapToEntity() {
        Address address = new Address(AddressTypeCode.POSTAL, department, subDepartment, country, countrySubDivision, null,
                townName, getPostCode(), getStreetName(), getBuildingNumber());
        if (addressLines != null) {
            for (String addressLine : addressLines) {
                address.addLine(addressLine);
            }
        }
        return address;
    }

    String updateEntity(@NotNull Payment payment) {
        Address postalAddress = payment.getCreditorPostalAddress();
        if (postalAddress == null) {
            payment.setCreditorPostalAddress(mapToEntity());
            return null;
        }
        super.updateEntity(postalAddress);

        if (addressType != null)
            postalAddress.setAddressType(addressType);
        if (department != null)
            postalAddress.setDepartment(department);
        if (subDepartment != null)
            postalAddress.setSubDepartment(subDepartment);
        if (townName != null)
            postalAddress.setTown(townName);
        if (country != null)
            postalAddress.setCountry(country);
        if (countrySubDivision != null)
            postalAddress.setCountryDivision1(countrySubDivision);
        if (addressLines != null) {
            List<AddressLine> lines = postalAddress.getAddressLines();
            int size = lines.size();
            for (int i = 0; i < addressLines.length; i++) {
                String addressLine = addressLines[i];
                if (size > i)
                    lines.get(i).setLine(addressLine);
                else
                    postalAddress.addLine(addressLine);
            }
            if (size > addressLines.length) {
                for (int i = size; --i >= addressLines.length;)
                    postalAddress.removeLine(lines.get(i));
            }
        }
        return null;
    }
}
