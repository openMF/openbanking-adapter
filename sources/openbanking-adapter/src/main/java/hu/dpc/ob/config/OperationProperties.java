/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.config.type.ApplicationSettings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class OperationProperties extends TenantDependentProperties {

    public OperationProperties(String name) {
        super(name);
    }

    @Override
    protected String getDefaultName() {
        return ApplicationSettings.OPERATION_DEFAULT_SETTINGS;
    }
}
