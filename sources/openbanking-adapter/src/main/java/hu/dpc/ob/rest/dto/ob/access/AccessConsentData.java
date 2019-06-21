/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.rest.dto.ob.api.ConsentResponseData;
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
public class AccessConsentData extends ConsentResponseData {

    @JsonProperty(value = "Scope", required = true)
    @NotNull
    private ApiScope scope;

    @JsonProperty(value = "ClientId", required = true)
    @NotEmpty
    private String clientId;

    @JsonProperty(value = "Account", required = true)
    List<ConsentAccountData> accounts;

    @JsonProperty(value = "UserId")
    private String userId;

    AccessConsentData(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull LocalDateTime creationDateTime,
                             @NotNull ConsentStatus status, @NotNull List<ApiPermission> permissions, @NotNull List<ConsentAccountData> accounts,
                             String userId, LocalDateTime statusUpdateDateTime, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                             LocalDateTime transactionToDateTime) {
        super(consentId, creationDateTime, status, permissions, statusUpdateDateTime, expirationDateTime, transactionFromDateTime,
                transactionToDateTime);
        this.scope = scope;
        this.clientId = clientId;
        this.accounts = accounts;
        this.userId = userId;
    }

    AccessConsentData(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull LocalDateTime creationDateTime,
                             @NotNull ConsentStatus status, @NotNull List<ApiPermission> permissions, @NotNull List<ConsentAccountData> accounts) {
        this(consentId, scope, clientId, creationDateTime, status, permissions, accounts, null, null, null, null, null);
    }

    @NotNull
    static AccessConsentData create(@NotNull Consent consent) {
        return new AccessConsentData(consent.getConsentId(), consent.getScope(), consent.getClientId(), consent.getCreatedOn(),
                consent.getStatus(), ConsentService.getPermissions(consent), ConsentAccountData.create(consent), consent.getUser().getApiUserId(),
                consent.getUpdatedOn(), consent.getExpiresOn(), ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
