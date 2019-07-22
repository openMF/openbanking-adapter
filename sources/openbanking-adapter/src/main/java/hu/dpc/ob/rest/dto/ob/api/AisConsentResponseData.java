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
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class AisConsentResponseData extends AisConsentData {

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Size(max = 128)
    private String consentId;

    @JsonProperty(value = "CreationDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime creationDateTime;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private ConsentStatusCode status;

    @JsonProperty(value = "StatusUpdateDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime statusUpdateDateTime;

    protected AisConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatusCode status,
                                     @NotNull List<PermissionCode> permissions, LocalDateTime statusUpdateDateTime, LocalDateTime expirationDateTime,
                                     LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(permissions, expirationDateTime, transactionFromDateTime, transactionToDateTime);
        this.status = status;
        this.consentId = consentId;
        this.creationDateTime = creationDateTime;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime == null ? creationDateTime : statusUpdateDateTime;
    }

    protected AisConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatusCode status,
                                     @NotNull List<PermissionCode> permissions) {
        this(consentId, creationDateTime, status, permissions, null, null, null, null);
    }

    @NotNull
    static AisConsentResponseData create(@NotNull Consent consent) {
        return new AisConsentResponseData(consent.getConsentId(), consent.getCreatedOn(), consent.getStatus(), ConsentService.getPermissions(consent),
                consent.getUpdatedOn(), consent.getExpiresOn(), ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
