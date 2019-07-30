/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class LimitProperties {

    private Short frequency;
    private Short number;
    private Duration expiration;
    @Getter(lazy = true)
    private final List<AmountProperties> amounts = new ArrayList<>(0);


    public Short getMaxFrequency() {
        return frequency;
    }

    public Short getMaxNumber() {
        return number;
    }

    public BigDecimal getMaxAmount(@NotNull String currency) {
        if (getAmounts() != null) {
            for (AmountProperties amount : getAmounts()) {
                if (amount.getCurrency().equals(currency))
                    return amount.getAmount();
            }
        }
        return null;
    }

    public LocalDateTime calcExpiresOn(@NotNull LocalDateTime createdOn) {
        return getExpiration() == null ? null : createdOn.plusSeconds(expiration.getSeconds());
    }

    public LocalDateTime calcExpiresOn() {
        return calcExpiresOn(DateUtils.getLocalDateTimeOfTenant());
    }
}
