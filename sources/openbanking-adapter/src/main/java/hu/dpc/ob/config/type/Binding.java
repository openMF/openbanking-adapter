/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config.type;

import hu.dpc.ob.domain.type.*;

import javax.validation.constraints.NotNull;

public interface Binding extends ListConfig, DisplayType {

    @NotNull
    RequestSource getSource();

    @NotNull
    ApiScope getScope();

    boolean isUserRequest(); // whether request is sent in the name of TPP or PSU

    @NotNull
    ConsentActionCode getActionCode(); // whether request is sent in the name of TPP or PSU

    /** @return list of permissions. If the user has rights for ANY of the permissions, the process must be allowed
     * NULL return value - no permission check is needed, everyone has rights, empty return value means no-one has rights */
    PermissionCode[] getPermissions(boolean detail);
}
