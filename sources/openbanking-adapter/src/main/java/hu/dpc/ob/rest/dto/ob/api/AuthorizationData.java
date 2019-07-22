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
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentAuthorization;
import hu.dpc.ob.domain.type.AuthorisationTypeCode;
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
public class AuthorizationData {

    @JsonProperty(value = "AuthorisationType", required = true)
    @NotNull
    private AuthorisationTypeCode authorisationType;

    @JsonProperty(value = "CompletionDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime completionDateTime;

    AuthorizationData(@NotNull AuthorisationTypeCode authorisationType, LocalDateTime completionDateTime) {
        this.authorisationType = authorisationType;
        this.completionDateTime = completionDateTime;
    }

    public PaymentAuthorization mapToEntity(@NotNull Payment payment) {
        return new PaymentAuthorization(payment, authorisationType, completionDateTime);
    }

    @NotNull
    public static AuthorizationData create(PaymentAuthorization authorization) {
        return authorization == null ? null : new AuthorizationData(authorization.getAuthorisationType(), authorization.getExpiresOn());
    }
}
