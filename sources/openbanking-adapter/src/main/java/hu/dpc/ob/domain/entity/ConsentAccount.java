/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

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
@Table(name = "consent_account", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id", "account_id"}, name = "uk_consent_account.accountId")})
public final class ConsentAccount extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id", nullable = false)
    private Consent consent;

    @NotNull
    @Column(name = "account_id", nullable = false, length = 128)
    private String accountId;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private ConsentEvent event;


    ConsentAccount(@NotNull Consent consent, @NotNull String accountId, @NotNull ConsentEvent event) {
        this.consent = consent;
        this.accountId = accountId;
        this.event = event;
    }
}
