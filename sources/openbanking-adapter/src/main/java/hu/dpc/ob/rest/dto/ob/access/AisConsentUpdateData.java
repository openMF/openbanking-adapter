/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.domain.type.PermissionCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class AisConsentUpdateData extends ConsentUpdateData {

    @JsonProperty(value = "Permissions")
    List<PermissionCode> permissions;

    @JsonProperty(value = "Account")
    List<ConsentAccountData> accounts;

    public AisConsentUpdateData(@NotEmpty @Size(max = 128) String consentId, @NotNull ConsentActionCode action, String reasonCode,
                                String reasonDesc, List<PermissionCode> permissions, List<ConsentAccountData> accounts) {
        super(consentId, action, reasonCode, reasonDesc);
        this.permissions = permissions;
        this.accounts = accounts;
    }
}
