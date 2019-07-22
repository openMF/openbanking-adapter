/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PisApiConsentResponseData extends PisConsentResponseData {

    PisApiConsentResponseData(@NotNull PisInitiationData initiation, AuthorizationData authorization, ScaSupportData scaSupportData,
                              @NotEmpty @Size(max = 128) String consentId, @NotNull LocalDateTime creationDateTime,
                              @NotNull ConsentStatusCode status, @NotNull LocalDateTime statusUpdateDateTime, LocalDateTime expectedExecutionDateTime,
                              LocalDateTime expectedSettlementDateTime, List<ChargeData> charges) {
        super(initiation, authorization, scaSupportData, consentId, creationDateTime, status, statusUpdateDateTime, expectedExecutionDateTime,
                expectedSettlementDateTime, charges);
    }

    @NotNull
    static PisApiConsentResponseData create(@NotNull Consent consent) {
        Payment payment = consent.getPayment();

        List<Charge> charges = payment.getCharges();
        List<ChargeData> chargeList = charges.isEmpty() ? null : charges.stream().map(ChargeData::create).collect(Collectors.toList());
        return new PisApiConsentResponseData(PisInitiationData.create(payment), AuthorizationData.create(payment.getAuthorization()),
                ScaSupportData.create(payment.getScaSupport()), consent.getConsentId(), consent.getCreatedOn(), consent.getStatus(),
                consent.getUpdatedOn(), payment.getExpectedExecutionOn(), payment.getExpectedSettlementOn(), chargeList);
    }
}
