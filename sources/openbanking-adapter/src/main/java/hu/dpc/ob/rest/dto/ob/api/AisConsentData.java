/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.service.ConsentService;
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
public class AisConsentData extends ConsentResponseData {

    AisConsentData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatus status,
                   @NotNull LocalDateTime statusUpdateDateTime, @NotNull List<ApiPermission> permissions, LocalDateTime expirationDateTime,
                   LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(consentId, creationDateTime, status, permissions, statusUpdateDateTime, expirationDateTime, transactionFromDateTime,
                transactionToDateTime);
    }

    AisConsentData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatus status,
                   @NotNull LocalDateTime statusUpdateDateTime, @NotNull List<ApiPermission> permissions) {
        this(consentId, creationDateTime, status, statusUpdateDateTime, permissions, null, null, null);
    }

    @NotNull
    static AisConsentData create(@NotNull Consent consent) {
        return new AisConsentData(consent.getConsentId(), consent.getCreatedOn(), consent.getStatus(), consent.getUpdatedOn(),
                ConsentService.getPermissions(consent), consent.getExpiresOn(), ConsentService.getTransactionFromDateTime(consent),
                ConsentService.getTransactionToDateTime(consent));
    }
}
