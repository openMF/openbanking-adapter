/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.psp.PspAccountResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalancesResponseDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private AccountsBalanceData data;

    BalancesResponseDto(@NotNull AccountsBalanceData data) {
        this.data = data;
    }

    public static BalancesResponseDto transform(@NotNull PspAccountResponseDto pspAccount) {
        AccountsBalanceData transform = AccountsBalanceData.transform(pspAccount);
        return new BalancesResponseDto(transform);
    }

    public static BalancesResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts, String accountId) {
        AccountsBalanceData transform = AccountsBalanceData.transform(pspAccounts, accountId);
        return new BalancesResponseDto(transform);
    }

    public static BalancesResponseDto transform(@NotNull PspAccountsResponseDto pspAccounts) {
        return transform(pspAccounts, null);
    }
}
