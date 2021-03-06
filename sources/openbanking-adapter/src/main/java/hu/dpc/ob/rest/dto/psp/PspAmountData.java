/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonFormat;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.util.MathUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PspAmountData {

    @JsonFormat(pattern = "^\\d{1,13}\\.\\d{1,5}$")
    @Digits(integer = 19, fraction = 5) // OB: (18,5), interoperation: (22,4), CN: (15,5), 1.2: (19,6)! -> we support (19,5)
    @NotNull
    private BigDecimal amount;

    @JsonFormat(pattern = "^[A-Z]{3,3}$")
    @NotEmpty
    private String currency;


    PspAmountData(@NotNull BigDecimal amount, @NotEmpty String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @NotNull
    static PspAmountData transform(@NotNull PspAccountResponseDto pspAccount) {
        return new PspAmountData(pspAccount.getAccountBalance(), pspAccount.getCurrency());
    }

    @NotNull
    static PspAmountData transform(@NotNull PspAccountsSavingsData pspAccount) {
        return new PspAmountData(pspAccount.getAccountBalance(), pspAccount.getCurrency().getCode());
    }

    static PspAmountData transform(@NotNull PspAccountsLoanData pspAccount) {
        return null;
    }

    static PspAmountData transform(@NotNull PspAccountsGuarantorData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return null;
    }

    @NotNull
    public static PspAmountData create(@NotNull Payment payment) {
        return new PspAmountData(payment.getAmount(), payment.getCurrency());
    }

    @NotNull
    static PspAmountData create(@NotNull Charge charge) {
        return new PspAmountData(charge.getAmount(), charge.getCurrency());
    }

    String updateEntity(@NotNull Payment payment) {
        if (!MathUtils.isEqualTo(amount, payment.getAmount()))
            return "Consent amount " + payment.getAmount() + " does not match requested amount " + amount;
        if (!currency.equals(payment.getCurrency()))
            return "Consent currency " + payment.getCurrency() + " does not match requested currency " + currency;
        return null;
    }
}
