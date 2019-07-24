/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AuthAction;
import hu.dpc.ob.domain.type.AuthStatusCode;
import hu.dpc.ob.domain.type.AuthorisationTypeCode;
import hu.dpc.ob.domain.type.ConsentActionCode;
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
@Table(name = "payment_authorization", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payment_id"}, name = "uk_payment_authorization.payment")})
public final class PaymentAuthorization extends AbstractEntity {

    @NotNull
    @OneToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "authorisation_type_code", nullable = false)
    private AuthorisationTypeCode authorisationType;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    // multi authorization

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false)
    private AuthStatusCode status;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "number_required", nullable = false)
    private short numberRequired;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "number_received", nullable = false)
    private short numberReceived;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    public PaymentAuthorization(@NotNull Payment payment, @NotNull AuthorisationTypeCode authorisationType, LocalDateTime expiresOn,
                                @NotNull AuthStatusCode status, short numberRequired, short numberReceived, LocalDateTime updatedOn) {
        this.payment = payment;
        this.authorisationType = authorisationType;
        this.expiresOn = expiresOn;
        this.status = status;
        this.numberRequired = numberRequired;
        this.numberReceived = numberReceived;
        this.updatedOn = updatedOn;
    }

    // simple authorization
    public PaymentAuthorization(@NotNull Payment payment, AuthorisationTypeCode authorisationType, LocalDateTime expiresOn) {
        this(payment, authorisationType, expiresOn, AuthStatusCode.AWAITING_FURTHER_AUTHORISATION, (short) 1, (short) 0, null);
    }

    boolean consentEventAdded(@NotNull ConsentEvent event) {
        AuthStatusCode newStatus = null;
        @NotNull ConsentActionCode action = event.getAction();
        if (action == ConsentActionCode.AUTHORIZE) {
            if (event.isAccepted()) {
                short numberReceived = getNumberReceived();
                setNumberReceived((short) (numberReceived + 1));
                if (numberReceived >= getNumberRequired()) {
                    newStatus = AuthStatusCode.forAction(status, AuthAction.AUTHORIZE);
                }
            }
            else {
                newStatus = AuthStatusCode.forAction(status, AuthAction.REJECT);
            }
        }
        else if (action == ConsentActionCode.REJECT && event.isAccepted()) {
            newStatus = AuthStatusCode.forAction(status, AuthAction.REJECT);
        }

        if (newStatus != null && this.status != newStatus) {
            setStatus(newStatus);
            if (this.status != null)
                setUpdatedOn(event.getCreatedOn());
            return true;
        }
        return false;
    }

    boolean paymentEventAdded(@NotNull PaymentEvent event) {
        return false;
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }
}
