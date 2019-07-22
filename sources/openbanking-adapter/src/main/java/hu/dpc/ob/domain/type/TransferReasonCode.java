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
public enum TransferReasonCode implements PersistentType<TransferReasonCode, String>, DisplayType {

    @JsonProperty("Cancelled")
    CANCELLED("Cancelled", "Reason why the payment status is cancelled"),
    @JsonProperty("PendingFailingSettlement")
    PENDING_FAILING_SETTLEMENT("PendingFailingSettlement", "Reason why the payment status is pending (failing settlement)"),
    @JsonProperty("PendingSettlement")
    PENDING_SETTLEMENT("PendingSettlement", "Reason why the payment status is pending (settlement)"),
    @JsonProperty("Proprietary")
    PROPRIETARY("Proprietary", "Defines a free text proprietary reason"),
    @JsonProperty("ProprietaryRejection")
    PROPRIETARY_REJECTION("ProprietaryRejection", "Defines the reason that has been used by the Local Instrument system to reject the transaction"),
    @JsonProperty("Suspended")
    SUSPENDED("Suspended", "Reason why the payment status is suspended"),
    @JsonProperty("Unmatched")
    UNMATCHED("Unmatched", "Reason why the payment status is unmatched"),
    ;


    private static final Map<String, TransferReasonCode> BY_ID = Arrays.stream(TransferReasonCode.values()).collect(Collectors.toMap(TransferReasonCode::getId, e -> e));

    @NotNull
    private final String id;

    @NotNull
    private final String displayName;

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
