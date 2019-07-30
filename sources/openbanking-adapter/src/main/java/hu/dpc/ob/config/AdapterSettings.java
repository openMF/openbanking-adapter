/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.config.type.ApplicationSettings;
import hu.dpc.ob.model.internal.ApiSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("adapter-settings")
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public class AdapterSettings implements ApplicationSettings {

    public static final int LIMIT_EVENT = 1;
    public static final int LIMIT_CONSENT = 2;
    public static final int LIMIT_PAYMENT = 3;
    public static final int LIMIT_SCA = 4;

    private String env;
    @NotEmpty
    private String instance;

    @Getter(lazy = true)
    @Valid
    private final List<ApiSchema> schemas = new ArrayList<>(0);

    @Getter(lazy = true)
    @Valid
    private final List<TenantConfig> tenants = new ArrayList<>(0);

    private LimitProperties eventLimits;
    private LimitProperties consentLimits;
    private LimitProperties paymentLimits;
    private LimitProperties scaLimits;


    public Short getMaxFrequency(int limitType) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.getMaxFrequency();
    }

    public Short getMaxNumber(int limitType) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.getMaxNumber();
    }

    public boolean hasExpiration(int limitType) {
        LimitProperties limits = getLimits(limitType);
        return limits != null && limits.getExpiration() != null;
    }

    public Duration getExpiration(int limitType) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.getExpiration();
    }

    public BigDecimal getMaxAmount(int limitType, @NotNull String currency) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.getMaxAmount(currency);
    }

    public LocalDateTime calcExpiresOn(int limitType, @NotNull LocalDateTime createdOn) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.calcExpiresOn(createdOn);
    }

    public LocalDateTime calcExpiresOn(int limitType) {
        LimitProperties limits = getLimits(limitType);
        return limits == null ? null : limits.calcExpiresOn();
    }

    public boolean isTestEnv() {
        return "test".equals(env);
    }

    private LimitProperties getLimits(int limitType) {
        switch (limitType) {
            case LIMIT_EVENT:
                return eventLimits;
            case LIMIT_CONSENT:
                return consentLimits;
            case LIMIT_PAYMENT:
                return paymentLimits;
            case LIMIT_SCA:
                return scaLimits;
            default:
                throw new UnsupportedOperationException("Unknown limit type: " + limitType);
        }
    }
}
