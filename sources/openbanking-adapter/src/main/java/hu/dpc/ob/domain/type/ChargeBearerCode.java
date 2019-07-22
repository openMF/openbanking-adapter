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
public enum ChargeBearerCode implements PersistentType<ChargeBearerCode, String>, DisplayType {

    @JsonProperty("BorneByCreditor")
    CREDITOR("BorneByCreditor", "Payment charges are to be borne by the creditor"),
    @JsonProperty("BorneByDebtor")
    DEBTOR("BorneByDebtor", "Payment charges are to be borne by the debtor"),
    @JsonProperty("FollowingServiceLevel")
    SERVICE("FollowingServiceLevel", "Charges are to be applied following the rules agreed in the service level and/or scheme"),
    @JsonProperty("Shared")
    SHARED("Shared", "Charges on the sender side are to be borne by the debtor, payment charges on the receiver side are to be borne by the creditor"),
;

    private static final Map<String, ChargeBearerCode> BY_ID = Arrays.stream(ChargeBearerCode.values()).collect(Collectors.toMap(ChargeBearerCode::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    public static ChargeBearerCode fromId(String id) {
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
