/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model;

import hu.dpc.ob.domain.type.EventStatusCode;
import hu.dpc.ob.domain.type.PaymentActionCode;
import hu.dpc.ob.domain.type.PaymentStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PaymentStateMachine {

    /**
     * Executes the given transition from one status to another when the given transition (actualEvent + action) is valid.
     * @return the new {@link PaymentStatusCode} for the tag if the given transition is valid
     */
    @NotNull
    public static PaymentStatusCode handleTransition(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action, @NotNull EventStatusCode eventStatus) {
        if (!eventStatus.isAccepted())
            return currentStatus;

        PaymentStatusCode status = getTransitionStatus(currentStatus, action);
        if (status == null) {
            throw new UnsupportedOperationException("Payment status transition is not valid from status " + currentStatus + " and action " + action);
        }
        return status;
    }

    public static void checkValidAction(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action) {
        if (!isValidAction(currentStatus, action))
            throw new UnsupportedOperationException("Payment status transition is not valid from status " + currentStatus + " and action " + action);
    }

    public static boolean isValidAction(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action) {
        return getTransitionStatus(currentStatus, action) != null;
    }

    private static PaymentStatusCode getTransitionStatus(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action) {
        return PaymentStatusCode.forAction(currentStatus, action);
    }
}
