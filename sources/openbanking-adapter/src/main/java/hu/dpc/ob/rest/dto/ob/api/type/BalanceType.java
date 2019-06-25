/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BalanceType {

    @JsonProperty("ClosingAvailable")
    CLOSING_AVAILABLE,
    @JsonProperty("ClosingBooked")
    CLOSING_BOOKED,
    @JsonProperty("ClosingCleared")
    CLOSING_CLEARED,
    @JsonProperty("Expected")
    EXPECTED,
    @JsonProperty("ForwardAvailable")
    FORWARD_AVAILABLE,
    @JsonProperty("Information")
    INFORMATION,
    @JsonProperty("InterimAvailable")
    INTERIM_AVAILABLE,
    @JsonProperty("InterimBooked")
    INTERIM_BOOKED,
    @JsonProperty("InterimCleared")
    INTERIM_CLEARED,
    @JsonProperty("OpeningAvailable")
    OPENING_AVAILABLE,
    @JsonProperty("OpeningBooked")
    OPENING_BOOKED,
    @JsonProperty("OpeningCleared")
    OPENING_CLEARED,
    @JsonProperty("PreviouslyClosedBooked")
    PREVIOUSLY_CLOSED_BOOKED,
;
}
