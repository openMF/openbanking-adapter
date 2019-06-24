/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PspClientResponseDto {

    private Long id;
    private String accountNo;
    private String externalId;

    private EnumOptionData status;
    private CodeValueData subStatus;

    private Boolean active;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activationDate;

    private String firstname;
    private String middlename;
    private String lastname;
    private String fullname;
    private String displayName;
    private String mobileNo;
    private String emailAddress;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;
    private CodeValueData gender;
    private CodeValueData clientType;
    private CodeValueData clientClassification;
    private Boolean isStaff;

    private Long officeId;
    private String officeName;
    private Long transferToOfficeId;
    private String transferToOfficeName;

    private Long imageId;
    private Boolean imagePresent;
    private Long staffId;
    private String staffName;
//    private ClientTimelineData timeline;

    private Long savingsProductId;
    private String savingsProductName;

    private Long savingsAccountId;
    private EnumOptionData legalForm;

    // associations
//    private Collection<GroupGeneralData> groups;

    // template
//    private Collection<OfficeData> officeOptions;
//    private Collection<StaffData> staffOptions;
    private Collection<CodeValueData> narrations;
//    private Collection<SavingsProductData> savingProductOptions;
//    private Collection<SavingsAccountData> savingAccountOptions;
    private Collection<CodeValueData> genderOptions;
    private Collection<CodeValueData> clientTypeOptions;
    private Collection<CodeValueData> clientClassificationOptions;
    private Collection<CodeValueData> clientNonPersonConstitutionOptions;
    private Collection<CodeValueData> clientNonPersonMainBusinessLineOptions;
    private List<EnumOptionData> clientLegalFormOptions;
//    private ClientFamilyMembersData familyMemberOptions;

//    private ClientNonPersonData clientNonPersonDetails;

//    private Collection<AddressData> address;

    private Boolean isAddressEnabled;


//    private List<DatatableData> datatables;

    //import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private Long clientTypeId;
    private Long genderId;
    private Long clientClassificationId;
    private Long legalFormId;
    private LocalDate submittedOnDate;
}
