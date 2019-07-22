/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.type.ChargeBearerCode;
import hu.dpc.ob.domain.type.ChargeTypeCode;
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
public class ChargeData {

    @JsonProperty(value = "ChargeBearer", required = true)
    @NotNull
    private ChargeBearerCode bearer;

    @JsonProperty(value = "Type", required = true)
    @NotNull
    private ChargeTypeCode type;

    @JsonProperty(value = "Amount", required = true)
    @NotNull
    @Valid
    private AmountData amount;

    ChargeData(@NotNull ChargeBearerCode bearer, @NotNull ChargeTypeCode type, @NotNull @Valid AmountData amount) {
        this.bearer = bearer;
        this.type = type;
        this.amount = amount;
    }

    public static ChargeData create(Charge charge) {
        return charge == null ? null : new ChargeData(charge.getBearer(), charge.getType(), AmountData.create(charge));
    }
}
