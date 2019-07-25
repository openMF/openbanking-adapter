/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.domain.type.EventStatusCode;
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
public final class ConsentEvent extends AbstractEntity implements Comparable<ConsentEvent> {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id", nullable = false)
    private Consent consent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_code", nullable = false, length = 32)
    private ConsentActionCode action;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", length = 128, nullable = false)
    private EventStatusCode status;

    @Column(name = "resource_id", length = 128)
    private String resourceId;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "seq_no", nullable = false)
    private long seqNo;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_status_code", length = 128)
    private ConsentStatusCode consentStatus;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "cause_id")
    private ConsentEvent cause;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason", length = 64)
    private String reason;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "reason_desc", length = 256)
    private String reasonDesc;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<ConsentPermission> permissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event")
    private List<ConsentAccount> accounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cause")
    private List<ConsentEvent> results = new ArrayList<>();

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId,
                 @NotNull LocalDateTime createdOn, long seqNo, ConsentStatusCode consentStatus, ConsentEvent cause, String reason, String reasonDesc) {
        this.consent = consent;
        this.action = action;
        this.status = status;
        this.resourceId = resourceId;
        this.createdOn = createdOn;
        this.seqNo = seqNo;
        this.consentStatus = consentStatus;
        this.cause = cause;
        this.reason = reason;
        this.reasonDesc = reasonDesc;
    }

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId,
                 @NotNull LocalDateTime createdOn, long seqNo, ConsentStatusCode consentStatus, ConsentEvent cause) {
        this(consent, action, status, resourceId, createdOn, seqNo, consentStatus, cause, null, null);
    }

    ConsentEvent(@NotNull Consent consent, @NotNull ConsentActionCode action, @NotNull EventStatusCode status, @NotNull LocalDateTime createdOn,
                 long seqNo) {
        this(consent, action, status, null, createdOn, seqNo, null, null);
    }

    ConsentEvent merge(ConsentEvent newEvent) {
        if (newEvent.isAccepted())
            return newEvent;
        if (isAccepted())
            return newEvent;
        if (!this.getConsent().equals(newEvent.getConsent()))
            return newEvent;
        if (!getAction().equals(newEvent.getAction()))
            return newEvent;
        if (getConsentStatus() == null ? newEvent.getConsentStatus() != null : !getConsentStatus().equals(newEvent.getConsentStatus()))
            return newEvent;
        if (getCause() == null ? newEvent.getCause() != null : !getCause().equals(newEvent.getCause()))
            return newEvent;
        if (getResourceId() == null ? newEvent.getResourceId() != null : !getResourceId().equals(newEvent.getResourceId()))
            return newEvent;
        if (getReason() == null ? newEvent.getReason() != null : !getReason().equals(newEvent.getReason()))
            return newEvent;

        return this; // no need to add new event, maybe we should increase an occurance number later or log all requests
    }

    public boolean isAccepted() {
        return getStatus().isAccepted();
    }

    @Override
    public int compareTo(ConsentEvent o) {
        return Long.signum(getSeqNo() - o.getSeqNo());
    }
}
