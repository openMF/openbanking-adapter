/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.model.ConsentGroupValidator;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class LimitProperties implements ConsentGroupValidator {

    private Short frequency;
    private Short number;
    private Long expiration; // sec
    @Getter(lazy = true)
    private final List<AmountProperties> amounts = new ArrayList<>(0);


    @Override
    public Short getMaxFrequency() {
        return frequency;
    }

    @Override
    public Short getMaxNumber() {
        return number;
    }

    @Override
    public BigDecimal getMaxAmount(@NotNull String currency) {
        if (getAmounts() != null) {
            for (AmountProperties amount : getAmounts()) {
                if (amount.getCurrency().equals(currency))
                    return amount.getAmount();
            }
        }
        return null;
    }

    @Override
    public LocalDateTime calcExpiresOn(@NotNull LocalDateTime createdOn) {
        return getExpiration() == null ? null : createdOn.plus(expiration, ChronoUnit.SECONDS);
    }

    @Override
    public LocalDateTime calcExpiresOn() {
        return calcExpiresOn(DateUtils.getLocalDateTimeOfTenant());
    }
}
