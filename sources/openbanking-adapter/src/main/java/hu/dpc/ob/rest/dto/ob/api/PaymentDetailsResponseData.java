/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentTransfer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PaymentDetailsResponseData {

    @JsonProperty(value = "PaymentStatus")
    @Valid
    private List<PaymentStatusData> statusList;


    public PaymentDetailsResponseData(List<PaymentStatusData> statusList) {
        this.statusList = statusList;
    }

    @NotNull
    static PaymentDetailsResponseData create(@NotNull Payment payment) {
        List<PaymentTransfer> transfers = payment.getTransfers();
        List<PaymentStatusData> statusList = transfers.isEmpty() ? null : transfers.stream().map(PaymentStatusData::create).collect(Collectors.toList());
        return new PaymentDetailsResponseData(statusList);
    }
}
