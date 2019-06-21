/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PartyLegalStructure {

    @JsonProperty("UK.OBIE.Individual")
    INDIVIDUAL,
    @JsonProperty("UK.OBIE.CIC")
    COMMUNITY_INTEREST_COMPANY,
    @JsonProperty("UK.OBIE.CIO")
    CHARITABLE_INCORPORATED_COMPANY,
    @JsonProperty("UK.OBIE.CoOp")
    CO_OPERATIVE,
    @JsonProperty("UK.OBIE.Charity")
    CHARITY,
    @JsonProperty("UK.OBIE.GeneralPartnership")
    GENERAL_PARTNERSHIP,
    @JsonProperty("UK.OBIE.LimitedLiabilityPartnership")
    LIMITED_LIABILITY_PARTNERSHIP,
    @JsonProperty("UK.OBIE.ScottishLimitedPartnership")
    SCOTTISH_LIMITED_PARTNERSHIP,
    @JsonProperty("UK.OBIE.LimitedPartnership")
    SCOTTISH_PARTNERSHIP,
    @JsonProperty("UK.OBIE.PrivateLimitedCompany")
    PRIVATE_LIMITED_COMPANY,
    @JsonProperty("UK.OBIE.PublicLimitedCompany")
    PUBLIC_LIMITED_COMPANY,
    @JsonProperty("UK.OBIE.Sole")
    SOLE,
    ;
}
