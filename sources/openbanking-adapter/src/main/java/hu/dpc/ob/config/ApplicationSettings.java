/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public interface ApplicationSettings {

    static String SCHEMA_DEFAULT_SETTINGS = "schema-basic-settings";
    static String OPERATION_DEFAULT_SETTINGS = "operation-basic-settings";
    static String BINDING_DEFAULT_SETTINGS = "binding-basic-settings";
}
