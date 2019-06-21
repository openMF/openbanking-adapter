/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.psp.PspAccountsGuarantorData;
import hu.dpc.ob.rest.dto.psp.PspAccountsLoanData;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsShareData;
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
public class AccountsData {

    @JsonProperty(value = "Account", required = true)
    private List<AccountData> accounts;

    public AccountsData(List<AccountData> accounts) {
        this.accounts = accounts;
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts, boolean detail, String accountId) {
        List<AccountData> accounts = new ArrayList<>();
        for (PspAccountsSavingsData pspAccount : pspAccounts.getSavingsAccounts()) {
            AccountData transform = AccountData.transform(pspAccount, detail, accountId);
            if (transform != null)
                accounts.add(transform);
        }
        for (PspAccountsLoanData pspAccount : pspAccounts.getLoanAccounts()) {
            AccountData transform = AccountData.transform(pspAccount, detail, accountId);
            if (transform != null)
                accounts.add(transform);
        }
        for (PspAccountsGuarantorData pspAccount : pspAccounts.getGuarantorAccounts()) {
            AccountData transform = AccountData.transform(pspAccount, detail, accountId);
            if (transform != null)
                accounts.add(transform);
        }
        for (PspAccountsShareData pspAccount : pspAccounts.getShareAccounts()) {
            AccountData transform = AccountData.transform(pspAccount, detail, accountId);
            if (transform != null)
                accounts.add(transform);
        }
        return new AccountsData(accounts);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts, boolean detail) {
        return transform(pspAccounts, detail, null);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts) {
        return transform(pspAccounts, false, null);
    }
}
