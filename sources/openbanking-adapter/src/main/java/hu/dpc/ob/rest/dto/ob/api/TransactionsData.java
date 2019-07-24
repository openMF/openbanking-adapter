/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.rest.dto.psp.PspTransactionsResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionsData {

    @JsonProperty(value = "Transaction", required = true)
    private List<TransactionData> transactions;

    TransactionsData(List<TransactionData> transactions) {
        this.transactions = transactions;
    }

    @NotNull
    public static TransactionsData transform(@NotNull PspTransactionsResponseDto transactions, boolean detail) {
        List<TransactionData> trans = transactions.getTransactions().stream().map(t -> TransactionData.transform(t, detail)).collect(toList());
        return new TransactionsData(trans);
    }

    @NotNull
    public static TransactionsData transform(@NotNull List<PspTransactionsResponseDto> transList, boolean detail) {
        List<TransactionData> transactions = new ArrayList<>();
        for (PspTransactionsResponseDto transactionsResponseDto : transList) {
            transactions.addAll(transactionsResponseDto.getTransactions().stream().map(t -> TransactionData.transform(t, detail)).collect(toList()));
        }
        return new TransactionsData(transactions);
    }
}
