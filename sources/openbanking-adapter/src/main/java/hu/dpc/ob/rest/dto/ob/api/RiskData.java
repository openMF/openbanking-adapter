/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Address;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentRisk;
import hu.dpc.ob.domain.type.PaymentContextCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuppressWarnings("unused")
public class RiskData {

    @JsonProperty(value = "PaymentContextCode")
    private PaymentContextCode paymentContext;

    @JsonProperty(value = "MerchantCategoryCode")
    @Size(min = 3, max = 4)
    private String merchantCategory; // ISO 18245 is not free

    @JsonProperty(value = "MerchantCustomerIdentification")
    @Size(max = 70)
    private String merchantCustomerIdentification;

    @JsonProperty(value = "DeliveryAddress")
    @Valid
    private DeliveryAddressData deliveryAddress;

    RiskData(PaymentContextCode paymentContext, @Size(min = 3, max = 4) String merchantCategory, @Size(max = 70) String merchantCustomerIdentification,
             @Valid DeliveryAddressData deliveryAddress) {
        this.paymentContext = paymentContext;
        this.merchantCategory = merchantCategory;
        this.merchantCustomerIdentification = merchantCustomerIdentification;
        this.deliveryAddress = deliveryAddress;
    }

    @NotNull
    public static RiskData create(PaymentRisk risk) {
        return risk == null
                ? new RiskData(null, null, null, null)
                : new RiskData(risk.getPaymentContext(), risk.getMerchantCategory(), risk.getMerchantCustomerIdentification(), DeliveryAddressData.create(risk.getDeliveryAddress()));
    }

    public PaymentRisk mapToEntity(@NotNull Payment payment) {
        if (paymentContext == null && merchantCategory == null && merchantCustomerIdentification == null && deliveryAddress == null)
            return null;

        Address address = deliveryAddress == null ? null : deliveryAddress.mapToEntity();
        return new PaymentRisk(payment, paymentContext, merchantCategory, merchantCustomerIdentification, address);
    }

    String updateEntity(@NotNull Payment payment) {
        PaymentRisk risk = payment.getRisk();
        if (risk == null) {
            payment.setRisk(mapToEntity(payment));
            return null;
        }
        if (paymentContext != null && !paymentContext.equals(risk.getPaymentContext()))
            return "Consent paymentContext " + risk.getPaymentContext() + " does not match requested paymentContext " + paymentContext;
        if (merchantCategory != null && !merchantCategory.equals(risk.getMerchantCategory()))
            return "Consent merchantCategory " + risk.getMerchantCategory() + " does not match requested merchantCategory " + merchantCategory;
        if (merchantCustomerIdentification != null && !merchantCustomerIdentification.equals(risk.getMerchantCustomerIdentification()))
            return "Consent merchantCustomerIdentification " + risk.getMerchantCustomerIdentification() + " does not match requested merchantCustomerIdentification " + merchantCustomerIdentification;
        if (deliveryAddress != null) {
            String failureReason = deliveryAddress.updateEntity(risk);
            if (failureReason != null)
                return failureReason;
        }
        return null;
    }
}
