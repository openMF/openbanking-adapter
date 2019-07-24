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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "interop_extension")
public final class InteropExtension extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "interop_payment_id", nullable = false)
    private InteropPayment interopPayment;

    @Setter(AccessLevel.PUBLIC)
    @NotEmpty
    @Size(min = 1, max = 32)
    @Column(name = "key", length = 32, nullable = false)
    private String key;

    @Setter(AccessLevel.PUBLIC)
    @NotEmpty
    @Size(min = 1, max = 128)
    @Column(name = "value", length = 128, nullable = false)
    private String value;

    InteropExtension(@NotNull InteropPayment interopPayment, @NotEmpty @Size(min = 1, max = 32) String key, @NotEmpty @Size(min = 1, max = 128) String value) {
        this.interopPayment = interopPayment;
        this.key = key;
        this.value = value;
    }
}
