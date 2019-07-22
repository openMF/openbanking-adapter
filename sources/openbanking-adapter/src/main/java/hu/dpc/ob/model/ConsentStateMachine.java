/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model;

import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.domain.type.EventStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ConsentStateMachine {

    /**
     * Executes the given transition from one status to another when the given transition (actualEvent + action) is valid.
     * @return the new {@link ConsentStatusCode} for the tag if the given transition is valid
     */
    @NotNull
    public static ConsentStatusCode handleTransition(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action, @NotNull EventStatusCode eventStatus) {
        if (!eventStatus.isAccepted())
            return currentStatus;

        ConsentStatusCode status = getTransitionStatus(currentStatus, action);
        if (status == null) {
            throw new UnsupportedOperationException("Consent status transition is not valid from status " + currentStatus + " and action " + action);
        }
        return status;
    }

    public static void checkValidAction(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action) {
        if (!isValidAction(currentStatus, action))
            throw new UnsupportedOperationException("Consent status transition is not valid from status " + currentStatus + " and action " + action);
    }

    public static boolean isValidAction(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action) {
        return getTransitionStatus(currentStatus, action) != null;
    }

    private static ConsentStatusCode getTransitionStatus(ConsentStatusCode currentStatus, @NotNull ConsentActionCode action) {
        return ConsentStatusCode.forAction(currentStatus, action);
    }
}
