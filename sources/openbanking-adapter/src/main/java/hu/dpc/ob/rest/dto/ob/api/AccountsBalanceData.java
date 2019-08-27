/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.psp.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountsBalanceData {

    @JsonProperty(value = "Balance", required = true)
    @NotNull
    private List<AccountBalanceData> balances;

    public AccountsBalanceData(@NotNull List<AccountBalanceData> balances) {
        this.balances = balances;
    }

    @NotNull
    public static AccountsBalanceData transform(@NotNull PspAccountResponseDto pspAccount) {
        List<AccountBalanceData> balances = AccountBalanceData.transform(pspAccount);
        return new AccountsBalanceData(balances);
    }

    @NotNull
    public static AccountsBalanceData transform(@NotNull PspAccountsResponseDto pspAccounts, String accountId) {
        List<AccountBalanceData> balances = new ArrayList<>();
        List<PspAccountsSavingsData> savingsAccounts = pspAccounts.getSavingsAccounts();
        if (savingsAccounts != null) {
            for (PspAccountsSavingsData pspAccount : savingsAccounts) {
                AccountBalanceData transform = AccountBalanceData.transform(pspAccount, accountId);
                if (transform != null)
                    balances.add(transform);
            }
        }
        List<PspAccountsLoanData> loanAccounts = pspAccounts.getLoanAccounts();
        if (loanAccounts != null) {
            for (PspAccountsLoanData pspAccount : loanAccounts) {
                AccountBalanceData transform = AccountBalanceData.transform(pspAccount, accountId);
                if (transform != null)
                    balances.add(transform);
            }
        }
        List<PspAccountsGuarantorData> guarantorAccounts = pspAccounts.getGuarantorAccounts();
        if (guarantorAccounts != null) {
            for (PspAccountsGuarantorData pspAccount : guarantorAccounts) {
                AccountBalanceData transform = AccountBalanceData.transform(pspAccount, accountId);
                if (transform != null)
                    balances.add(transform);
            }
        }
        List<PspAccountsShareData> shareAccounts = pspAccounts.getShareAccounts();
        if (shareAccounts != null) {
            for (PspAccountsShareData pspAccount : shareAccounts) {
                AccountBalanceData transform = AccountBalanceData.transform(pspAccount, accountId);
                if (transform != null)
                    balances.add(transform);
            }
        }
        return new AccountsBalanceData(balances);
    }

    @NotNull
    public static AccountsBalanceData transform(@NotNull PspAccountsResponseDto pspAccounts) {
        return transform(pspAccounts, null);
    }
}
