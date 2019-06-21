/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountSubType {

    @JsonProperty("ChargeCard")
    CHARGE_CARD,
    @JsonProperty("CreditCard")
    CREDIT_CARD,
    @JsonProperty("CurrentAccount")
    CURRENT_ACCOUNT,
    @JsonProperty("EMoney")
    E_MONEY,
    @JsonProperty("Loan")
    LOAN,
    @JsonProperty("Mortgage")
    MORTGAGE,
    @JsonProperty("PrePaidCard")
    PRE_PAID_CARD,
    @JsonProperty("Savings")
    SAVINGS,
;
}
