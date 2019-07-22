/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.PaymentStatusCode;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PaymentResponseData extends PisData {

    @JsonProperty(value = "DomesticPaymentId", required = true)
    @NotEmpty
    @Size(max = 40)
    private String paymentId;

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Size(max = 128)
    private String consentId;

    @JsonProperty(value = "CreationDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime creationDateTime;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private PaymentStatusCode status;

    @JsonProperty(value = "StatusUpdateDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime statusUpdateDateTime;

    @JsonProperty(value = "ExpectedExecutionDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expectedExecutionDateTime;

    @JsonProperty(value = "ExpectedSettlementDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expectedSettlementDateTime;

    @JsonProperty(value = "Charges")
    private List<ChargeData> charges;

    @JsonProperty(value = "MultiAuthorization")
    @Valid
    private MultiAuthorizationData authorization;

    public PaymentResponseData(@NotNull PisInitiationData initiation, @NotEmpty @Size(max = 40) String paymentId, @NotEmpty @Size(max = 128) String consentId,
                               @NotNull LocalDateTime creationDateTime, @NotNull PaymentStatusCode status, @NotNull LocalDateTime statusUpdateDateTime,
                               LocalDateTime expectedExecutionDateTime, LocalDateTime expectedSettlementDateTime, List<ChargeData> charges,
                               @Valid MultiAuthorizationData authorization) {
        super(initiation);
        this.paymentId = paymentId;
        this.consentId = consentId;
        this.creationDateTime = creationDateTime;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime;
        this.expectedExecutionDateTime = expectedExecutionDateTime;
        this.expectedSettlementDateTime = expectedSettlementDateTime;
        this.charges = charges;
        this.authorization = authorization;
    }

    @NotNull
    static PaymentResponseData create(@NotNull Payment payment) {
        List<Charge> charges = payment.getCharges();
        List<ChargeData> chargeList = charges.isEmpty() ? null : charges.stream().map(ChargeData::create).collect(Collectors.toList());
        @NotNull Consent consent = payment.getConsent();
        return new PaymentResponseData(PisInitiationData.create(payment), payment.getPaymentId(), consent.getConsentId(),
                payment.getCreatedOn(), payment.getStatus().getBasicStatus(), payment.getUpdatedOn(), payment.getExpectedExecutionOn(),
                payment.getExpectedSettlementOn(), chargeList, MultiAuthorizationData.create(payment.getAuthorization()));
    }
}
