/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class FundsResponseData {


    @JsonProperty(value = "FundsAvailableResult")
    @Valid
    private FundsAvailableData fundsAvailable;

    @JsonProperty(value = "SupplementaryData")
    @Valid
    private SupplementaryData supplementaryData;

    FundsResponseData(@Valid FundsAvailableData fundsAvailable, @Valid SupplementaryData supplementaryData) {
        this.fundsAvailable = fundsAvailable;
        this.supplementaryData = supplementaryData;
    }

    @NotNull
    static FundsResponseData create(@NotNull LocalDateTime fundsDateTime, boolean fundsAvailable) {
        return new FundsResponseData(FundsAvailableData.create(fundsDateTime, fundsAvailable), null);
    }
}
