/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp.type;

import hu.dpc.ob.domain.type.PaymentActionCode;

import javax.validation.constraints.NotNull;

import static hu.dpc.ob.domain.type.PaymentActionCode.*;

public enum TransferState {

    RECEIVED,
    RESERVED,
    COMMITTED,
    ABORTED,
;

    public static PaymentActionCode getPaymentAction(TransferState state) {
        return state == null ? null : state.getPaymentAction();
    }

    @NotNull
    public PaymentActionCode getPaymentAction() {
        switch (this) {
            case RECEIVED:
            case RESERVED:
                return PAYMENT_ACCEPT;
            case COMMITTED:
                return PAYMENT_EXECUTE;
            default:
                return PAYMENT_REJECT;
        }
    }
}
