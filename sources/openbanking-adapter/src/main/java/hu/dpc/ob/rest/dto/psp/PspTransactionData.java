/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.dpc.ob.rest.dto.psp.type.TransactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PspTransactionData {

    @NotNull
    private String accountId;
    @NotNull
    private String transactionId;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal chargeAmount;
    @NotNull
    private String currency;
    @NotNull
    private BigDecimal accountBalance;
    @NotNull
    private LocalDateTime bookingDateTime;
    @NotNull
    private LocalDateTime valueDateTime;

    private String note;
}
