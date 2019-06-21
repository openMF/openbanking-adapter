/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.domain.type.ApiPermission;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ConsentUpdateData {

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Length(max = 128)
    private String consentId;

    @JsonProperty(value = "Action", required = true)
    @NotNull
    private ConsentActionType action;

    @JsonProperty(value = "Permissions", required = true)
    List<ApiPermission> permissions;

    @JsonProperty(value = "Account", required = true)
    List<ConsentAccountData> accounts;

    @JsonProperty(value = "ReasonCode")
    private String reasonCode;

    @JsonProperty(value = "ReasonDesc")
    private String reasonDesc;

    public ConsentUpdateData(@NotNull String consentId, @NotNull ConsentActionType action, @NotNull List<ApiPermission> permissions,
                             @NotNull List<ConsentAccountData> accounts, String reasonCode, String reasonDesc) {
        this.consentId = consentId;
        this.action = action;
        this.permissions = permissions;
        this.accounts = accounts;
        this.reasonCode = reasonCode;
        this.reasonCode = reasonCode;
    }

    public ConsentUpdateData(@NotNull String consentId, @NotNull ConsentActionType action, @NotNull List<ApiPermission> permissions,
                             @NotNull List<ConsentAccountData> accounts) {
        this(consentId, action, permissions, accounts, null, null);
    }
}
