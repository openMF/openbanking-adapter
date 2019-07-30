/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.type.ConsentActionCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ConsentUpdateData {

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Size(max = 128)
    private String consentId;

    @JsonProperty(value = "Action", required = true)
    @NotNull
    private ConsentActionCode action;

    @JsonProperty(value = "ReasonCode")
    private String reasonCode;

    @JsonProperty(value = "ReasonDesc")
    private String reasonDesc;

    public ConsentUpdateData(@NotEmpty @Size(max = 128) String consentId, @NotNull ConsentActionCode action, String reasonCode, String reasonDesc) {
        this.consentId = consentId;
        this.action = action;
        this.reasonCode = reasonCode;
        this.reasonDesc = reasonDesc;
    }

    public boolean isAuthorize() {
        return action == ConsentActionCode.AUTHORIZE;
    }
}
