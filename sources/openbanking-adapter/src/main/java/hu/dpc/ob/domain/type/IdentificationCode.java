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
public enum IdentificationCode implements PersistentType<IdentificationCode, String>,  DisplayType {

    @JsonProperty("UK.OBIE.BBAN")
    BBAN("BBAN", "Basic Bank Account Number (BBAN)", InteropIdentifierType.ACCOUNT_ID),
    @JsonProperty("UK.OBIE.IBAN")
    IBAN("IBAN", "An identifier used internationally by financial institutions", InteropIdentifierType.IBAN),
    @JsonProperty("UK.OBIE.PAN")
    PAN("CardPAN", "Primary Account Number to identify a card accountId"),
    @JsonProperty("UK.OBIE.Paym")
    PAYM("MobilePaym", "Paym Scheme to make payments via mobile", InteropIdentifierType.MSISDN),
    @JsonProperty("UK.OBIE.SortCodeAccountNumber")
    SORT_CODE_ACCOUNT_NUMBER("UKSortCodeAccountNumber", "Sort Code and Account Number - identifier scheme used in the UK by financial institutions"),
    // interoperation extension
//    MSISDN("MSISDN", "MSISDN", InteropIdentifierType.MSISDN),
    EMAIL("EMAIL", "EMAIL", InteropIdentifierType.EMAIL),
    PERSONAL_ID("PERSONAL_ID", "PERSONAL ID", InteropIdentifierType.PERSONAL_ID),
    BUSINESS("BUSINESS", "BUSINESS", InteropIdentifierType.BUSINESS),
    DEVICE("DEVICE", "DEVICE", InteropIdentifierType.DEVICE),
//    ACCOUNT_ID("ACCOUNT_ID", "ACCOUNT ID", InteropIdentifierType.ACCOUNT_ID), // = BBAN
    // IBAN,
    ALIAS("ALIAS", "ALIAS", InteropIdentifierType.ALIAS),
;

    private static final Map<String, IdentificationCode> BY_ID = Arrays.stream(IdentificationCode.values()).collect(Collectors.toMap(IdentificationCode::getId, e -> e));
    private static final Map<InteropIdentifierType, IdentificationCode> BY_INTEROP_TYPE = Arrays.stream(IdentificationCode.values())
            .filter(e -> e.getInteropType() != null).collect(Collectors.toMap(IdentificationCode::getInteropType, e -> e));

    @NotNull
    private final String id;
    @NotNull
    private final String displayName;

    private final InteropIdentifierType interopType;

    IdentificationCode(@NotNull String id, @NotNull String displayName) {
        this(id, displayName, null);
    }

    public static IdentificationCode fromId(String id) {
        return BY_ID.get(id);
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

    public static IdentificationCode forInteropIdType(@NotNull InteropIdentifierType interopType) {
        return BY_INTEROP_TYPE.get(interopType);
    }
}
