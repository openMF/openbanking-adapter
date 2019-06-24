/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class FineractXLoginResponseDto implements PspLoginResponseDto {

    private String username;
    private Long userId;
    private String base64EncodedAuthenticationKey;

    private boolean authenticated;
    private boolean shouldRenewPassword;
    @JsonProperty(value = "isTwoFactorAuthenticationRequired")
    private boolean isTwoFactorAuthenticationRequired;

    private Long officeId;

    private String officeName;

    private Collection<FineractXLoginRoleData> roles;

    private Collection<String> permissions;

    @Override
    public String getAccessToken() {
        return base64EncodedAuthenticationKey;
    }

    @Override
    public Date getAccessTokenExpiration() {
        return shouldRenewPassword ? new Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000) : null; //TODO: TIMEZONE
    }
}
