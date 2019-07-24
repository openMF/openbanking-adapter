/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.rest.dto.ob.api.AccountIdentificationData;
import hu.dpc.ob.rest.dto.ob.api.CreditorTrustedData;
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
public class PisConsentUpdateData extends ConsentUpdateData {

    @JsonProperty(value = "DebtorAccount")
    @Valid
    private AccountIdentificationData debtorAccount;

    @JsonProperty(value = "CreditorTrusted")
    @Valid
    private CreditorTrustedData creditorTrusted;

    public PisConsentUpdateData(@NotEmpty @Size(max = 128) String consentId, @NotNull ConsentActionCode action, String reasonCode,
                                String reasonDesc, @Valid AccountIdentificationData debtorAccount, @Valid CreditorTrustedData creditorTrusted) {
        super(consentId, action, reasonCode, reasonDesc);
        this.debtorAccount = debtorAccount;
        this.creditorTrusted = creditorTrusted;
    }

    public String updateEntity(@NotNull Consent consent) {
        Payment payment = consent.getPayment();
        if (payment.getDebtorAccountId() == null) {
            if (debtorAccount == null)
                return "Debtor account must be specified for consent " + getConsentId();
            AccountIdentification deptorIdentification = debtorAccount.mapToEntity();
            payment.addDebtorIdentification(deptorIdentification, true);
        }
        String failureReason = null;
        if (debtorAccount != null) {
            failureReason = debtorAccount.updateEntity(payment.getDebtorIdentification(debtorAccount.getSchemeName()));
        }
        return failureReason;
    }
}
