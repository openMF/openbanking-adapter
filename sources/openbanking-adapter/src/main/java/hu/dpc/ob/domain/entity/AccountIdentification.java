/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.IdentificationCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "account_identification", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"scheme_code", "identification", "secondary_identification"}, name = "uk_account_identification.id")})
public final class AccountIdentification extends AbstractEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_code", length = 40, nullable = false)
    private IdentificationCode scheme; // Name of the identification scheme

    @NotEmpty
    @Size(max = 256)
    @Column(name = "identification", length = 256, nullable = false)
    private String identification; // Identification assigned by an institution to identify an accountId. This identification is known by the accountId owner.

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 34)
    @Column(name = "secondary_identification", length = 128)
    private String secondaryIdentification; // Assigned by the accountId servicing institution

    @Setter(AccessLevel.PUBLIC)
    @Size(max = 70)
    @Column(name = "name", length = 70)
    private String name; // Name or names of the accountId owner(s) represented at an accountId level, as displayed by the ASPSP's online channels

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "account")
    private TrustedBeneficiary trustedBeneficiary;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "account")
    private List<TrustedUserBeneficiary> trustedUserBeneficiaries = new ArrayList<>();
//
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "debtorIdentification")
//    private List<Payment> debtorPayments = new ArrayList<>();
//
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "creditorIdentification")
//    private List<Payment> creditorPayments = new ArrayList<>();

    public AccountIdentification(@NotNull IdentificationCode scheme, @NotEmpty @Size(max = 256) String identification,
                                 @Size(max = 34) String secondaryIdentification, @Size(max = 70) String name) {
        this.scheme = scheme;
        this.identification = identification;
        this.secondaryIdentification = secondaryIdentification;
        this.name = name;
    }

    public AccountIdentification(@NotNull IdentificationCode scheme, @NotEmpty @Size(max = 256) String identification) {
        this(scheme, identification, null, null);
    }

    public TrustedBeneficiary createTrustedBeneficiary(LocalDateTime expiresOn) {
        TrustedBeneficiary trusted = new TrustedBeneficiary(this, expiresOn);
        setTrustedBeneficiary(trusted);
        return trusted;
    }

    public TrustedUserBeneficiary addTrustedUserBeneficiary(@NotNull String clientId, @NotNull User user, BigDecimal limit,
                                                            LocalDateTime expiresOn) {
        TrustedUserBeneficiary trusted = new TrustedUserBeneficiary(clientId, user, this, limit, expiresOn);
        trustedUserBeneficiaries.add(trusted);
        return trusted;
    }
}
