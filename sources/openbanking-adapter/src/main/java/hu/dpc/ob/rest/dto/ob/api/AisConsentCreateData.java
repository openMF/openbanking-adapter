/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import hu.dpc.ob.domain.type.PermissionCode;
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
public class AisConsentCreateData extends AisConsentData {

    AisConsentCreateData(@NotNull List<PermissionCode> permissions, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(permissions, expirationDateTime, transactionFromDateTime, transactionToDateTime);
    }

    AisConsentCreateData(@NotNull List<PermissionCode> permissions) {
        this(permissions, null, null, null);
    }

}
