/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent_event", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seq_no"}, name = "uk_consent_event.seq")})
public class ConsentEvent extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id")
    private Consent consent;

    @NotNull
    @Enumerated(EnumType.STRING)
//    @Convert(converter = PersistentTypeEnumConverter.class)
    @Column(name = "action", nullable = false)
    private ConsentActionType action;

    @NotNull
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "seq_no", nullable = false)
    private int seqNo;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_code", length = 64)
    private String reasonCode;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_desc", length = 256)
    private String reasonDesc;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<ConsentStatusStep> statusSteps = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<ConsentPermission> permissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<ConsentAccount> accounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    @OrderBy("seqNo")
    private List<ConsentTransaction> transactions = new ArrayList<>();

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionType action, @NotNull LocalDateTime createdOn, int seqNo,
                        String reasonCode, String reasonDesc) {
        this.consent = consent;
        this.action = action;
        this.createdOn = createdOn;
        this.seqNo = seqNo;
        this.reasonCode = reasonCode;
        this.reasonDesc = reasonDesc;
    }

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionType action, @NotNull LocalDateTime createdOn, int seqNo) {
        this(consent, action, createdOn, seqNo, null, null);
    }

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionType action, int seqNo) {
        this(consent, action, DateUtils.getLocalDateTimeOfTenant(), seqNo, null, null);
    }
}
