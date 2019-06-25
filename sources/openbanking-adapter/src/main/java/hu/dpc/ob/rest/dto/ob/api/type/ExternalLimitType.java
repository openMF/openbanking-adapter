/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExternalLimitType {

    @JsonProperty("Available")
    AVAILABLE,
    @JsonProperty("Credit")
    CREDIT,
    @JsonProperty("Emergency")
    EMERGENCY,
    @JsonProperty("Pre-Agreed")
    PRE_AGREED,
    @JsonProperty("Temporary")
    TEMPORARY,
;
}
