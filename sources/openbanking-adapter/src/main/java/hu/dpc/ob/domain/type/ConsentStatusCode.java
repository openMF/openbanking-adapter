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
public enum ConsentStatusCode implements PersistentType<ConsentStatusCode, String>, DisplayType {

    @JsonProperty("AwaitingAuthorisation")
    AWAITING_AUTHORIZATION("AwaitingAuthorisation", "The consent resource is awaiting PSU authorisation"),
    @JsonProperty("Authorised")
    AUTHORIZED("Authorised", "The consent resource has been successfully authorised"),
    @JsonProperty("Rejected")
    REJECTED("Rejected", "The consent resource has been rejected"),
    @JsonProperty("Consumed")
    CONSUMED("Consumed", "The consented action has been successfully completed."),
    @JsonProperty("Revoked")
    REVOKED("Revoked", "Revoked"),
    ;


    private static final Map<String, ConsentStatusCode> BY_ID = Arrays.stream(ConsentStatusCode.values()).collect(Collectors.toMap(ConsentStatusCode::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    public boolean isAlive() {
        return this == AWAITING_AUTHORIZATION || this == AUTHORIZED;
    }

    public boolean isActive() {
        return this == AUTHORIZED;
    }

    public static ConsentStatusCode fromId(String id) {
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

    boolean isValidAction(@NotNull ConsentActionCode action) {
        switch (action) {
            case CREATE:
                return false;
            case PREPARE:
            case AUTHORIZE:
                return this == AWAITING_AUTHORIZATION;
            case REJECT:
                return this == AWAITING_AUTHORIZATION || this == AUTHORIZED;
            default:
                return this == AUTHORIZED;
        }
    }

    /** @return NULL only if the parameter action is not valid */
    ConsentStatusCode forActionImpl(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action) {
        if (!isValidAction(action))
            return null;

        ConsentStatusCode status = action.getConsentStatus();
        return status == null ? currentStatus : status;
    }

    /** @return NULL only if the parameter action is not valid */
    public static ConsentStatusCode forAction(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action) {
        if (currentStatus == null) {
            return action == ConsentActionCode.CREATE ? action.getConsentStatus() : null;
        }
        return currentStatus.forActionImpl(currentStatus, action);
    }
}
