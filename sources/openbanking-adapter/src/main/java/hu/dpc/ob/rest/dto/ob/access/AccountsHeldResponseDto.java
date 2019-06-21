/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.ob.api.AccountsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountsHeldResponseDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private AccountsData data;

    AccountsHeldResponseDto(@NotNull AccountsData data) {
        this.data = data;
    }

    public static AccountsHeldResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts) {
        AccountsData transform = AccountsData.transform(pspAccounts);
        return new AccountsHeldResponseDto(transform);
    }
}
