/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.model.service.ConsentService;
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
public class AisApiConsentResponseData extends AisConsentResponseData {

    AisApiConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatusCode status,
                              @NotNull List<PermissionCode> permissions, LocalDateTime statusUpdateDateTime, LocalDateTime expirationDateTime,
                              LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(consentId, creationDateTime, status, permissions, statusUpdateDateTime, expirationDateTime, transactionFromDateTime,
                transactionToDateTime);
    }

    AisApiConsentResponseData(@NotNull String consentId, @NotNull LocalDateTime creationDateTime, @NotNull ConsentStatusCode status,
                              @NotNull List<PermissionCode> permissions) {
        this(consentId, creationDateTime, status, permissions, null, null, null, null);
    }

    @NotNull
    static AisApiConsentResponseData create(@NotNull Consent consent) {
        return new AisApiConsentResponseData(consent.getConsentId(), consent.getCreatedOn(), consent.getStatus(), ConsentService.getPermissions(consent),
                consent.getUpdatedOn(), consent.getExpiresOn(), ConsentService.getTransactionFromDateTime(consent), ConsentService.getTransactionToDateTime(consent));
    }
}
