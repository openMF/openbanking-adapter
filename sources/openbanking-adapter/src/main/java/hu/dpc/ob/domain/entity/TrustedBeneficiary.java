/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "trusted_beneficiary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_identification_id"}, name = "uk_trusted_beneficiary.account")})
public final class TrustedBeneficiary extends AbstractEntity {

    @NotNull
    @OneToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_identification_id", nullable = false)
    private AccountIdentification account;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    TrustedBeneficiary(@NotNull AccountIdentification account, @NotNull LocalDateTime createdOn, LocalDateTime expiresOn) {
        this.account = account;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    TrustedBeneficiary(@NotNull AccountIdentification account, LocalDateTime expiresOn) {
        this(account, DateUtils.getLocalDateTimeOfTenant(), expiresOn);
    }

    TrustedBeneficiary(@NotNull AccountIdentification account) {
        this(account, DateUtils.getLocalDateTimeOfTenant(), null);
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }
}
