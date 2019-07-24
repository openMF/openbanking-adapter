/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.InteropPayment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class InteropRefundData {

    @JsonProperty(required = true)
    @Size(max = 36)
    @NotNull
    private String originalTransactionId; // mandatory payment id
    @Size(max = 128)
    private String refundReason; // optional, 128

    InteropRefundData(@Size(max = 36) @NotNull String originalTransactionId, @Size(max = 128) String refundReason) {
        this.originalTransactionId = originalTransactionId;
        this.refundReason = refundReason;
    }

    @NotNull
    public static InteropRefundData create(InteropPayment interopPayment) {
        return interopPayment == null || interopPayment.getRefundTransactionId() == null ? null : new InteropRefundData(interopPayment.getRefundTransactionId(), interopPayment.getRefundReason());
    }

    String updateEntity(@NotNull InteropPayment payment) {
        if (!originalTransactionId.equals(payment.getRefundTransactionId()))
            return "Consent originalTransactionId " + payment.getRefundTransactionId() + " does not match requested originalTransactionId " + originalTransactionId;
        if (refundReason != null && !refundReason.equals(payment.getRefundReason()))
            return "Consent refundReason " + payment.getRefundReason() + " does not match requested refundReason " + refundReason;
        return null;
    }
}
