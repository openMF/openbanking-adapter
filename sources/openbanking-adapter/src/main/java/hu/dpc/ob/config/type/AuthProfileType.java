/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthProfileType {
    NONE(""),
    BASIC("Basic "),
    BASIC_TWOFACTOR("Basic "),
    OAUTH("Bearer "),
    OAUTH_TWOFACTOR("Bearer "),
    ;

    private @NotNull final String prefix;


    public static AuthProfileType forConfig(String config) {
        if (Strings.isEmpty(config))
            return NONE;
        return AuthProfileType.valueOf(config.toUpperCase());
    }

    public String encode(String value) {
        if (value == null)
            return null;
        String prefix = getPrefix();
        return Strings.isEmpty(prefix) || value.startsWith(prefix) ? value : (prefix + value);
    }
}
