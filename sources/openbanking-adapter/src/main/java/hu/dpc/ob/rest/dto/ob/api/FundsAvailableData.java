/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class FundsAvailableData {

    @JsonProperty(value = "FundsAvailableDateTime", required = true)
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    @NotNull
    private LocalDateTime fundsDateTime;

    @JsonProperty(value = "FundsAvailable")
    private boolean fundsAvailable;

    public FundsAvailableData(@NotNull LocalDateTime fundsDateTime, boolean fundsAvailable) {
        this.fundsDateTime = fundsDateTime;
        this.fundsAvailable = fundsAvailable;
    }

    @NotNull
    static FundsAvailableData create(@NotNull LocalDateTime fundsDateTime, boolean fundsAvailable) {
        return new FundsAvailableData(fundsDateTime, fundsAvailable);
    }
}
