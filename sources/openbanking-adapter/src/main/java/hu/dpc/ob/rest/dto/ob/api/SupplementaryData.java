/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.dpc.ob.domain.entity.InteropPayment;
import hu.dpc.ob.domain.entity.Payment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown=true)
public class SupplementaryData {

    @Valid
    private InteropSuplementaryData interopData;

    SupplementaryData(@Valid InteropSuplementaryData interopData) {
        this.interopData = interopData;
    }

    @NotNull
    static SupplementaryData create(InteropPayment interopPayment) {
        return interopPayment == null ? null : new SupplementaryData(InteropSuplementaryData.create(interopPayment));
    }

    public InteropPayment mapToEntity(@NotNull Payment payment) {
        return getInteropData() == null ? null : interopData.mapToEntity(payment);
    }

    String updateEntity(@NotNull Payment payment) {
        return getInteropData() == null ? null : interopData.updateEntity(payment);
    }
}
