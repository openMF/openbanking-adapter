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

import static hu.dpc.ob.domain.type.PaymentStatusCode.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum PaymentActionCode implements PersistentType<PaymentActionCode, String> {

    PAYMENT_CREATE("PaymentCreate"),
    PAYMENT_VALIDATE("PaymentValidate"),
    DEBTOR_ACCOUNT_RESOLVE("DebtorAccount"),
    DEBTOR_FOUNDS_CHECK("DebtorFounds"),
    DEBTOR_QUOTES("DebtorQuotes"),
    PAYMENT_ACCEPT("PaymentAccept"),
    PAYMENT_EXECUTE("PaymentExecute"),
    PAYMENT_REJECT("PaymentReject"),
    PAYMENT_REVOKE("PaymentRevoke"),
    ;


    private static final Map<String, PaymentActionCode> BY_ID = Arrays.stream(PaymentActionCode.values()).collect(Collectors.toMap(PaymentActionCode::getId, e -> e));

    @NotNull
    private final String id;

    @Override
    public String toId() {
        return id;
    }

    public static PaymentActionCode fromId(String id) {
        return BY_ID.get(id);
    }

    /** @return NULL if this action does not cause any status change */
    public PaymentStatusCode getPaymentStatus() {
        switch (this) {
            case PAYMENT_CREATE:
                return PENDING;
            case PAYMENT_VALIDATE:
                return ACCEPTED_TECHNICAL_VALIDATION;
            case DEBTOR_ACCOUNT_RESOLVE:
                return ACCEPTED_CUSTOMER_PROFILE;
            case DEBTOR_FOUNDS_CHECK:
                return ACCEPTED_FUNDS_CHECKED;
            case DEBTOR_QUOTES:
                return ACCEPTED_SETTLEMENT_COMPLETED;
            case PAYMENT_ACCEPT:
                return ACCEPTED_WITHOUT_POSTING;
            case PAYMENT_EXECUTE:
                return RECEIVED;
            case PAYMENT_REJECT:
                return REJECTED;
            case PAYMENT_REVOKE:
                return CANCELLED;
            default:
                return null;
        }
    }

    public static PaymentActionCode forAction(@NotNull ConsentActionCode action) {
        if (action == ConsentActionCode.REJECT)
            return PAYMENT_REJECT;
        if (action == ConsentActionCode.REVOKE)
            return PAYMENT_REVOKE;
        return null;
    }

    public boolean isUpdate() {
        return getPaymentStatus() != null;
    }
}
