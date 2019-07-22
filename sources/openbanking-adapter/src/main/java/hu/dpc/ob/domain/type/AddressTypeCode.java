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
public enum AddressTypeCode implements PersistentType<AddressTypeCode, String> {

    @JsonProperty("Business")
    BUSINESS("Business"),
    @JsonProperty("Correspondence")
    CORRESPONDENCE("Correspondence"),
    @JsonProperty("DeliveryTo")
    DELIVERY_TO("DeliveryTo"),
    @JsonProperty("MailTo")
    MAIL_TO("MailTo"),
    @JsonProperty("POBox")
    PO_BOX("POBox"),
    @JsonProperty("Postal")
    POSTAL("Postal"),
    @JsonProperty("Residential")
    RESIDENTIAL("Residential"),
    @JsonProperty("Statement")
    STATEMENT("Statement"),
    ;


    private static final Map<String, AddressTypeCode> BY_ID = Arrays.stream(AddressTypeCode.values()).collect(Collectors.toMap(AddressTypeCode::getId, e -> e));

    @NotNull
    private final String id;

    public static AddressTypeCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return id;
    }
}
