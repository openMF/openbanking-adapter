/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountsResponseDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private AccountsData data;

    AccountsResponseDto(@NotNull AccountsData data) {
        this.data = data;
    }

    public static AccountsResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts, boolean detail, String accountId) {
        AccountsData transform = AccountsData.transform(pspAccounts, detail, accountId);
        return new AccountsResponseDto(transform);
    }

    public static AccountsResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts, boolean detail) {
        return transform(pspAccounts, detail, null);
    }

    public static AccountsResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts) {
        return transform(pspAccounts, false);
    }
}
