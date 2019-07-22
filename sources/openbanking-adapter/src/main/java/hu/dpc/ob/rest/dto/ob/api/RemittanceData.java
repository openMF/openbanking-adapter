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
import hu.dpc.ob.domain.entity.Remittance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class RemittanceData {

    @JsonProperty(value = "Unstructured")
    @Size(max = 140)
    private String unstructured;

    @JsonProperty(value = "Reference")
    @Size(max = 35)
    private String reference;

    @JsonProperty(value = "SupplementaryData")
    private String supplementaryData;

    RemittanceData(@Size(max = 140) String unstructured, @Size(max = 35) String reference, String supplementaryData) {
        this.unstructured = unstructured;
        this.reference = reference;
        this.supplementaryData = supplementaryData;
    }

    static RemittanceData create(@NotNull Remittance remittance) {
        return remittance == null ? new RemittanceData(null, null, null) : new RemittanceData(remittance.getUnstructured(), remittance.getReference(), remittance.getSupplementary());
    }

    public Remittance mapToEntity(@NotNull Payment payment) {
        if (getUnstructured() == null && getReference() == null && getSupplementaryData() == null)
            return null;
        return new Remittance(payment, unstructured, reference, supplementaryData);
    }

    String updateEntity(@NotNull Payment payment) {
        Remittance remittance = payment.getRemittanceInformation();
        if (remittance == null) {
            payment.setRemittanceInformation(mapToEntity(payment));
            return null;
        }
        if (unstructured != null)
            remittance.setUnstructured(unstructured);
        if (reference != null)
            remittance.setReference(reference);
        if (supplementaryData != null)
            remittance.setSupplementary(supplementaryData);
        return null;
    }
}
