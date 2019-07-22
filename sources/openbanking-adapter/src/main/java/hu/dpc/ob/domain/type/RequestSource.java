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
public enum RequestSource {

    API("api"),
    ACCESS("access"),
    PSP("psp"),
    ;

    private static final Map<String, RequestSource> BY_ID = Arrays.stream(RequestSource.values()).collect(Collectors.toMap(RequestSource::getId, e -> e));

    @NotNull
    private final String id;

    public static RequestSource fromId(String id) {
        return BY_ID.get(id);
    }
}
