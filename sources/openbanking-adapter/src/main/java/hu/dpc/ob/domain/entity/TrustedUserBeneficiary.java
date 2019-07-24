/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.MathUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "trusted_user_beneficiary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "user_id", "account_identification_id"}, name = "uk_trusted_user_beneficiary.account")})
public final class TrustedUserBeneficiary extends AbstractEntity {

    @NotNull
    @Column(name = "client_id", nullable = false, length = 128)
    private String clientId;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_identification_id", nullable = false)
    private AccountIdentification account;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "limit_amount", precision = 23, scale = 5)
    private BigDecimal limit;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    TrustedUserBeneficiary(@NotNull String clientId, @NotNull User user, @NotNull AccountIdentification account, BigDecimal limit,
                                  @NotNull LocalDateTime createdOn, LocalDateTime expiresOn) {
        this.clientId = clientId;
        this.user = user;
        this.account = account;
        this.limit = limit;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    TrustedUserBeneficiary(@NotNull String clientId, @NotNull User user, @NotNull AccountIdentification account, BigDecimal limit,
                           LocalDateTime expiresOn) {
        this(clientId, user, account, limit, DateUtils.getLocalDateTimeOfTenant(), expiresOn);
    }

    TrustedUserBeneficiary(@NotNull String clientId, @NotNull User user, @NotNull AccountIdentification account) {
        this(clientId, user, account, null, null);
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }

    public boolean isAboveLimit(@NotNull BigDecimal amount) {
        return limit != null && MathUtils.isGreaterThan(amount, limit);
    }
}
