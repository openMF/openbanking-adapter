/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AddressTypeCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "address")
public final class Address extends AbstractEntity {

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type_code")
    private AddressTypeCode addressType;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 70)
    @Column(name = "department", length = 70)
    private String department;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 70)
    @Column(name = "sub_department", length = 70)
    private String subDepartment;

    @Setter(AccessLevel.PUBLIC)
    @NotNull
    @Column(name = "country", length = 2)
//    @JsonFormat(pattern = "^[A-Z]{2,2}$")
    private String country;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 35)
    @Column(name = "country_division1", length = 35)
    private String countryDivision1;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 35)
    @Column(name = "country_division2", length = 35)
    private String countryDivision2;

    @Setter(AccessLevel.PUBLIC)
    @NotNull
    @Size(max = 35)
    @Column(name = "town", length = 35)
    private String town;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 16)
    @Column(name = "postcode")
    private String postCode;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 70)
    @Column(name = "street", length = 70)
    private String street;

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 16)
    @Column(name = "building", length = 16)
    private String building;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "address")
    private List<AddressLine> addressLines = new ArrayList<>();

    public Address(AddressTypeCode addressType, @Size(max = 70) String department, @Size(max = 70) String subDepartment,
                   @NotNull String country, @Size(max = 35) String countryDivision1, @Size(max = 35) String countryDivision2,
                   @NotNull @Size(max = 35) String town, @Size(max = 16) String postCode, @Size(max = 70) String street,
                   @Size(max = 16) String building) {
        this.addressType = addressType;
        this.department = department;
        this.subDepartment = subDepartment;
        this.country = country;
        this.countryDivision1 = countryDivision1;
        this.countryDivision2 = countryDivision2;
        this.town = town;
        this.postCode = postCode;
        this.street = street;
        this.building = building;
    }

    public Address(AddressTypeCode addressType, @NotNull String country, @NotNull @Size(max = 35) String town, @Size(max = 16) String postCode,
                   @Size(max = 70) String street, @Size(max = 16) String building) {
        this(addressType, null, null, country, null, null, town, postCode, street, building);
    }

    public Address(@NotNull String country, @NotNull @Size(max = 35) String town) {
        this(null, country, town, null, null, null);
    }

    public AddressLine addLine(String line) {
        if (Strings.isEmpty(line))
            return null;

        AddressLine addressLine = new AddressLine(this, line);
        getAddressLines().add(addressLine);
        return addressLine;
    }

    public boolean removeLine(@NotNull AddressLine line) {
        line.setAddress(null);
        return getAddressLines().remove(line);
    }
}
