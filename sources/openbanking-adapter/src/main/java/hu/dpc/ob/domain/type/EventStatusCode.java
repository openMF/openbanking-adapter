/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

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
public enum EventStatusCode implements PersistentType<EventStatusCode, String>, DisplayType {

    ACCEPTED("Accepted", "Event accepted"),
    REJECTED("Rejected", "Event rejected"),
    ;

    private static final Map<String, EventStatusCode> BY_ID = Arrays.stream(EventStatusCode.values()).collect(Collectors.toMap(EventStatusCode::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    public boolean isFailed() {
        return this == REJECTED;
    }

    public static EventStatusCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return id;
    }

    @Override
    public String getDisplayLabel() {
        return id;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }
}
