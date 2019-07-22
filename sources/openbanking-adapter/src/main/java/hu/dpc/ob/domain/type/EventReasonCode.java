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
public enum EventReasonCode implements PersistentType<EventReasonCode, String>, DisplayType {

    CLIENT_CONSENT_REQUEST("ClientConsentRequest", "Client Consent requested"),
    SCA_NOT_NEEDED("ScaNotNeeded", "Allow authorization without SCA"),
    SCA_NEEDED("ScaNeeded", "SCA authorization required"),
    USER_CONSENT_ACCEPT("UserConsentAuthorize", "User Consent accepted"),
    USER_CONSENT_REJECT("UserConsentReject", "User Consent rejected"),
    USER_TRANSACTION("UserTransaction", "User Payment requested"),
    // Reject reasons
    CONSENT_EXPIRED("ConsentExpired", "Consent expired"),
    TRANSACTION_EXPIRED("TransactionExpired", "Payment expired"),
    CONSENT_REJECTED("ConsentRejected", "Consent was rejected"),
    CONSENT_REVOKED("ConsentRevoked", "Consent was revoked"),
    ACTION_STATE_INVALID("ActionStateInvalid", "Action is invalid in this state"),
    USER_INACTIVE("UserInactive", "User is not active"),
    ACCOUNT_IDENTIFIER_NOT_FOUND("AccountIdentifierNotFound", "Secondary identifier is not registered"),
    ACCOUNT_NOT_ENABLED("AccountNotEnabled", "Payment account is not enabled"),
    PAYMENT_QUOTE_FAILED("PaymentQuoteFailed", "Calculating the payment quote failed"),
    NOT_ENOUGH_FUNDS("NotEnoughFunds", "Not enough funds"),
    INVALID_REQUEST("InvalidRequest", "Invalid payment request"),
    TRANSFER_FAILED("TransferFailed", "Transfer request failed"),
    LIMIT_FREQUENCY("LimitFrequency", "Frequency limit exceeded"),
    LIMIT_NUMBER("LimitNumber", "Number limit exceeded"),
    LIMIT_EXPIRATION("LimitExpiration", "Expiration limit exceeded"),
    LIMIT_AMOUNT("LimitAmount", "Amount limit exceeded"),
    ;


    private static final Map<String, EventReasonCode> BY_ID = Arrays.stream(EventReasonCode.values()).collect(Collectors.toMap(EventReasonCode::getId, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;


    public static EventReasonCode fromId(String id) {
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
