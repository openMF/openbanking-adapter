/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.psp.PspAccountsGuarantorData;
import hu.dpc.ob.rest.dto.psp.PspAccountsLoanData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsShareData;
import hu.dpc.ob.rest.dto.psp.PspIdentifiersResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AmountData {

    @JsonProperty(value = "Amount", required = true)
    @NotNull
    private BigDecimal amount;

    @JsonProperty(value = "Currency", required = true)
    @JsonFormat(pattern = "^[A-Z]{3,3}$")
    @NotEmpty
    private String currency;


    AmountData(@NotNull BigDecimal amount, @NotEmpty String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    static AmountData transform(@NotNull PspAccountsSavingsData pspAccount) {
        return new AmountData(pspAccount.getAccountBalance(), pspAccount.getCurrency().getCode());
    }

    static AmountData transform(@NotNull PspAccountsLoanData pspAccount) {
        return null;
    }

    static AmountData transform(@NotNull PspAccountsGuarantorData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return null;
    }

    static AmountData transform(@NotNull PspAccountsShareData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return new AmountData(BigDecimal.valueOf(pspAccount.getTotalApprovedShares()), pspAccount.getCurrency().getCode());
    }
}
