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
import java.util.Map;

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
    public static AccountsData transform(@NotNull PspAccountResponseDto pspAccount, boolean detail) {
        List<AccountData> accounts = new ArrayList<>(1);
        AccountData transform = AccountData.transform(pspAccount, detail);
        accounts.add(transform);
        return new AccountsData(accounts);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts, Map<String, PspIdentifiersResponseDto> idMap, boolean detail, String accountId) {
        List<AccountData> accounts = new ArrayList<>();
        List<PspAccountsSavingsData> savingsAccounts = pspAccounts.getSavingsAccounts();
        if (savingsAccounts != null) {
            for (PspAccountsSavingsData pspAccount : savingsAccounts) {
                PspIdentifiersResponseDto identities = detail && idMap != null ? idMap.get(accountId == null ? pspAccount.getExternalId() : accountId) : null;
                AccountData transform = AccountData.transform(pspAccount, identities, detail, accountId);
                if (transform != null)
                    accounts.add(transform);
            }
        }
        List<PspAccountsLoanData> loanAccounts = pspAccounts.getLoanAccounts();
        if (loanAccounts != null) {
            for (PspAccountsLoanData pspAccount : loanAccounts) {
                PspIdentifiersResponseDto identities = detail && idMap != null ? idMap.get(accountId == null ? pspAccount.getExternalId() : accountId) : null;
                AccountData transform = AccountData.transform(pspAccount, identities, detail, accountId);
                if (transform != null)
                    accounts.add(transform);
            }
        }
        List<PspAccountsGuarantorData> guarantorAccounts = pspAccounts.getGuarantorAccounts();
        if (guarantorAccounts != null) {
            for (PspAccountsGuarantorData pspAccount : guarantorAccounts) {
                PspIdentifiersResponseDto identities = detail && idMap != null ? idMap.get(accountId == null ? pspAccount.getExternalId() : accountId) : null;
                AccountData transform = AccountData.transform(pspAccount, identities, detail, accountId);
                if (transform != null)
                    accounts.add(transform);
            }
        }
        List<PspAccountsShareData> shareAccounts = pspAccounts.getShareAccounts();
        if (shareAccounts != null) {
            for (PspAccountsShareData pspAccount : shareAccounts) {
                PspIdentifiersResponseDto identities = detail && idMap != null ? idMap.get(accountId == null ? pspAccount.getExternalId() : accountId) : null;
                AccountData transform = AccountData.transform(pspAccount, identities, detail, accountId);
                if (transform != null)
                    accounts.add(transform);
            }
        }
        return new AccountsData(accounts);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts, Map<String, PspIdentifiersResponseDto> idMap, boolean detail) {
        return transform(pspAccounts, idMap, detail, null);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts, boolean detail) {
        return transform(pspAccounts, null, detail, null);
    }

    @NotNull
    public static AccountsData transform(@NotNull PspAccountsResponseDto pspAccounts) {
        return transform(pspAccounts, null, false, null);
    }
}
