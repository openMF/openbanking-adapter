/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.util.PersistentTypeEnumConverter;
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
@Table(name = "consent_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id", "permission"}, name = "uk_consent_permission.permission")})
public class ConsentPermission extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id")
    private Consent consent;

    @NotNull
    @Enumerated(EnumType.STRING)
//    @Convert(converter = PersistentTypeEnumConverter.class)
    @Column(name = "permission", nullable = false)
    private ApiPermission permission;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private ConsentEvent event;

    public ConsentPermission(@NotNull Consent consent, @NotNull ApiPermission permission, @NotNull ConsentEvent event) {
        this.consent = consent;
        this.permission = permission;
        this.event = event;
    }
}
