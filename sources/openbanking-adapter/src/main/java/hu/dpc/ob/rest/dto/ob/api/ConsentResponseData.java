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
public class ConsentResponseData extends ConsentData {

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Length(max = 128)
    private String consentId;

    @JsonProperty(value = "CreationDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDateTime;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private ConsentStatus status;

    @JsonProperty(value = "StatusUpdateDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime statusUpdateDateTime;

    protected ConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatus status,
                               @NotNull List<ApiPermission> permissions, LocalDateTime statusUpdateDateTime, LocalDateTime expirationDateTime,
                               LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(permissions, expirationDateTime, transactionFromDateTime, transactionToDateTime);
        this.status = status;
        this.consentId = consentId;
        this.creationDateTime = creationDateTime;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime == null ? creationDateTime : statusUpdateDateTime;
    }

    protected ConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatus status,
                               @NotNull List<ApiPermission> permissions) {
        this(consentId, creationDateTime, status, permissions, null, null, null, null);
    }

    @NotNull
    static ConsentResponseData create(@NotNull Consent consent) {
        return new ConsentResponseData(consent.getConsentId(), consent.getCreatedOn(), consent.getStatus(), ConsentService.getPermissions(consent),
                consent.getUpdatedOn(), consent.getExpiresOn(), ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
