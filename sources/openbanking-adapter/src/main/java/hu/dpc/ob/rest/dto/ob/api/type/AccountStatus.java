/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountStatus {

    @JsonProperty("Enabled")
    ENABLED,
    @JsonProperty("Disabled")
    DISABLED,
    @JsonProperty("Deleted")
    DELETED,
    @JsonProperty("ProForma")
    PROFORMA,
    @JsonProperty("Pending")
    PENDING,
;

    public boolean isEnabled() {
        return this == ENABLED;
    }
}
