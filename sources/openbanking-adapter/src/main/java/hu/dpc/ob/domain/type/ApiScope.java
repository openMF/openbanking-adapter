/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum ApiScope {

    AIS("ais"),
    PIS("pis"),
    AIIS("aiis"),
    ;


    private static final Map<String, ApiScope> BY_ID = Arrays.stream(ApiScope.values()).collect(Collectors.toMap(ApiScope::getId, e -> e));

    @NotNull
    private final String id;

    public static ApiScope fromId(String id) {
        return BY_ID.get(id);
    }
}
