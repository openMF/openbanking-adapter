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
public enum PaymentStatusCode implements PersistentType<PaymentStatusCode, String>, DisplayType {

    @JsonProperty("Pending")
    PENDING("Pending", "Payment initiation is pending. Further checks and status update will be performed", false),
    @JsonProperty("Accepted")
    ACCEPTED("Accepted", "Accepted", true, PENDING),
    @JsonProperty("AcceptedTechnicalValidation")
    ACCEPTED_TECHNICAL_VALIDATION("AcceptedTechnicalValidation", "AcceptedTechnicalValidation", true, PENDING),
    @JsonProperty("AcceptedCustomerProfile")
    ACCEPTED_CUSTOMER_PROFILE("AcceptedCustomerProfile", "AcceptedCustomerProfile", true, PENDING),
    @JsonProperty("PartiallyAcceptedTechnicalCorrect")
    PARTIALLY_ACCEPTED_TECHNICAL_CORRECT("PartiallyAcceptedTechnicalCorrect", "PartiallyAcceptedTechnicalCorrect", true, PENDING),

    @JsonProperty("Rejected")
    ACCEPTED_SETTLEMENT_PROCESS("AcceptedSettlementInProcess", "All preceding checks were successful and therefore the payment initiation has been accepted for execution", false),
    @JsonProperty("AcceptedFundsChecked")
    ACCEPTED_FUNDS_CHECKED("AcceptedFundsChecked", "AcceptedFundsChecked", true, ACCEPTED_SETTLEMENT_PROCESS),
    @JsonProperty("AcceptedWithChange")
    ACCEPTED_WITH_CHANGE("AcceptedWithChange", "AcceptedWithChange", true, ACCEPTED_SETTLEMENT_PROCESS),

    @JsonProperty("AcceptedSettlementCompleted")
    ACCEPTED_SETTLEMENT_COMPLETED("AcceptedSettlementCompleted", "Settlement on the debtor's accountId has been completed", false),
    @JsonProperty("AcceptedWithoutPosting")
    ACCEPTED_WITHOUT_POSTING("AcceptedWithoutPosting", "Payment instruction included in the credit transfer is accepted", false),

    @JsonProperty("AcceptedCreditSettlementCompleted")
    ACCEPTED_CREDIT_SETTLEMENT_COMPLETED("AcceptedCreditSettlementCompleted", "Settlement on the creditor's accountId has been completed", false),
    @JsonProperty("Received")
    RECEIVED("Received", "Received", true, ACCEPTED_CREDIT_SETTLEMENT_COMPLETED),


    @JsonProperty("Rejected")
    REJECTED("Rejected", "Payment initiation has been rejected.", false),
    @JsonProperty("PendingCancellationRequest")
    PENDING_CANCELLATION_REQUEST("PendingCancellationRequest", "PendingCancellationRequest", true, REJECTED),
    @JsonProperty("PartiallyAcceptedCancellationRequest")
    PARTIALLY_ACCEPTED_CANCELLATION_REQUEST("PartiallyAcceptedCancellationRequest", "PartiallyAcceptedCancellationRequest", true, REJECTED),
    @JsonProperty("AcceptedCancellationRequest")
    ACCEPTED_CANCELLATION_REQUEST("AcceptedCancellationRequest", "AcceptedCancellationRequest", true, REJECTED),
    @JsonProperty("NoCancellationProcess")
    NO_CANCELLATION_PROCESS("NoCancellationProcess", "NoCancellationProcess", true, REJECTED),
    @JsonProperty("RejectedCancellationRequest")
    REJECTED_CANCELLATION_REQUEST("RejectedCancellationRequest", "RejectedCancellationRequest", true, REJECTED),
    @JsonProperty("PaymentCancelled")
    PAYMENT_CANCELLED("PaymentCancelled", "PaymentCancelled", true, REJECTED),
    @JsonProperty("Cancelled")
    CANCELLED("Cancelled", "Cancelled", true, REJECTED),
    ;


    private static final Map<String, PaymentStatusCode> BY_ID = Arrays.stream(PaymentStatusCode.values()).collect(Collectors.toMap(PaymentStatusCode::getId, e -> e));

    @NotNull
    private final String id;

    @NotNull
    private final String displayName;

    private final boolean detail;

    private final PaymentStatusCode basicStatus;

    PaymentStatusCode(@NotNull String id, @NotNull String displayName, boolean detail) {
        this(id, displayName, detail, null);
    }

    public boolean isAlive() {
        return this != REJECTED;
    }

    public boolean isActive() {
        return this != PENDING && this != REJECTED;
    }

    public boolean isExecuted() {
        return getBasicStatus() == ACCEPTED_CREDIT_SETTLEMENT_COMPLETED;
    }

    public boolean isFailed() {
        return getBasicStatus() == REJECTED;
    }

    public boolean isComplete() {
        @NotNull PaymentStatusCode status = getBasicStatus();
        return status == ACCEPTED_CREDIT_SETTLEMENT_COMPLETED || status == REJECTED;
    }

    public static PaymentStatusCode fromId(String id) {
        return BY_ID.get(id);
    }

    @NotNull
    public PaymentStatusCode getBasicStatus() {
        return basicStatus == null ? this : basicStatus;
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

    boolean isValidAction(@NotNull PaymentActionCode action) {
        PaymentStatusCode paymentStatus = action.getPaymentStatus();
        return paymentStatus == null || paymentStatus.ordinal() >= REJECTED.ordinal() || paymentStatus.ordinal() > this.ordinal();
    }

    /** @return NULL only if the parameter action is not valid */
    PaymentStatusCode forActionImpl(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action) {
        if (!isValidAction(action))
            return null;

        PaymentStatusCode status = action.getPaymentStatus();
        return status == null ? currentStatus : status;
    }

    /** @return NULL only if the parameter action is not valid */
    public static PaymentStatusCode forAction(PaymentStatusCode currentStatus, @NotNull PaymentActionCode action) {
        if (currentStatus == null) {
            return action == PaymentActionCode.PAYMENT_CREATE ? action.getPaymentStatus() : null;
        }
        return currentStatus.forActionImpl(currentStatus, action);
    }
}
