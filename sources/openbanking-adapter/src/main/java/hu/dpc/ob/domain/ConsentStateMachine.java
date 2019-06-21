/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain;

import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.domain.type.ConsentStatus;
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
     * @return the new {@link ConsentStatus} for the tag if the given transition is valid
     */
    @NotNull
    public static ConsentStatus handleTransition(ConsentStatus currentStatus, @NotNull ConsentActionType action) {
        ConsentStatus status = getTransitionStatus(currentStatus, action);
        if (status == null) {
            throw new UnsupportedOperationException("Consent status transition is not valid from statuc " + currentStatus + "and action " + action);
        }
        return status;
    }

    public static boolean isValidAction(ConsentStatus currentStatus, @NotNull ConsentActionType action) {
        return getTransitionStatus(currentStatus, action) != null;
    }

    private static ConsentStatus getTransitionStatus(ConsentStatus currentStatus, @NotNull ConsentActionType action) {
        return ConsentStatus.forAction(currentStatus, action);
    }
}
