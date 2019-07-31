/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import hu.dpc.ob.rest.dto.psp.type.TransactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PspTransactionData {

    @NotNull
    private String accountId;
    @NotNull
    @JsonProperty("savingTransactionId")
    private String transactionId;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private BigDecimal amount;

    private BigDecimal chargeAmount;
    @NotNull
    private String currency;
    @NotNull
    private BigDecimal accountBalance;
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate bookingDateTime;
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate valueDateTime;

    private String note;
}
