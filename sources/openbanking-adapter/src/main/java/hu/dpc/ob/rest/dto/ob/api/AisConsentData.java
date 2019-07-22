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
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class AisConsentData {

    @JsonProperty(value = "Permissions", required = true)
    private List<PermissionCode> permissions;

    @JsonProperty(value = "ExpirationDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expirationDateTime;

    @JsonProperty(value = "TransactionFromDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime transactionFromDateTime;

    @JsonProperty(value = "TransactionToDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime transactionToDateTime;

    protected AisConsentData(@NotNull List<PermissionCode> permissions, LocalDateTime expirationDateTime,
                             LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        this.permissions = permissions;
        this.expirationDateTime = expirationDateTime;
        this.transactionFromDateTime = transactionFromDateTime;
        this.transactionToDateTime = transactionToDateTime;
    }

    protected AisConsentData(@NotNull List<PermissionCode> permissions) {
        this(permissions, null, null, null);
    }

    @NotNull
    static AisConsentData create(@NotNull Consent consent) {
        return new AisConsentData(ConsentService.getPermissions(consent), consent.getExpiresOn(),
                ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
