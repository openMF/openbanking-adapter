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
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionsResponseDto {

    @JsonProperty(value = "Data", required = true)
    @NotNull
    private TransactionsData data;

    TransactionsResponseDto(@NotNull TransactionsData data) {
        this.data = data;
    }

    public static TransactionsResponseDto transform(@NotNull PspTransactionsResponseDto transactions, boolean detail) {
        TransactionsData transform = TransactionsData.transform(transactions, detail);
        return new TransactionsResponseDto(transform);
    }

    public static TransactionsResponseDto transform(@NotNull List<PspTransactionsResponseDto> transList, boolean detail) {
        TransactionsData transform = TransactionsData.transform(transList, detail);
        return new TransactionsResponseDto(transform);
    }
}
