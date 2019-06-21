/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspAccountsGuarantorData extends PspAccountsData {
    
    private PspAccountsLoanStatusData status;
    private EnumOptionData loanType;
    private Integer loanCycle;
    private Boolean inArrears;
    private BigDecimal originalLoan;
    private BigDecimal loanBalance;
    private BigDecimal amountPaid;
    private Boolean isActive;
    private String relationship;
    private BigDecimal onHoldAmount;
}
