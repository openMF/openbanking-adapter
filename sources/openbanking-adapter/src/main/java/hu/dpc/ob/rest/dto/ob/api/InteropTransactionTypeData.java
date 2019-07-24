/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.InteropPayment;
import hu.dpc.ob.domain.type.InteropInitiatorType;
import hu.dpc.ob.domain.type.InteropScenario;
import hu.dpc.ob.domain.type.InteropTransactionRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class InteropTransactionTypeData {

    @JsonProperty(required = true)
    @Size(max = 32)
    @NotNull
    private InteropScenario scenario;
    @Size(max = 128)
    private String subScenario;
    @JsonProperty(required = true)
    @Size(max = 32)
    @NotNull
    private InteropTransactionRole initiator;
    @JsonProperty(required = true)
    @Size(max = 32)
    @NotNull
    private InteropInitiatorType initiatorType;
    @Valid
    private InteropRefundData refundInfo;
    @JsonFormat(pattern = "^[1-9]\\d{2}$")
    private String balanceOfPayments; // 3 digits number, see https://www.imf.org/external/np/sta/bopcode/

    InteropTransactionTypeData(@Size(max = 32) @NotNull InteropScenario scenario, @Size(max = 128) String subScenario,
                                      @Size(max = 32) @NotNull InteropTransactionRole initiator, @Size(max = 32) @NotNull InteropInitiatorType initiatorType,
                                      @Valid InteropRefundData refundInfo, String balanceOfPayments) {
        this.scenario = scenario;
        this.subScenario = subScenario;
        this.initiator = initiator;
        this.initiatorType = initiatorType;
        this.refundInfo = refundInfo;
        this.balanceOfPayments = balanceOfPayments;
    }

    @NotNull
    public static InteropTransactionTypeData create(InteropPayment payment) {
        return payment == null ? null : new InteropTransactionTypeData(payment.getScenario(), payment.getSubScenario(),
                payment.getInitiator(), payment.getInitiatorType(), InteropRefundData.create(payment), payment.getBalanceOfPayments());
    }

    String updateEntity(@NotNull InteropPayment payment) {
        if (!scenario.equals(payment.getScenario()))
            return "Consent scenario " + payment.getScenario() + " does not match requested scenario " + scenario;
        if (subScenario != null && !subScenario.equals(payment.getSubScenario()))
            return "Consent subScenario " + payment.getSubScenario() + " does not match requested subScenario " + subScenario;
        if (!initiator.equals(payment.getInitiator()))
            return "Consent initiator " + payment.getInitiator() + " does not match requested initiator " + initiator;
        if (!initiatorType.equals(payment.getInitiatorType()))
            return "Consent initiatorType " + payment.getInitiatorType() + " does not match requested initiatorType " + initiatorType;
        if (refundInfo != null) {
            String failureReason = refundInfo.updateEntity(payment);
            if (failureReason != null)
                return failureReason;
        }
        if (balanceOfPayments != null && !balanceOfPayments.equals(payment.getBalanceOfPayments()))
            return "Consent balanceOfPayments " + payment.getBalanceOfPayments() + " does not match requested balanceOfPayments " + balanceOfPayments;
        return null;
    }
}
