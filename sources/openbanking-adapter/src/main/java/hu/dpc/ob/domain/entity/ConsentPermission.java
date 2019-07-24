/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.PermissionCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id", "permission_code"}, name = "uk_consent_permission.permission")})
public final class ConsentPermission extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id", nullable = false)
    private Consent consent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_code", nullable = false)
    private PermissionCode permission;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private ConsentEvent event;

    ConsentPermission(@NotNull Consent consent, @NotNull PermissionCode permission, @NotNull ConsentEvent event) {
        this.consent = consent;
        this.permission = permission;
        this.event = event;
    }
}
