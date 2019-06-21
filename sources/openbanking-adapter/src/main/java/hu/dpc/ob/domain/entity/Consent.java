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
import hu.dpc.ob.rest.dto.ob.api.ConsentData;
import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.LocalDateTimeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consent_id"}, name = "uk_consent.consent"),
        @UniqueConstraint(columnNames = {"scope", "client_id", "user_id"}, name = "uk_consent.scope")})
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
    private List<ConsentEvent> events;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentStatusStep> statusSteps;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("id")
    private List<ConsentPermission> permissions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("accountId")
    private List<ConsentAccount> accounts;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "consent")
    @OrderBy("seqNo")
    private List<ConsentTransaction> transactions;


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
    public static Consent create(@NotNull ConsentData consentData, @NotNull ApiScope scope, @NotNull String clientId) {
        Consent consent = new Consent(UUID.randomUUID().toString(), scope, clientId, ConsentStatus.forAction(null, ConsentActionType.CREATE),
                DateUtils.getLocalDateTimeOfTenant(), consentData.getExpirationDateTime(), consentData.getTransactionFromDateTime(),
                consentData.getTransactionToDateTime());
        EventReasonCode reason = EventReasonCode.CLIENT_CONSENT_REQUESTED;
        ConsentEvent event = consent.addEvent(ConsentActionType.CREATE, reason.getCode(), reason.getDesc());
        return consent;
    }

    @NotNull
    public ConsentEvent action(ConsentActionType action, String reasonCode, String reasonDesc) {
        ConsentEvent event = addEvent(action, reasonCode, reasonDesc);
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

    private ConsentEvent addEvent(@NotNull ConsentActionType action, String reasonCode, String reasonDesc) {
        ConsentEvent lastEvent = getLastEvent();
        ConsentEvent event = new ConsentEvent(this, action, DateUtils.getLocalDateTimeOfTenant(),
                (lastEvent == null ? 0 : lastEvent.getSeqNo() + 1), reasonCode, reasonDesc);
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

    public ConsentPermission addPermission(@NotNull ApiPermission apiPermission, @NotNull ConsentEvent event) {
        ConsentPermission permission = new ConsentPermission(this, apiPermission, event);
        getPermissions().add(permission);
        return permission;
    }

    public ConsentAccount addAccount(@NotNull String accountid, @NotNull ConsentEvent event) {
        ConsentAccount account = new ConsentAccount(this, accountid, event);
        getAccounts().add(account);
        return account;
    }

    @NotNull
    public ConsentTransaction transaction(@NotNull BigDecimal amount, @NotNull String currency, @NotNull AmountType amountType,
                                          @NotNull Scenario scenario, @NotNull TransactionRole initiator, @NotNull IdentifierType payerIdType,
                                          @NotNull String payerId, @NotNull IdentifierType payeeIdType, @NotNull String payeeId) {
        EventReasonCode reason = EventReasonCode.USER_TRANSACTION;
        ConsentEvent event = addEvent(ConsentActionType.INTEROP_TRANSACTION, reason.getCode(), reason.getDesc()); // TODO
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
