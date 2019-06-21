/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public enum TransactionStatus implements PersistentType<TransactionStatus, String>, DisplayType {

    @JsonProperty("Pending")
    PENDING("Pending", "Waiting for authorization"),
    @JsonProperty("Rejected")
    REJECTED("Rejected", "Authorised"),
    @JsonProperty("Rejected")
    ACCEPTED_SETTLEMENT_PROCESS("AcceptedSettlementInProcess", "Rejected"),
    @JsonProperty("AcceptedSettlementCompleted")
    ACCEPTED_SETTLEMENT_COMPLETED("AcceptedSettlementCompleted", "Revoked"),
    @JsonProperty("AcceptedWithoutPosting")
    ACCEPTED_WITHOUT_POSTING("AcceptedWithoutPosting", "Revoked"),
    @JsonProperty("AcceptedCreditSettlementCompleted")
    ACCEPTED_CREDIT_SETTLEMENT_COMPLETED("AcceptedCreditSettlementCompleted", "Revoked"),
    ;


    private static final Map<String, TransactionStatus> BY_ID = Arrays.stream(TransactionStatus.values()).collect(Collectors.toMap(TransactionStatus::getApiName, e -> e));

    @NotNull
    private final String apiName;
    @NotNull
    private final String displayName;

    public boolean isAlive() {
        return this != REJECTED;
    }

    public boolean isActive() {
        return this != PENDING && this != REJECTED;
    }

    public static TransactionStatus fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return apiName;
    }

    @Override
    public String getDisplayLabel() {
        return apiName;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }

    public TransactionStatus forAction(@NotNull ConsentActionType action) {
        switch (action) {
            case CREATE:
                return null;
//            case AUTHORIZE:
//                return this == AWAITING_AUTHORIZATION ? AUTHORIZED : null;
//            case REJECT:
//                return this == AWAITING_AUTHORIZATION ? REJECTED : null;
//            case REVOKE:
//                return REVOKED;
            default:
                return this;
        }
    }

    public static TransactionStatus forAction(TransactionStatus currentStatus, @NotNull ConsentActionType action) {
//        if (currentStatus == null) {
//            switch (action) {
//                case CREATE:
//                    return AWAITING_AUTHORIZATION;
//                case REVOKE:
//                    return REVOKED;
//                default:
//                    return null;
//            }
//        }
        return currentStatus.forAction(action);
    }
}
