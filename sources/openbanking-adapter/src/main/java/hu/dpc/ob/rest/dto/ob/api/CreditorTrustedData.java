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

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditorTrustedData {


    @JsonProperty(value = "Limit")
    @Digits(integer = 23, fraction = 5) // OB: (18,5), interoperation: (22,4), CN: (15,5), 1.2: (19,6)! -> we support (23,5)
    private BigDecimal limit;

    @JsonProperty(value = "ExpirationDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expirationDateTime;

    public CreditorTrustedData(@Digits(integer = 23, fraction = 5) BigDecimal limit, LocalDateTime expirationDateTime) {
        this.limit = limit;
        this.expirationDateTime = expirationDateTime;
    }
}
