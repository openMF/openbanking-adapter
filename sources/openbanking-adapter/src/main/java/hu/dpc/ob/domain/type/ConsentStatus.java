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
public enum ConsentStatus implements PersistentType<ConsentStatus, String>, DisplayType {

    @JsonProperty("AwaitingAuthorisation")
    AWAITING_AUTHORIZATION("AwaitingAuthorisation", "Waiting for authorization"),
    @JsonProperty("Authorised")
    AUTHORIZED("Authorised", "Authorised"),
    @JsonProperty("Rejected")
    REJECTED("Rejected", "Rejected"),
    @JsonProperty("Revoked")
    REVOKED("Revoked", "Revoked"),
    ;


    private static final Map<String, ConsentStatus> BY_ID = Arrays.stream(ConsentStatus.values()).collect(Collectors.toMap(ConsentStatus::getApiName, e -> e));

    @NotNull
    private final String apiName;
    @NotNull
    private final String displayName;

    public boolean isAlive() {
        return this == AWAITING_AUTHORIZATION || this == AUTHORIZED;
    }

    public boolean isActive() {
        return this == AUTHORIZED;
    }

    public static ConsentStatus fromId(String id) {
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

    public ConsentStatus forAction(@NotNull ConsentActionType action) {
        switch (action) {
            case CREATE:
                return null;
            case AUTHORIZE:
                return this == AWAITING_AUTHORIZATION ? AUTHORIZED : null;
            case REJECT:
                return this == AWAITING_AUTHORIZATION ? REJECTED : null;
            case REVOKE:
                return REVOKED;
            default:
                return this;
        }
    }

    public static ConsentStatus forAction(ConsentStatus currentStatus, @NotNull ConsentActionType action) {
        if (currentStatus == null) {
            switch (action) {
                case CREATE:
                    return AWAITING_AUTHORIZATION;
                case REVOKE:
                    return REVOKED;
                default:
                    return null;
            }
        }
        return currentStatus.forAction(action);
    }
}
