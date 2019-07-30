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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "trusted_client", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id"}, name = "uk_trusted_client.client")})
public final class TrustedClient extends AbstractEntity {


    @NotNull
    @Column(name = "client_id", nullable = false, length = 128)
    private String clientId;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    TrustedClient(@NotNull String clientId, @NotNull LocalDateTime createdOn, LocalDateTime expiresOn) {
        this.clientId = clientId;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    TrustedClient(@NotNull String clientId, LocalDateTime expiresOn) {
        this(clientId, DateUtils.getLocalDateTimeOfTenant(), expiresOn);
    }

    TrustedClient(@NotNull String clientId) {
        this(clientId, DateUtils.getLocalDateTimeOfTenant(), null);
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }
}
