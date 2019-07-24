/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.*;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import hu.dpc.ob.domain.type.LocalInstrumentCode;
import hu.dpc.ob.model.service.SeqNoGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PisInitiationData {

    @JsonProperty(value = "InstructionIdentification", required = true)
    @NotEmpty
    @Size(max = 36) // OB: 35, interoperation: 36
    private String instructionIdentification;

    @JsonProperty(value = "EndToEndIdentification", required = true)
    @NotEmpty
    @Size(max = 36) // OB: 35, interoperation: 36
    private String endToEndIdentification;

    @JsonProperty(value = "LocalInstrument")
    private LocalInstrumentCode localInstrument;

    @JsonProperty(value = "InstructedAmount", required = true)
    @NotNull
    @Valid
    private AmountData instructedAmount;

    @JsonProperty(value = "DebtorAccount")
    @Valid
    private AccountIdentificationData debtorAccount;

    @JsonProperty(value = "CreditorAccount", required = true)
    @NotNull
    @Valid
    private AccountIdentificationData creditorAccount;

    @JsonProperty(value = "CreditorPostalAddress")
    @Valid
    private PostalAddressData creditorPostalAddress;

    @JsonProperty(value = "RemittanceInformation")
    @Valid
    private RemittanceData remittanceInformation;

    @JsonProperty(value = "SupplementaryData")
    @Valid
    private SupplementaryData supplementaryData;

    public PisInitiationData(@NotEmpty @Size(max = 35) String instructionIdentification, @NotEmpty @Size(max = 35) String endToEndIdentification,
                             LocalInstrumentCode localInstrument, @NotNull AmountData instructedAmount, AccountIdentificationData debtorAccount,
                             @NotNull AccountIdentificationData creditorAccount, PostalAddressData creditorPostalAddress,
                             RemittanceData remittanceInformation, SupplementaryData supplementaryData) {
        this.instructionIdentification = instructionIdentification;
        this.endToEndIdentification = endToEndIdentification;
        this.localInstrument = localInstrument;
        this.instructedAmount = instructedAmount;
        this.debtorAccount = debtorAccount;
        this.creditorAccount = creditorAccount;
        this.creditorPostalAddress = creditorPostalAddress;
        this.remittanceInformation = remittanceInformation;
        this.supplementaryData = supplementaryData;
    }

    public PisInitiationData(@NotEmpty @Size(max = 35) String instructionIdentification, @NotEmpty @Size(max = 35) String endToEndIdentification,
                             @NotNull AmountData instructedAmount, @NotNull AccountIdentificationData creditorAccount) {
        this(instructionIdentification, endToEndIdentification, null, instructedAmount, null, creditorAccount, null, null, null);
    }

    @NotNull
    public static PisInitiationData create(@NotNull Payment payment) {
        AccountIdentificationData debtor = AccountIdentificationData.create(payment.getDebtorIdentification(InteropIdentifierType.ACCOUNT_ID));
        AccountIdentificationData creditor = AccountIdentificationData.create(payment.getCreditorIdentification());
        PostalAddressData postalAddress = PostalAddressData.create(payment.getCreditorPostalAddress());
        RemittanceData remittanceData = RemittanceData.create(payment.getRemittanceInformation());
        @NotNull SupplementaryData supplementaryData = SupplementaryData.create(payment.getInteropPayment());
        return new PisInitiationData(payment.getInstructionId(), payment.getEndToEndId(), payment.getLocalInstrument(),
                AmountData.create(payment), debtor, creditor, postalAddress, remittanceData, supplementaryData);
    }

    public Payment mapToEntity(@NotNull Consent consent, @NotNull SeqNoGenerator seqNoGenerator) {
        AccountIdentification debtor = debtorAccount == null ? null : debtorAccount.mapToEntity();
        AccountIdentification creditor = creditorAccount.mapToEntity();

        Address creditorAddress = creditorPostalAddress == null ? null : creditorPostalAddress.mapToEntity();

        Payment payment = Payment.create(consent, instructionIdentification, endToEndIdentification, localInstrument,
                instructedAmount.getAmount(), instructedAmount.getCurrency(), creditor, creditorAddress, seqNoGenerator);

        payment.addDebtorIdentification(debtor, true);

        Remittance remittance = getRemittanceInformation() == null ? null : remittanceInformation.mapToEntity(payment);
        payment.setRemittanceInformation(remittance);

        InteropPayment interopPayment = getSupplementaryData() == null ? null : supplementaryData.mapToEntity(payment);
        payment.setInteropPayment(interopPayment);

        return payment;
    }

    String updateEntity(@NotNull Payment payment) {
        if (!instructionIdentification.equals(payment.getInstructionId()))
            return "Consent instructionIdentification " + payment.getInstructionId() + " does not match requested instructionIdentification " + instructionIdentification;
        if (!endToEndIdentification.equals(payment.getEndToEndId()))
            return "Consent endToEndIdentification " + payment.getEndToEndId() + " does not match requested endToEndIdentification " + endToEndIdentification;
        if (localInstrument != null && !localInstrument.equals(payment.getLocalInstrument()))
            return "Consent localInstrument " + payment.getLocalInstrument() + " does not match requested localInstrument " + localInstrument;
        String failureReason = instructedAmount.updateEntity(payment);
        if (failureReason != null)
            return failureReason;
        if (debtorAccount != null) {
            failureReason = debtorAccount.updateEntity(payment.getDebtorIdentification(debtorAccount.getSchemeName()));
            if (failureReason != null)
                return failureReason;
        }
        failureReason = creditorAccount.updateEntity(payment.getCreditorIdentification());
        if (failureReason != null)
            return failureReason;
        if (creditorPostalAddress != null) {
            failureReason = creditorPostalAddress.updateEntity(payment);
            if (failureReason != null)
                return failureReason;
        }
        if (remittanceInformation != null) {
            failureReason = remittanceInformation.updateEntity(payment);
            if (failureReason != null)
                return failureReason;
        }
        if (supplementaryData != null) {
            failureReason = supplementaryData.updateEntity(payment);
            if (failureReason != null)
                return failureReason;
        }
        return null;
    }
}
