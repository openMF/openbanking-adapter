/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AmountType;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.domain.type.EventReasonCode;
import hu.dpc.ob.domain.type.IdentifierType;
import hu.dpc.ob.domain.type.Scenario;
import hu.dpc.ob.domain.type.TransactionRole;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id"}, name = "uk_consent.consent")/* ,
        @UniqueConstraint(columnNames = {"scope", "client_id", "user_id", "status"}, name = "uk_consent.scope") */})
public class Consent extends AbstractEntity {

    @NotNull
    @Column(name = "consent_id", nullable = false, length = 128)
    private String consentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private ApiScope scope;

    @NotNull
    @Column(name = "client_id", nullable = false, length = 128)
    private String clientId;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 128)
    private ConsentStatus status;

    @Column(name = "transaction_id", length = 128)
    private String transactionId;

    @NotNull
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "transaction_from")
    private LocalDateTime transactionFrom;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "transaction_to")
    private LocalDateTime transactionTo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentEvent> events = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentStatusStep> statusSteps = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    private List<ConsentPermission> permissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    private List<ConsentAccount> accounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentTransaction> transactions = new ArrayList<>();


    Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, User user, @NotNull ConsentStatus status,
            String transactionId, @NotNull LocalDateTime createdOn, LocalDateTime updatedOn, LocalDateTime expiresOn,
            LocalDateTime transactionFrom, LocalDateTime transactionTo) {
        this.consentId = consentId;
        this.scope = scope;
        this.clientId = clientId;
        this.user = user;
        this.status = status;
        this.transactionId = transactionId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.expiresOn = expiresOn;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
    }

    Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull ConsentStatus status,
                   @NotNull LocalDateTime createdOn, LocalDateTime expiresOn, LocalDateTime transactionFrom, LocalDateTime transactionTo) {
        this(consentId, scope, clientId, null, status, null, createdOn, null, expiresOn, transactionFrom, transactionTo);
    }

    Consent(@NotNull String consentId, @NotNull ApiScope scope, @NotNull String clientId, @NotNull ConsentStatus status,
                   @NotNull LocalDateTime createdOn) {
        this(consentId, scope, clientId, null, status, null, createdOn, null, null, null, null);
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isAlive() {
        return status.isAlive();
    }

    @NotNull
    public static Consent create(@NotNull String clientId, @NotNull ApiScope scope, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                                 LocalDateTime transactionToDateTime, List<ApiPermission> permissions, Integer seqNo) {
        Consent consent = new Consent(UUID.randomUUID().toString(), scope, clientId, ConsentStatus.forAction(null, ConsentActionType.CREATE),
                DateUtils.getLocalDateTimeOfTenant(), expirationDateTime, transactionFromDateTime, transactionToDateTime);
        EventReasonCode reason = EventReasonCode.CLIENT_CONSENT_REQUESTED;
        ConsentEvent event = consent.addEvent(ConsentActionType.CREATE, reason.getCode(), reason.getDesc(), seqNo);
        if (permissions != null) {
            for (ApiPermission permission : permissions) {
                consent.addPermission(permission, event);
            }
        }
        return consent;
    }

    @NotNull
    public ConsentEvent action(ConsentActionType action, String reasonCode, String reasonDesc, Integer seqNo) {
        ConsentEvent event = addEvent(action, reasonCode, reasonDesc, seqNo);
        return action(action, event);
    }

    @NotNull
    public ConsentEvent action(ConsentActionType action, ConsentEvent event) {
        ConsentStatus newStatus = ConsentStatus.forAction(status, action);
        if (status != newStatus) {
            addStep(newStatus, event);
        }
        return event;
    }

    public ConsentEvent getLastEvent() {
        List<ConsentEvent> events = getEvents();
        int size = events.size();
        return size == 0 ? null : events.get(size - 1);
    }

    public ConsentEvent getLastEvent(ConsentActionType action) {
        List<ConsentEvent> events = getEvents();
        for (int i = 0; i < events.size(); i++) {
            ConsentEvent consentEvent = events.get(i);

        }
        int size = events.size();
        return size == 0 ? null : events.get(size - 1);
    }

    private ConsentEvent addEvent(@NotNull ConsentActionType action, String reasonCode, String reasonDesc, Integer seqNo) {
        ConsentEvent event = new ConsentEvent(this, action, DateUtils.getLocalDateTimeOfTenant(), seqNo, reasonCode, reasonDesc);
        getEvents().add(event);
        return event;
    }

    public ConsentStatusStep getLastStep() {
        List<ConsentStatusStep> statusSteps = getStatusSteps();
        int size = statusSteps.size();
        return size == 0 ? null : statusSteps.get(size - 1);
    }

    private ConsentStatusStep addStep(@NotNull ConsentStatus status, @NotNull ConsentEvent event) {
        ConsentStatusStep lastStep = getLastStep();
        ConsentStatusStep step = new ConsentStatusStep(this, status, event,
                (lastStep == null ? 0 : lastStep.getSeqNo() + 1));
        getStatusSteps().add(step);
        setStatus(status);
        return step;
    }

    public boolean hasPermission(@NotNull ApiPermission apiPermission) {
        return getPermission(apiPermission) != null;
    }

    public ConsentPermission getPermission(@NotNull ApiPermission apiPermission) {
        for (ConsentPermission permission : getPermissions()) {
            if (permission.getPermission() == apiPermission)
                return permission;
        }
        return null;
    }

    public ConsentPermission addPermission(@NotNull ApiPermission apiPermission, @NotNull ConsentEvent event) {
        ConsentPermission permission = new ConsentPermission(this, apiPermission, event);
        getPermissions().add(permission);
        return permission;
    }

    public void mergePermissions(List<ApiPermission> apiPermissions, @NotNull ConsentEvent event) {
        if (apiPermissions == null)
            getPermissions().clear();
        else {
            for (int i = getPermissions().size(); --i >= 0; ) {
                ConsentPermission consentPermission = permissions.get(i);
                if (!apiPermissions.contains(consentPermission.getPermission()))
                    permissions.remove(consentPermission);
            }
            for (ApiPermission apiPermission : apiPermissions) {
                if (!hasPermission(apiPermission))
                    addPermission(apiPermission, event);
            }
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
            getAccounts().clear();
        else {
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
    }

    @NotNull
    public ConsentTransaction transaction(@NotNull BigDecimal amount, @NotNull String currency, @NotNull AmountType amountType,
                                          @NotNull Scenario scenario, @NotNull TransactionRole initiator, @NotNull IdentifierType payerIdType,
                                          @NotNull String payerId, @NotNull IdentifierType payeeIdType, @NotNull String payeeId, Integer seqNo) {
        EventReasonCode reason = EventReasonCode.USER_TRANSACTION;
        ConsentEvent event = addEvent(ConsentActionType.INTEROP_TRANSACTION, reason.getCode(), reason.getDesc(), seqNo); // TODO
        return addTransaction(amount, currency, amountType, scenario, initiator, payerIdType, payerId, payeeIdType, payeeId, event);
    }

    public ConsentTransaction getLastTransaction() {
        List<ConsentTransaction> transactions = getTransactions();
        int size = transactions.size();
        return size == 0 ? null : transactions.get(size - 1);
    }

    private ConsentTransaction addTransaction(@NotNull BigDecimal amount, @NotNull String currency, @NotNull AmountType amountType,
                                              @NotNull Scenario scenario, @NotNull TransactionRole initiator, @NotNull IdentifierType payerIdType,
                                              @NotNull String payerId, @NotNull IdentifierType payeeIdType, @NotNull String payeeId,
                                              @NotNull ConsentEvent event) {
        ConsentTransaction lastTransaction = getLastTransaction();
        ConsentTransaction transaction = new ConsentTransaction(this, UUID.randomUUID().toString(), amount, currency, amountType,
                scenario, initiator, payerIdType, payerId, payeeIdType, payeeId, event, (lastTransaction == null ? 0 : lastTransaction.getSeqNo() + 1));
        getTransactions().add(lastTransaction);
        return transaction;
    }
}
