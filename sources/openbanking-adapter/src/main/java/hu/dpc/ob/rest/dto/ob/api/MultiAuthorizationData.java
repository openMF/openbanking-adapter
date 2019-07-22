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
import hu.dpc.ob.domain.entity.PaymentAuthorization;
import hu.dpc.ob.domain.type.AuthStatusCode;
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
public class MultiAuthorizationData {

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private AuthStatusCode status;

    @JsonProperty(value = "NumberRequired")
    private Short numberRequired;

    @JsonProperty(value = "NumberReceived")
    private Short numberReceived;

    @JsonProperty(value = "LastUpdateDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime lastUpdateDateTime;

    @JsonProperty(value = "ExpirationDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expirationDateTime;

    public MultiAuthorizationData(@NotNull AuthStatusCode status, Short numberRequired, Short numberReceived, LocalDateTime lastUpdateDateTime,
                                  LocalDateTime expirationDateTime) {
        this.status = status;
        this.numberRequired = numberRequired;
        this.numberReceived = numberReceived;
        this.lastUpdateDateTime = lastUpdateDateTime;
        this.expirationDateTime = expirationDateTime;
    }

    @NotNull
    public static MultiAuthorizationData create(PaymentAuthorization authorization) {
        return authorization == null ? null : new MultiAuthorizationData(authorization.getStatus(), authorization.getNumberRequired(),
                authorization.getNumberReceived(), authorization.getUpdatedOn(), authorization.getExpiresOn());
    }
}
