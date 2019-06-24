/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.type.DisplayType;
import hu.dpc.ob.domain.type.IdentifierType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum IdentificationType implements DisplayType {

    @JsonProperty("UK.OBIE.BBAN")
    BBAN("UK.OBIE.BBAN", "Basic Bank Account Number (BBAN)"),
    @JsonProperty("UK.OBIE.IBAN")
    IBAN("UK.OBIE.IBAN", "An identifier used internationally by financial institutions"),
    @JsonProperty("UK.OBIE.PAN")
    PAN("UK.OBIE.PAN", "Primary Account Number to identify a card account"),
    @JsonProperty("UK.OBIE.Paym")
    PAYM("UK.OBIE.Paym", "Paym Scheme to make payments via mobile"),
    @JsonProperty("UK.OBIE.SortCodeAccountNumber")
    OBIE("UK.OBIE.SortCodeAccountNumber", "Sort Code and Account Number - identifier scheme used in the UK by financial institutions"),
;

    @NotNull
    private final String apiName;
    @NotNull
    private final String displayName;

    @Override
    public String getDisplayLabel() {
        return apiName;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }

    public static IdentificationType forInteropIdType(@NotNull IdentifierType idType) {
        switch (idType) {
            case MSISDN:
                return PAYM;
            case ACCOUNT_ID:
                return BBAN;
            case IBAN:
                return IBAN;
            case DEVICE:
                return PAN;
            default:
                return null;
        }
    }
}
