/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.entity;

import hu.dpc.ob.domain.type.AmountType;
import hu.dpc.ob.domain.type.IdentifierType;
import hu.dpc.ob.domain.type.InitiatorType;
import hu.dpc.ob.domain.type.Scenario;
import hu.dpc.ob.domain.type.TransactionRole;
import hu.dpc.ob.domain.type.TransactionStatus;
import hu.dpc.ob.util.LocalDateTimeConverter;
import hu.dpc.ob.util.PersistentTypeEnumConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@Entity
@Table(name = "consent_transaction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"transaction_id"}, name = "uk_consent_transaction.transaction"),
        @UniqueConstraint(columnNames = {"client_ref_id"}, name = "uk_consent_transaction.client_ref"),
        @UniqueConstraint(columnNames = {"seq_no"}, name = "uk_consent_transaction.seq")})
public class ConsentTransaction extends AbstractEntity {

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "consent_id")
    private Consent consent;

    @NotNull
    @Column(name = "transaction_id", nullable = false, length = 128)
    private String transactionid;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "client_ref_id", length = 128)
    private String clientRefid;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 22, scale = 4)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "amount_type", nullable = false, length = 32)
    private AmountType amountType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scenario", nullable = false, length = 32)
    private Scenario scenario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "initiator", nullable = false, length = 32)
    private TransactionRole initiator;

    @Setter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "initiator_type", length = 32)
    private InitiatorType initiatorType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payer_id_type", nullable = false, length = 32)
    private IdentifierType payerIdType;

    @NotNull
    @Column(name = "payer_id", nullable = false, length = 128)
    private String payerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payee_id_type", nullable = false, length = 32)
    private IdentifierType payeeIdType;

    @NotNull
    @Column(name = "payee_id", nullable = false, length = 128)
    private String payeeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 128)
//    @Convert(converter = PersistentTypeEnumConverter.class)
    private TransactionStatus status;

    @NotNull
    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private ConsentEvent event;

    @NotNull
    @Column(name = "seq_no", nullable = false)
    private int seqNo;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "performed_on")
    private LocalDateTime performedOn;

    @Setter(AccessLevel.PUBLIC)
//    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "note", length = 128)
    private String note;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "error_desc", length = 256)
    private String errorDesc;


    ConsentTransaction(@NotNull Consent consent, @NotNull String transactionid, String clientRefid, @NotNull BigDecimal amount,
                       @NotNull String currency, @NotNull AmountType amountType, @NotNull Scenario scenario, @NotNull TransactionRole initiator,
                       InitiatorType initiatorType, @NotNull IdentifierType payerIdType, @NotNull String payerId, @NotNull IdentifierType payeeIdType,
                       @NotNull String payeeId, @NotNull TransactionStatus status, @NotNull ConsentEvent event, int seqNo, LocalDateTime performedOn,
                       LocalDateTime expiresOn, String note, String errorCode, String errorDesc) {
        this.consent = consent;
        this.transactionid = transactionid;
        this.clientRefid = clientRefid;
        this.amount = amount;
        this.currency = currency;
        this.amountType = amountType;
        this.scenario = scenario;
        this.initiator = initiator;
        this.initiatorType = initiatorType;
        this.payerIdType = payerIdType;
        this.payerId = payerId;
        this.payeeIdType = payeeIdType;
        this.payeeId = payeeId;
        this.status = status;
        this.event = event;
        this.seqNo = seqNo;
        this.performedOn = performedOn;
        this.expiresOn = expiresOn;
        this.note = note;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    ConsentTransaction(@NotNull Consent consent, @NotNull String transactionid, @NotNull BigDecimal amount,
                       @NotNull String currency, @NotNull AmountType amountType, @NotNull Scenario scenario, @NotNull TransactionRole initiator,
                       @NotNull IdentifierType payerIdType, @NotNull String payerId, @NotNull IdentifierType payeeIdType,
                       @NotNull String payeeId, @NotNull TransactionStatus status, @NotNull ConsentEvent event, int seqNo) {
        this(consent, transactionid, null, amount, currency, amountType, scenario, initiator, null, payerIdType, payerId,
                payeeIdType, payeeId, status, event, seqNo, null, null, null, null, null);
    }

    ConsentTransaction(@NotNull Consent consent, @NotNull String transactionid, @NotNull BigDecimal amount,
                       @NotNull String currency, @NotNull AmountType amountType, @NotNull Scenario scenario, @NotNull TransactionRole initiator,
                       @NotNull IdentifierType payerIdType, @NotNull String payerId, @NotNull IdentifierType payeeIdType,
                       @NotNull String payeeId, @NotNull ConsentEvent event, int seqNo) {
        this(consent, transactionid, null, amount, currency, amountType, scenario, initiator, null, payerIdType, payerId,
                payeeIdType, payeeId, TransactionStatus.PENDING, event, seqNo, null, null, null, null, null);
    }
}
