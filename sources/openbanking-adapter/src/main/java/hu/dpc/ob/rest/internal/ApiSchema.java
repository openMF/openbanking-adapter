/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.internal;

import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.domain.type.DisplayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ApiSchema implements DisplayType {
    OB("OB", "OpenBanking UK"),
    PSD2("PSD2", "NextGenPsd2"),
    ;

    private static final Map<String, ApiSchema> BY_CONFIG_NAME = Arrays.stream(ApiSchema.values()).collect(Collectors.toMap(ApiSchema::getConfigName, e -> e));

    @NotNull
    private final String configName;
    @NotNull
    private final String displayText;

    @Override
    public String getDisplayLabel() {
        return configName;
    }

    public static ApiSchema forConfigName(String configName) {
        return BY_CONFIG_NAME.get(configName);
    }
}
