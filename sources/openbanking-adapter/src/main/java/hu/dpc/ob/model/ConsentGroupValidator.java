/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
public interface ConsentGroupValidator {

    Short getMaxFrequency();

    Short getMaxNumber();

    BigDecimal getMaxAmount(@NotNull String currency);

    LocalDateTime calcExpiresOn();

    LocalDateTime calcExpiresOn(@NotNull LocalDateTime createdOn);
}
