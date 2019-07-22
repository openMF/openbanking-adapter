/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
/**
 * This element is used to specify a payment local instrument, local clearing option and/or further qualify the service or service level.
 */
public enum LocalInstrumentCode implements DisplayType {

    @JsonProperty("UK.OBIE.BACS")
    BACS("BACS", "Back Payment Scheme"),
    @JsonProperty("UK.OBIE.CHAPS")
    CHAPS("CHAPS", "CHAPS Payment Scheme"),
    @JsonProperty("UK.OBIE.FPS")
    FPS("FPS", "Faster Payment Scheme"),
    @JsonProperty("UK.OBIE.SWIFT")
    SWIFT("SWIFT", "Swift Payment Service"),
    @JsonProperty("UK.OBIE.BalanceTransfer")
    BALANCE_TRANSFER("BalanceTransfer", "To indicate Balance Transfer"),
    @JsonProperty("UK.OBIE.MoneyTransfer")
    MONEY_TRANSFER("MoneyTransfer", "To Indicate Money Transfer"),
    @JsonProperty("UK.OBIE.Paym")
    PAYM("MobilePaym", "Paym Scheme to make payments via mobile"),
    @JsonProperty("UK.OBIE.Euro1")
    EURO1("Euro1", "To use Euro1 Payment System"),
    @JsonProperty("UK.OBIE.SEPACreditTransfer")
    SEPA_CREDIT_TRANSFER("SEPACreditTransfer", "To indicate SEPA Credit Transfer payment service"),
    @JsonProperty("UK.OBIE.SEPAInstantCreditTransfer")
    SEPA_INSTANT_CREDIT_TRANSFER("SEPAInstantCreditTransfer", "To indicate SEPA Instant Credit Transfer payment service"),
    @JsonProperty("UK.OBIE.Link")
    LINK("Link", "To indicate Link payment service"),
    @JsonProperty("UK.OBIE.Target2")
    TARGET2("Target2", "To indicate Target2 payment service"),
;

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    @Override
    public String getDisplayLabel() {
        return id;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }
}
