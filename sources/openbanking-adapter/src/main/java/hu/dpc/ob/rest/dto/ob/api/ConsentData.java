/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.service.ConsentService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ConsentData {

    @JsonProperty(value = "Permissions", required = true)
    private List<ApiPermission> permissions;

    @JsonProperty(value = "ExpirationDateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expirationDateTime;

    @JsonProperty(value = "TransactionFromDateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime transactionFromDateTime;

    @JsonProperty(value = "TransactionToDateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime transactionToDateTime;

    protected ConsentData(@NotNull List<ApiPermission> permissions, LocalDateTime expirationDateTime,
                       LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        this.permissions = permissions;
        this.expirationDateTime = expirationDateTime;
        this.transactionFromDateTime = transactionFromDateTime;
        this.transactionToDateTime = transactionToDateTime;
    }

    protected ConsentData(@NotNull List<ApiPermission> permissions) {
        this(permissions, null, null, null);
    }

    @NotNull
    static ConsentData create(@NotNull Consent consent) {
        return new ConsentData(ConsentService.getPermissions(consent), consent.getExpiresOn(),
                ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
