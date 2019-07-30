/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.*;
import hu.dpc.ob.model.ConsentStateMachine;
import hu.dpc.ob.model.service.SeqNoGenerator;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id"}, name = "uk_consent.consent")})
public final class Consent extends AbstractEntity implements Comparable<Consent> {

    @NotNull
    @Column(name = "consent_id", nullable = false, length = 128)
    private String consentId; // Unique identification as assigned by the ASPSP to uniquely identify the consent resource.

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_code", nullable = false, length = 8)
    private ApiScope scope;

    @NotNull
    @Column(name = "client_id", nullable = false, length = 128)
    private String clientId;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false, length = 128)
    private ConsentStatusCode status;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn; // Specified cut-off date and time for the consent.

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "transaction_from")
    private LocalDateTime transactionFrom;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "transaction_to")
    private LocalDateTime transactionTo;

    @Setter(AccessLevel.PUBLIC)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "consent")
    private Payment payment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentEvent> events = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    private List<ConsentPermission> permissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    private List<ConsentAccount> accounts = new ArrayList<>();

    public Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, User user, @NotNull ConsentStatusCode status,
                   @NotNull LocalDateTime createdOn, LocalDateTime updatedOn, LocalDateTime expiresOn, LocalDateTime transactionFrom,
                   LocalDateTime transactionTo) {
        this.consentId = consentId;
        this.scope = scope;
        this.clientId = clientId;
        this.user = user;
        this.status = status;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.expiresOn = expiresOn;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
    }

    Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, ConsentStatusCode status,
                   @NotNull LocalDateTime createdOn, LocalDateTime expiresOn, LocalDateTime transactionFrom, LocalDateTime transactionTo) {
        this(consentId, scope, clientId, null, status, createdOn, null, expiresOn, transactionFrom, transactionTo);
    }

    Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, ConsentStatusCode status,
                   @NotNull LocalDateTime createdOn) {
        this(consentId, scope, clientId, status, createdOn, null, null, null);
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isAlive() {
        return status.isAlive();
    }

    @NotNull
    public static Consent create(@NotNull String clientId, @NotNull ApiScope scope, @NotNull SeqNoGenerator seqNoGenerator) {
        Consent consent = new Consent(UUID.randomUUID().toString(), scope, clientId, null, DateUtils.getLocalDateTimeOfTenant());
        consent.action(ConsentActionCode.CREATE, EventStatusCode.ACCEPTED, consent.getConsentId(), null, null, seqNoGenerator);
        return consent;
    }

    @NotNull
    public static Consent create(@NotNull String clientId, @NotNull ApiScope scope, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                                 LocalDateTime transactionToDateTime, List<PermissionCode> permissions, @NotNull SeqNoGenerator seqNoGenerator) {
        @NotNull Consent consent = create(clientId, scope, seqNoGenerator);
        consent.setExpiresOn(expirationDateTime);
        consent.setTransactionFrom(transactionFromDateTime);
        consent.setTransactionTo(transactionToDateTime);
        ConsentEvent event = consent.getLastEvent(ConsentActionCode.CREATE);
        if (event != null && permissions != null) {
            for (PermissionCode permission : permissions) {
                consent.addPermission(permission, event);
            }
        }
        return consent;
    }

    public ConsentEvent action(@NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId, ConsentEvent cause,
                               String reasonCode, String reasonDesc, @NotNull SeqNoGenerator seqNoGenerator) {
        ConsentStatusCode newStatus = ConsentStateMachine.handleTransition(this.status, action, status);
        ConsentEvent event = mergeEvent(action, status, resourceId, this.status == newStatus ? null : newStatus, cause,
                reasonCode, reasonDesc, seqNoGenerator);
        if (event != null) {
            if (this.status != newStatus) {
                event.setConsentStatus(newStatus);
                if (this.status != null)
                    setUpdatedOn(event.getCreatedOn());
                setStatus(newStatus);
            }
            addEvent(event, seqNoGenerator);
            return event;
        }
        return null;
    }

    public ConsentEvent action(@NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId, String reasonCode,
                               String reasonDesc, @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, status, resourceId, null, reasonCode, reasonDesc, seqNoGenerator);
    }

    public ConsentEvent action(@NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId, EventReasonCode reasonCode,
                               @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, status, resourceId, reasonCode == null ? null : reasonCode.getId(), reasonCode == null ? null : reasonCode.getDisplayText(),
                seqNoGenerator);
    }

    public ConsentEvent action(@NotNull ConsentActionCode action, ConsentEvent cause, @NotNull SeqNoGenerator seqNoGenerator) {
        return action(action, EventStatusCode.ACCEPTED, null, cause, null, null, seqNoGenerator);
    }

    private ConsentEvent mergeEvent(@NotNull ConsentActionCode action, @NotNull EventStatusCode status, String resourceId,
                                    ConsentStatusCode consentStatus, ConsentEvent cause, String reasonCode, String reasonDesc,
                                    @NotNull SeqNoGenerator seqNoGenerator) {
        ConsentEvent lastEvent = getLastEvent(action);
        ConsentEvent newEvent = new ConsentEvent(this, action, status, resourceId, DateUtils.getLocalDateTimeOfTenant(),
                seqNoGenerator.getConsentEventNextSeqNo(), consentStatus, cause, reasonCode, reasonDesc);
        if (lastEvent == null)
            return newEvent;
        newEvent = lastEvent.merge(newEvent);
        return newEvent == lastEvent ? null : newEvent;
    }

    private boolean addEvent(@NotNull ConsentEvent event, @NotNull SeqNoGenerator seqNoGenerator) {
        boolean added = getEvents().add(event);
        event.setConsent(this);
        if (added && getPayment() != null) {
            payment.consentEventAdded(event, seqNoGenerator);
        }
        return added;
    }

    public ConsentEvent getFirstEvent() {
        List<ConsentEvent> events = getEvents();
        return events.isEmpty() ? null : events.get(0);
    }

    public ConsentEvent getLastEvent() {
        List<ConsentEvent> events = getEvents();
        int size = events.size();
        return size == 0 ? null : events.get(size - 1);
    }

    public ConsentEvent getLastEvent(ConsentActionCode action) {
        List<ConsentEvent> events = getEvents(action);
        int size = events.size();
        return size == 0 ? null : this.events.get(size - 1);
    }

    public List<ConsentEvent> getEvents(@NotNull ConsentActionCode action) {
        return getEvents().stream().filter(e -> e.getAction() == action).sorted().collect(Collectors.toList());
    }

    boolean paymentEventAdded(@NotNull PaymentEvent event, @NotNull SeqNoGenerator seqNoGenerator) {
        if (!event.isAccepted())
            return false;

        ConsentActionCode action = ConsentActionCode.forAction(event.getAction());
        if (action != null) {
            action(action, EventStatusCode.ACCEPTED, event.getPayment().getPaymentId(), event.getCause(), event.getReason(), event.getReasonDesc(), seqNoGenerator);
        }
        return false;
    }

    public boolean hasPermission(@NotNull PermissionCode permission) {
        return getPermission(permission) != null;
    }

    public ConsentPermission getPermission(@NotNull PermissionCode apiPermission) {
        for (ConsentPermission permission : getPermissions()) {
            if (permission.getPermission() == apiPermission)
                return permission;
        }
        return null;
    }

    public ConsentPermission addPermission(@NotNull PermissionCode apiPermission, @NotNull ConsentEvent event) {
        ConsentPermission permission = new ConsentPermission(this, apiPermission, event);
        getPermissions().add(permission);
        return permission;
    }

    public void mergePermissions(List<PermissionCode> permissionCodes, @NotNull ConsentEvent event) {
        if (permissionCodes == null)
            return;

        for (int i = getPermissions().size(); --i >= 0; ) {
            ConsentPermission consentPermission = permissions.get(i);
            if (!permissionCodes.contains(consentPermission.getPermission()))
                permissions.remove(consentPermission);
        }
        for (PermissionCode permissionCode : permissionCodes) {
            if (!hasPermission(permissionCode))
                addPermission(permissionCode, event);
        }
    }

    public boolean hasAccount(@NotNull String accountId) {
        return getAccount(accountId) != null;
    }

    public ConsentAccount getAccount(@NotNull String accountId) {
        for (ConsentAccount account : getAccounts()) {
            if (account.getAccountId().equals(accountId))
                return account;
        }
        return null;
    }

    public ConsentAccount addAccount(@NotNull String accountid, @NotNull ConsentEvent event) {
        ConsentAccount account = new ConsentAccount(this, accountid, event);
        getAccounts().add(account);
        return account;
    }

    public void mergeAccounts(List<String> accountIds, @NotNull ConsentEvent event) {
        if (accountIds == null)
            return;

        for (int i = getAccounts().size(); --i >= 0; ) {
            ConsentAccount consentAccount = accounts.get(i);
            if (!accountIds.contains(consentAccount.getAccountId()))
                accounts.remove(consentAccount);
        }
        for (String accountId : accountIds) {
            if (!hasAccount(accountId))
                addAccount(accountId, event);
        }
    }

    public boolean isExpired() {
        return expiresOn != null && DateUtils.getLocalDateTimeOfTenant().isAfter(expiresOn);
    }

    @Override
    public int compareTo(Consent o) {
        return getId() == null
                ? o.getId() == null ? 0 : 1
                : o.getId() == null ? -1 : getId().compareTo(o.getId());
    }
}
