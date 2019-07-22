/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.ob.api.type.ExternalLimitType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditLineData {

    @JsonProperty(value = "Included", required = true)
    private boolean included; // Indicates whether or not the credit line is included in the balance of the accountId.

    @JsonProperty(value = "Amount")
    @NotNull
    @Valid
    private AmountData amount;

    @JsonProperty(value = "Type")
    private ExternalLimitType type;

    CreditLineData(boolean included, AmountData amount, ExternalLimitType type) {
        this.included = included;
        this.amount = amount;
        this.type = type;
    }
}
