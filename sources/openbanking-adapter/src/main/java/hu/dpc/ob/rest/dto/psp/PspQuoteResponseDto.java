/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.ChargeBearerCode;
import hu.dpc.ob.domain.type.ChargeTypeCode;
import hu.dpc.ob.domain.type.EventStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PspQuoteResponseDto {

    @NotNull
    private String transactionCode;

    @NotNull
    private String quoteCode;

    @NotNull
    private EventStatusCode state;

    private PspAmountData fspFee;

    private PspAmountData fspCommission;


    public Charge mapToEntity(@NotNull Payment payment) { // TODO type codes,  // TODO commissions
        return getFspFee() == null || !state.isAccepted() ? null : new Charge(payment, ChargeBearerCode.DEBTOR, ChargeTypeCode.MONEY_TRANSFER, fspFee.getAmount(), fspFee.getCurrency());
    }
}
