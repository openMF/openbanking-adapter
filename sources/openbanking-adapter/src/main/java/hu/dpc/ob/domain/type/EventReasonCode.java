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
public enum EventReasonCode implements PersistentType<EventReasonCode, String>, DisplayType {

    USER_CONSENT_ACCEPTED("UserConsentAuthorize", "User Consent accepted"),
    CLIENT_CONSENT_REQUESTED("ClientConsentRequest", "Client Consent requested"),
    USER_CONSENT_REJECTED("UserConsentReject", "User Consent rejected"),
    USER_TRANSACTION("UserTransaction", "User Transaction requested"),
    ;


    private static final Map<String, EventReasonCode> BY_ID = Arrays.stream(EventReasonCode.values()).collect(Collectors.toMap(EventReasonCode::getCode, e -> e));

    @NotNull
    private final String code;
    @NotNull
    private final String desc;


    public static EventReasonCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return code;
    }

    @Override
    public String getDisplayLabel() {
        return code;
    }

    @Override
    public String getDisplayText() {
        return desc;
    }
}
