/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import hu.dpc.ob.domain.entity.Payment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspPaymentCreateResponseDto {

    @Size(max = 36)
    @NotEmpty
    private String transactionId;


    @NotNull
    public Payment mapToEntity(@NotNull Payment payment) {
        payment.setTransactionId(transactionId);
        return payment;
    }

}
