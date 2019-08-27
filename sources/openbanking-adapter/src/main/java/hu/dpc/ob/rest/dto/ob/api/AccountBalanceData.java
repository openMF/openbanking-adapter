/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.rest.dto.ob.api.type.BalanceType;
import hu.dpc.ob.rest.dto.ob.api.type.CreditDebitType;
import hu.dpc.ob.rest.dto.psp.*;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountBalanceData {

    @JsonProperty(value = "AccountId", required = true)
    @NotEmpty
    @Size(max = 40)
    private String accountId;

    @JsonProperty(value = "Amount", required = true)
    @NotNull
    @Valid
    private AmountData amount;

    @JsonProperty(value = "CreditDebitIndicator", required = true)
    @NotNull
    private CreditDebitType creditDebitIndicator;

    @JsonProperty(value = "Type", required = true)
    @NotNull
    private BalanceType type;

    @JsonProperty(value = "DateTime", required = true)
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    @NotNull
    private LocalDateTime dateTime; // Indicates the date (and time) of the balance.

    @JsonProperty(value = "CreditLine", required = true)
    private List<CreditLineData> creditLine;

    AccountBalanceData(@NotEmpty @Size(max = 40) String accountId, @NotNull AmountData amount, @NotNull CreditDebitType creditDebitIndicator,
                              @NotNull BalanceType type, @NotNull LocalDateTime dateTime, List<CreditLineData> creditLine) {
        this.accountId = accountId;
        this.amount = amount;
        this.creditDebitIndicator = creditDebitIndicator;
        this.type = type;
        this.dateTime = dateTime;
        this.creditLine = creditLine;
    }

    AccountBalanceData(@NotEmpty @Size(max = 40) String accountId, @NotNull AmountData amount, @NotNull CreditDebitType creditDebitIndicator,
                              @NotNull BalanceType type, @NotNull LocalDateTime dateTime) {
        this(accountId, amount, creditDebitIndicator, type, dateTime, null);
    }

    @NotNull
    static List<AccountBalanceData> transform(@NotNull PspAccountResponseDto pspAccount) {
        @NotNull String accountId = pspAccount.getAccountId();
        @NotNull LocalDateTime dateTime = DateUtils.toLocalDateTime(pspAccount.getBalanceOn());

        List<AccountBalanceData> balances = new ArrayList<>(1);
        AmountData amount = new AmountData(pspAccount.getAccountBalance(), pspAccount.getCurrency());
        balances.add(new AccountBalanceData(accountId, amount, CreditDebitType.DEBIT, BalanceType.INFORMATION, dateTime));

        amount = new AmountData(pspAccount.getAvailableBalance(), pspAccount.getCurrency());
        balances.add(new AccountBalanceData(accountId, amount, CreditDebitType.DEBIT, BalanceType.INTERIM_AVAILABLE, dateTime));
        return balances;
    }

    static AccountBalanceData transform(@NotNull PspAccountsSavingsData pspAccount, String accountId) {
        String externalId = pspAccount.getExternalId();
        if (accountId != null && !accountId.equals(externalId)) {
            return null;
        }

        AmountData amount = AmountData.transform(pspAccount);
        return amount == null ? null : new AccountBalanceData(externalId, amount, CreditDebitType.DEBIT, BalanceType.INFORMATION,
                DateUtils.toLocalDateTime(pspAccount.getLastActiveTransactionDate()));
    }

    static AccountBalanceData transform(@NotNull PspAccountsLoanData pspAccount, String accountId) {
        return null;
    }

    static AccountBalanceData transform(@NotNull PspAccountsGuarantorData pspAccount, String accountId) {
        return null;
    }

    static AccountBalanceData transform(@NotNull PspAccountsShareData pspAccount, String accountId) {
        return null;
    }
}
