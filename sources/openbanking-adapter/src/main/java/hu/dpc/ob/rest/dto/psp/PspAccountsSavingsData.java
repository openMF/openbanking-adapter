/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import hu.dpc.ob.rest.dto.ob.api.type.AccountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspAccountsSavingsData extends PspAccountsData {

    private PspAccountsSavingsStatusData status;
    private PspCurrencyData currency;
    private BigDecimal accountBalance;
    //differentiate Individual, JLG or Group accountId
    private EnumOptionData accountType;
    private PspAccountsSavingsTimelineData timeline;
    private PspAccountsSavingsSubStatusData subStatus;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate lastActiveTransactionDate;

    //differentiate deposit accounts Savings, FD and RD accounts
    private EnumOptionData depositType;

    @Transient
    public AccountType getApiAccountType() {
        return accountType == null || accountType.getId() == 1L ? AccountType.PERSONAL : AccountType.BUSINESS;
    }

    @Transient
    public String getApiNickName() {
        return getProductName() == null
                ? (getShortProductName() == null ? getAccountNo() : getShortProductName())
                : getProductName();
    }
}
