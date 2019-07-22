/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.PersistentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum ChargeTypeCode implements PersistentType<ChargeTypeCode, String>, DisplayType {

    @JsonProperty("UK.OBIE.CHAPSOut")
    CHAPS("CHAPSOut", "CHAPS Payment Service fee"), // same-day UK payments
    @JsonProperty("UK.OBIE.BalanceTransferOut")
    BALANCE_TRANSFER("BalanceTransferOut", "Balance Transfer Service fee"),
    @JsonProperty("UK.OBIE.MoneyTransferOut")
    MONEY_TRANSFER("MoneyTransferOut", "Money Transfer Service fee"),
;

    private static final Map<String, ChargeTypeCode> BY_ID = Arrays.stream(ChargeTypeCode.values()).collect(Collectors.toMap(ChargeTypeCode::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    public static ChargeTypeCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return id;
    }

    @Override
    public String getDisplayLabel() {
        return id;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }
}
