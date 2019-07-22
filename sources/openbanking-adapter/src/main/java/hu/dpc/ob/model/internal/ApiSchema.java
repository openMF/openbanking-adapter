/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.internal;

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

    private static final Map<String, ApiSchema> BY_ID = Arrays.stream(ApiSchema.values()).collect(Collectors.toMap(ApiSchema::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayText;

    @Override
    public String getDisplayLabel() {
        return id;
    }

    public static ApiSchema fromId(String id) {
        return BY_ID.get(id);
    }
}
