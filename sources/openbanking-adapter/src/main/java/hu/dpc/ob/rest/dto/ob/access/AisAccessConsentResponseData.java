/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.dto.ob.api.AisConsentResponseData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class AisAccessConsentResponseData extends AisConsentResponseData {

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

    AisAccessConsentResponseData(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull LocalDateTime creationDateTime,
                                 @NotNull ConsentStatusCode status, @NotNull List<PermissionCode> permissions, @NotNull List<ConsentAccountData> accounts,
                                 String userId, LocalDateTime statusUpdateDateTime, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                                 LocalDateTime transactionToDateTime) {
        super(consentId, creationDateTime, status, permissions, statusUpdateDateTime, expirationDateTime, transactionFromDateTime,
                transactionToDateTime);
        this.scope = scope;
        this.clientId = clientId;
        this.accounts = accounts;
        this.userId = userId;
    }

    AisAccessConsentResponseData(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull LocalDateTime creationDateTime,
                                 @NotNull ConsentStatusCode status, @NotNull List<PermissionCode> permissions, @NotNull List<ConsentAccountData> accounts) {
        this(consentId, scope, clientId, creationDateTime, status, permissions, accounts, null, null, null, null, null);
    }

    @NotNull
    static AisAccessConsentResponseData create(@NotNull Consent consent) {
        return new AisAccessConsentResponseData(consent.getConsentId(), consent.getScope(), consent.getClientId(), consent.getCreatedOn(),
                consent.getStatus(), ConsentService.getPermissions(consent), ConsentAccountData.create(consent),
                consent.getUser() == null ? null : consent.getUser().getApiUserId(), consent.getUpdatedOn(), consent.getExpiresOn(),
                ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
