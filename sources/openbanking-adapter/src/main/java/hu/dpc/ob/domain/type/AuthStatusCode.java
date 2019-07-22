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

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum AuthStatusCode implements DisplayType {

    @JsonProperty("AwaitingFurtherAuthorisation")
    AWAITING_FURTHER_AUTHORISATION("AwaitingFurtherAuthorisation", "The authorisation flow is awaiting further authorisation"),
    @JsonProperty("Authorised")
    AUTHORISED("Authorised", "The authorisation flow has been fully authorised"),
    @JsonProperty("Rejected")
    REJECTED("Rejected", "The authorisation flow has been rejected"),
;

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    @Override
    public String getDisplayLabel() {
        return id;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }

    boolean isValidAction(@NotNull AuthAction action) {
        switch (this) {
            case AWAITING_FURTHER_AUTHORISATION:
                return true;
            case AUTHORISED:
                return action == AuthAction.AUTHORIZE;
            case REJECTED:
                return false;
            default:
                return false;
        }
    }

    /** @return NULL only if the parameter action is not valid */
    AuthStatusCode forActionImpl(AuthStatusCode currentStatus, @NotNull AuthAction action) {
        if (!isValidAction(action))
            return null;

        AuthStatusCode status = action.getAuthStatus();
        return status == null ? currentStatus : status;
    }

    /** @return NULL only if the parameter action is not valid */
    public static AuthStatusCode forAction(AuthStatusCode currentStatus, @NotNull AuthAction action) {
        if (currentStatus == null) {
            return action.getAuthStatus();
        }
        return currentStatus.forActionImpl(currentStatus, action);
    }
}
