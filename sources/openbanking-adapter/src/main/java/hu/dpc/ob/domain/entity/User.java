/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AuthenticationApproachCode;
import hu.dpc.ob.domain.type.ConsentActionCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"api_user_id"}, name = "uk_user.api_user"),
        @UniqueConstraint(columnNames = {"psp_user_id"}, name = "uk_user.psp_user")})
public final class User extends AbstractEntity {

    @NotNull
    @Column(name = "api_user_id", nullable = false, length = 128)
    private String apiUserId;

    @NotNull
    @Column(name = "psp_user_id", nullable = false, length = 128)
    private String pspUserId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<Consent> consents = new ArrayList<>();

    public List<Payment> getTransactionsTillLastSca(@NotNull String clientId) {
        Consent consent = getConsents().stream().filter(c -> {
            if (!c.getClientId().equals(clientId))
                return false;
            Payment payment = c.getPayment();
            if (payment == null)
                return false;
            ScaSupport scaSupport = payment.getScaSupport();
            if (scaSupport == null)
                return false;
            ConsentEvent authEvent = c.getLastEvent(ConsentActionCode.AUTHORIZE);
            if (authEvent == null || !authEvent.isAccepted())
                return false;
            return scaSupport.getAuthenticationApproach() == AuthenticationApproachCode.SCA;
        }).sorted(Comparator.comparing(Consent::getPayment).reversed()).findFirst().orElse(null);

        if (consent == null)
            return null;

        Payment payment = consent.getPayment();

        return getConsents().stream().filter(c -> c.getPayment() != null && c.getPayment().compareTo(payment) > 0).map(Consent::getPayment).collect(Collectors.toList());
    }
}
