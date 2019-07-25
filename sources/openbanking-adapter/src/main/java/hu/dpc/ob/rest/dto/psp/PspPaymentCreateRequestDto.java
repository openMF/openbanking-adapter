/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.config.PspSettings;
import hu.dpc.ob.domain.entity.InteropExtension;
import hu.dpc.ob.domain.entity.InteropPayment;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.entity.PaymentRisk;
import hu.dpc.ob.domain.type.InteropAmountType;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import hu.dpc.ob.rest.dto.ob.api.ExtensionData;
import hu.dpc.ob.rest.dto.ob.api.GeoCodeData;
import hu.dpc.ob.rest.dto.ob.api.InteropTransactionTypeData;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {
 *   "clientRefId": "{{ch_client_ref}}",
 *   "payer": {
 *     "partyIdInfo": {
 *       "partyIdType": "IBAN",
 *       "partyIdentifier": "{{IBAN_prefix}}{{fsp_payer_id}}{{fsp_payer_tenant1}}{{fsp_payer_account1}}"
 *     }
 *   },
 *   "payee": {
 *     "partyIdInfo": {
 *       "partyIdType": "IBAN",
 *       "partyIdentifier": "{{IBAN_prefix}}{{fsp_payee_id}}{{fsp_payee_tenant1}}{{fsp_payee_account1}}"
 *     },
 *     "merchantClassificationCode": ""
 *   },
 *   "amountType": "RECEIVE",
 *   "amount": {
 *     "amount": {{amount}},
 *     "currency": "{{currency}}"
 *   },
 *   "transactionType": {
 *     "scenario": "PAYMENT",
 *     "initiator": "PAYER",
 *     "initiatorType": "CONSUMER"
 *   },
 *   "note": "Demo interoperation merchant payment",
 *   "expiration": "2019-12-31T00:00:00.000-01:00"
 * }
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspPaymentCreateRequestDto {

    @Size(max = 36)
    private String clientRefId;
    @NotNull
    private InteropPartyData payer;
    @NotNull
    private InteropPartyData payee;
    @NotNull
    private InteropAmountType amountType;
    @NotNull
    private PspAmountData amount;
    @NotNull
    private InteropTransactionTypeData transactionType;

    private GeoCodeData geoCode;

    private String note;
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    private LocalDateTime expiration;

    private List<ExtensionData> extensionList;

    PspPaymentCreateRequestDto(@Size(max = 36) String clientRefId, @NotNull InteropPartyData payer, @NotNull InteropPartyData payee,
                               @NotNull InteropAmountType amountType, @NotNull PspAmountData amount, @NotNull InteropTransactionTypeData transactionType,
                               GeoCodeData geoCode, String note, LocalDateTime expiration, List<ExtensionData> extensionList) {
        this.clientRefId = clientRefId;
        this.payer = payer;
        this.payee = payee;
        this.amountType = amountType;
        this.amount = amount;
        this.transactionType = transactionType;
        this.geoCode = geoCode;
        this.note = note;
        this.expiration = expiration;
        this.extensionList = extensionList;
    }

    public static PspPaymentCreateRequestDto create(@NotNull Payment payment) {
        InteropPayment interopPayment = payment.getInteropPayment();
        if (interopPayment == null)
            return null;

        List<InteropExtension> extensions = interopPayment.getExtensions();
        List<ExtensionData> extensionList = extensions.isEmpty() ? null : extensions.stream().map(ExtensionData::create).collect(Collectors.toList());

        InteropIdentifierType requestIdentifier = PspSettings.getRequestAccountIdentifier();
        InteropPartyData debtor = InteropPartyData.create(payment.getDebtorIdentification(requestIdentifier == null ? InteropIdentifierType.ACCOUNT_ID : requestIdentifier), null);

        PaymentRisk risk = payment.getRisk();
        String merchantClassificationCode = risk == null ? null : risk.getMerchantCustomerIdentification();
        InteropPartyData creditor = InteropPartyData.create(payment.getCreditorIdentification(), merchantClassificationCode);

        return new PspPaymentCreateRequestDto(payment.getPaymentId(), debtor, creditor, interopPayment.getAmountType(),
                PspAmountData.create(payment), InteropTransactionTypeData.create(interopPayment), GeoCodeData.create(interopPayment),
                interopPayment.getNote(), payment.getExpiresOn(), extensionList);
    }

    @Override
    public String toString() {
        return "TransactionChannelRequestDTO{" +
                "clientRefId:'" + clientRefId + '\'' +
                ", payer:" + payer +
                ", payee:" + payee +
                ", amountType:" + amountType +
                ", amount:" + amount +
                ", transactionType:" + transactionType +
                ", note:'" + note + '\'' +
                ", expiration:" + expiration +
                '}';
    }
}
