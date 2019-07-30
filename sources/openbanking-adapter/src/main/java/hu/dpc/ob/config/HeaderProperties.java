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

import javax.validation.constraints.NotEmpty;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public class HeaderProperties {

    @NotEmpty
    private String name;
    @NotEmpty
    private String key;

    public HeaderProperties(String name) {
        this.name = name;
    }

    void postConstruct(HeaderProperties parentProps) {
        if (parentProps == null || parentProps == this)
            return;

        // empty is a valid value
        if (key == null)
            key = parentProps.getKey();
    }
}
