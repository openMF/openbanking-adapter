/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import javax.validation.constraints.NotNull;

public interface DisplayType {

    @NotNull
    String getDisplayLabel();
    @NotNull
    String getDisplayText();
}
