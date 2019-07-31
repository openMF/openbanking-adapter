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
import hu.dpc.ob.rest.dto.ob.api.type.CreditDebitType;
import hu.dpc.ob.rest.dto.ob.api.type.TransactionStatus;
import hu.dpc.ob.rest.dto.psp.PspTransactionData;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionData {

    @JsonProperty(value = "AccountId", required = true)
    @NotEmpty
    @Size(max = 40)
    private String accountId;

    @JsonProperty(value = "TransactionId")
    @Size(max = 210)
    private String transactionId;

    @JsonProperty(value = "TransactionReference")
    @Size(max = 35)
    private String transactionReference;

    @JsonProperty(value = "StatementReference")
    @Size(max = 35)
    private String statementReference;

    @JsonProperty(value = "CreditDebitIndicator", required = true)
    @Size(max = 35)
    @NotNull
    private CreditDebitType creditDebit;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private TransactionStatus status;

    @JsonProperty(value = "BookingDateTime", required = true)
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    @NotNull
    private LocalDateTime bookingDateTime;

    @JsonProperty(value = "ValueDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime valueDateTime;

    @JsonProperty(value = "TransactionInformation")
    @Size(max = 500)
    private String transactionInformation;

    @JsonProperty(value = "AddressLine")
    @Size(max = 70)
    private String addressLine;

    @JsonProperty(value = "Amount", required = true)
    @NotNull
    @Valid
    private AmountData amount;

    @JsonProperty(value = "ChargeAmount")
    @Valid
    private AmountData chargeAmount;

    // CurrencyExchange
    // BankTransactionCode
    // ProprietaryBankTransactionCode
    // Balance
    // MerchantDetails
    // CreditorAgent
    // CreditorAccount
    // DebtorAgent
    // DebtorAccount
    // CardInstrument

    @JsonProperty(value = "SupplementaryData")
    @Valid
    private SupplementaryData supplementaryData;

    public TransactionData(@NotEmpty @Size(max = 40) String accountId, @Size(max = 210) String transactionId, @Size(max = 35) String transactionReference,
                           @Size(max = 35) String statementReference, @Size(max = 35) CreditDebitType creditDebit, @NotNull TransactionStatus status,
                           @NotNull LocalDateTime bookingDateTime, LocalDateTime valueDateTime, @Size(max = 500) String transactionInformation,
                           @Size(max = 70) String addressLine, @NotNull @Valid AmountData amount, @Valid AmountData chargeAmount,
                           @Valid SupplementaryData supplementaryData) {
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;
        this.statementReference = statementReference;
        this.creditDebit = creditDebit;
        this.status = status;
        this.bookingDateTime = bookingDateTime;
        this.valueDateTime = valueDateTime;
        this.transactionInformation = transactionInformation;
        this.addressLine = addressLine;
        this.amount = amount;
        this.chargeAmount = chargeAmount;
        this.supplementaryData = supplementaryData;
    }

    @NotNull
    public static TransactionData transform(@NotNull PspTransactionData transaction, boolean detail) {
        BigDecimal charge = transaction.getChargeAmount();
        @NotNull String currency = transaction.getCurrency();
        AmountData chargeAmount = null;
        if (charge != null)
            chargeAmount = new AmountData(charge, currency);

        return new TransactionData(transaction.getAccountId(), transaction.getTransactionId(), null, null, transaction.getTransactionType().toCreditDebitType(),
                TransactionStatus.BOOKED, transaction.getBookingDateTime().atStartOfDay(), transaction.getValueDateTime().atStartOfDay(), transaction.getNote(),
                null, new AmountData(transaction.getAmount(), currency), chargeAmount, null);
    }
}
