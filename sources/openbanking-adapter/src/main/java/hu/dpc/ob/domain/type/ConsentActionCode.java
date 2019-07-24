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

import static hu.dpc.ob.domain.type.ConsentStatusCode.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum ConsentActionCode implements PersistentType<ConsentActionCode, String> {

    // --- Consent actions ---
    CREATE("Create"),
    PREPARE("Prepare"),
    @JsonProperty("Authorize")
    AUTHORIZE("Authorize"),
    @JsonProperty("Reject")
    REJECT("Reject"),
    @JsonProperty("Revoke")
    REVOKE("Revoke"),
    PAYMENT("PaymentAccept"),
    PAYMENT_EXECUTED("PaymentExecuted"),
    DELETE("Delete"),
//    @JsonProperty("AddAccount")
//    ADD_ACCOUNT("AddAccount"),
//    @JsonProperty("RemoveAccount")
//    REMOVE_ACCOUNT("RemoveAccount"),
//    @JsonProperty("AddPermission")
//    ADD_PERMISSION("AddPermission"),
//    @JsonProperty("RemovePermission")
//    REMOVE_PERMISSION("RemovePermission"),

    // --- Query actions ---
    QUERY_ACCOUNT("QueryAccount"),
    QUERY_BALANCE("QueryBalance"),
    QUERY_TRANSACTION("QueryTransaction"),
    QUERY_PARTY("QueryParty"),
    QUERY_CONSENT("QueryConsent"),
    QUERY_PAYMENT("QueryPayment"),
    ;


    private static final Map<String, ConsentActionCode> BY_ID = Arrays.stream(ConsentActionCode.values()).collect(Collectors.toMap(ConsentActionCode::getId, e -> e));

    @NotNull
    private final String id;

    @Override
    public String toId() {
        return id;
    }

    public static ConsentActionCode fromId(String id) {
        return BY_ID.get(id);
    }

    /** @return NULL if this action does not cause any status change */
    public ConsentStatusCode getConsentStatus() {
        switch (this) {
            case CREATE:
            case PREPARE:
                return AWAITING_AUTHORIZATION;
            case AUTHORIZE:
                return AUTHORIZED;
            case REJECT:
                return REJECTED;
            case REVOKE:
                return REVOKED;
            case PAYMENT_EXECUTED:
                return CONSUMED;
            default:
                return null;
        }
    }

    public static ConsentActionCode forAction(@NotNull PaymentActionCode action) {
        if (action == PaymentActionCode.PAYMENT_ACCEPT)
            return PAYMENT;
        if (action == PaymentActionCode.PAYMENT_EXECUTE)
            return PAYMENT_EXECUTED;
        return null;
    }

    public boolean isUpdate() {
        return getConsentStatus() != null;
    }
}
