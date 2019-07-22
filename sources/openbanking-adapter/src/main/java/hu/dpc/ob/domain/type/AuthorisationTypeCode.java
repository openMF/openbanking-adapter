/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.PersistentType;
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
public enum AuthorisationTypeCode implements PersistentType<AuthorisationTypeCode, String> {

    @JsonProperty("Any")
    ANY("Any"),
    @JsonProperty("Single")
    SINGLE("Single"),
    ;


    private static final Map<String, AuthorisationTypeCode> BY_ID = Arrays.stream(AuthorisationTypeCode.values()).collect(Collectors.toMap(AuthorisationTypeCode::getId, e -> e));

    @NotNull
    private final String id;

    public static AuthorisationTypeCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return id;
    }
}
