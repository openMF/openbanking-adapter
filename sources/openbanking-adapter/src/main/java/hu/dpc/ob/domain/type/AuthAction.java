/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import static hu.dpc.ob.domain.type.AuthStatusCode.AUTHORISED;
import static hu.dpc.ob.domain.type.AuthStatusCode.REJECTED;

public enum AuthAction {

    AUTHORIZE,
    REJECT,
    ;

    /** @return NULL if this action does not cause any status change */
    public AuthStatusCode getAuthStatus() {
        switch (this) {
            case AUTHORIZE:
                return AUTHORISED;
            default:
                return REJECTED;
        }
    }
}
