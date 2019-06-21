/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PartyAccountRole {

    @JsonProperty("UK.OBIE.Principal")
    PRINCIPAL,
    @JsonProperty("UK.OBIE.SecondaryOwner")
    SECONDARY_OWNER,
    @JsonProperty("UK.OBIE.Beneficiary")
    BENEFICIARY,
    @JsonProperty("UK.OBIE.PowerOfAttorney")
    POWER_OF_ATTORNAY,
    @JsonProperty("UK.OBIE.LegalGuardian")
    LEGAL_GUARDIAN,
    @JsonProperty("UK.OBIE.SuccessorOnDeath")
    SUCCESSOR_ON_DEATH,
    @JsonProperty("UK.OBIE.Administrator")
    ADMINISTRATOR,
    @JsonProperty("UK.OBIE.OtherParty")
    OTHER_PARTY,
    @JsonProperty("UK.OBIE.Granter")
    GRANTER,
    @JsonProperty("UK.OBIE.Settlor")
    SETTLOR,
    @JsonProperty("UK.OBIE.SeniorManagingOfficial")
    SENIOR_MANAGING_OFFICIAL,
    @JsonProperty("UK.OBIE.Protector")
    PROTECTOR,
    @JsonProperty("UK.OBIE.RegisteredShareholderName")
    REGISTERED_SHAREHOLDER,
    ;
}
