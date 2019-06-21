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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"api_user_id"}, name = "uk_user.api_user"),
        @UniqueConstraint(columnNames = {"psp_user_id"}, name = "uk_user.psp_user")})
public class User extends AbstractEntity {

    @NotNull
    @Column(name = "api_user_id", nullable = false, length = 128)
    private String apiUserId;

    @NotNull
    @Column(name = "psp_user_id", nullable = false, length = 128)
    private String pspUserId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @OrderBy("id")
    private List<Consent> consents;
}
