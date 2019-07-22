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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class GeoCodeData {

    @JsonProperty(required = true)
    @JsonFormat(pattern = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$")
    @NotNull
    private String longitude;

    @JsonProperty(required = true)
    @JsonFormat(pattern = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$")
    @NotNull
    private String latitude;

    GeoCodeData(@NotNull String longitude, @NotNull String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static GeoCodeData create(InteropPayment payment) {
        if (payment == null || payment.getLongitude() == null)
            return null;

        return new GeoCodeData(payment.getLongitude(), payment.getLatitude());
    }

    String updateEntity(@NotNull InteropPayment payment) {
        if (payment.getLongitude() == null)
            payment.setLongitude(longitude);
        else if (!longitude.equals(payment.getLongitude()))
            return "Consent longitude " + payment.getLongitude() + " does not match requested longitude " + longitude;

        if (payment.getLatitude() == null)
            payment.setLatitude(latitude);
        else if (!latitude.equals(payment.getLatitude()))
            return "Consent latitude " + payment.getLatitude() + " does not match requested latitude " + latitude;

        return null;
    }

}
