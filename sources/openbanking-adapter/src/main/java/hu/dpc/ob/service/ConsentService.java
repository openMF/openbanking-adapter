/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.service;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.ConsentAccount;
import hu.dpc.ob.domain.entity.ConsentEvent;
import hu.dpc.ob.domain.entity.ConsentEventRepository;
import hu.dpc.ob.domain.entity.ConsentPermission;
import hu.dpc.ob.domain.entity.ConsentRepository;
import hu.dpc.ob.domain.entity.ConsentTransaction;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.rest.dto.ob.access.ConsentAccountData;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateData;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import hu.dpc.ob.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsentService {

    private static Logger log = LoggerFactory.getLogger(ConsentService.class);

    private final ConsentRepository consentRepository;
    private final ConsentEventRepository consentEventRepository;

    @Autowired
    public ConsentService(ConsentRepository consentRepository, ConsentEventRepository consentEventRepository) {
        this.consentRepository = consentRepository;
        this.consentEventRepository = consentEventRepository;
    }

    @NotNull
    @Transactional
    public Consent getConsentById(String consentId) {
        Consent consent = consentRepository.findByConsentId(consentId);
        if (consent == null)
            throw new UnsupportedOperationException("Consent not found for consent_id " + consentId);

        return consent;
    }

    @Transactional
    public Consent getActiveConsent(User user, String clientId, ApiScope scope) {
        List<Consent> consents = user.getConsents();
        for (Consent consent : consents) {
            if (consent.getClientId().equals(clientId) && consent.getScope() == scope && consent.getStatus().isActive())
                return consent;
        }
        return null;
    }

    @Transactional
    public static LocalDateTime getTransactionFromDateTime(Consent consent) {
        if (consent == null)
            return null;
        if (consent.getTransactionFrom() != null)
            return consent.getTransactionFrom();

        List<ConsentTransaction> transactions = consent.getTransactions();
        if (transactions.isEmpty())
            return consent.getCreatedOn();

        transactions.sort(Comparator.comparing(ConsentTransaction::getSeqNo)); // TODO: must be sorted by sqNo
        return transactions.get(0).getEvent().getCreatedOn();
    }

    @Transactional
    public static LocalDateTime getTransactionToDateTime(Consent consent) {
        if (consent == null)
            return null;
        if (consent.getTransactionTo() != null)
            return consent.getTransactionTo();

        List<ConsentTransaction> transactions = consent.getTransactions();
        int size = transactions.size();
        if (size == 0)
            return consent.getCreatedOn();

        transactions.sort(Comparator.comparing(ConsentTransaction::getSeqNo)); // TODO: must be sorted by sqNo
        return transactions.get(size - 1).getEvent().getCreatedOn();
    }

    @Transactional
    public static List<ApiPermission> getPermissions(Consent consent) {
        return consent == null ? null : consent.getPermissions().stream().map(ConsentPermission::getPermission).collect(Collectors.toList());
    }

    @Transactional
    public Consent createConsent(String clientId, @NotNull ApiScope scope, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime,
                                 LocalDateTime transactionToDateTime, List<ApiPermission> permissions) {
        @NotNull Consent consent = Consent.create(clientId, scope, expirationDateTime, transactionFromDateTime, transactionToDateTime,
                permissions, getNextEventSeqNo());
        consentRepository.save(consent);
        return consent;
    }

    @Transactional
    public Consent authorizeConsent(@NotNull User user, @NotNull ConsentUpdateRequestDto request) {
        if (user == null)
            throw new UnsupportedOperationException("User is not specified");

        // no need to implement update of already authorized consent - revoke and create new
        @NotNull ConsentUpdateData data = request.getData();
        @NotNull Consent consent = getConsentById(data.getConsentId());

        @NotNull ConsentEvent event = consent.action(data.getAction(), data.getReasonCode(), data.getReasonDesc(), getNextEventSeqNo());
        consentEventRepository.save(event);

        for (Consent userConsent : user.getConsents()) {
            if (userConsent.isAlive() && consent.getScope() == userConsent.getScope() && !userConsent.getId().equals(consent.getId()) && userConsent.getClientId().equals(consent.getClientId())) {
                log.info("Revoke user consent with id " + userConsent.getConsentId() + ", new consent was accepted " + consent.getConsentId());
                userConsent.action(ConsentActionType.REVOKE, event);
                consentRepository.save(userConsent);
            }
        }

        consent.setUser(user);
        consent.setUpdatedOn(event.getCreatedOn());
        if (consent.isActive()) {
            consent.mergePermissions(data.getPermissions(), event);
            if (data.getAccounts() != null) {
                consent.mergeAccounts(data.getAccounts().stream().map(ConsentAccountData::getAccountId).collect(Collectors.toList()), event);
            }
        }
        consentRepository.save(consent);
        return consent;
    }

    @Transactional
    public boolean hasPermission(Consent consent, @NotNull ApiPermission apiPermission, String accountId) {
        if (consent == null) {
            log.info("No consent exist for permission: " + apiPermission + ", account:" + accountId);
            return false;
        }
        if (!consent.isActive()) {
            log.info("Consent is not active: " + consent);
            return false;
        }
        if (consent.getExpiresOn() != null && DateUtils.getLocalDateTimeOfTenant().isAfter(consent.getExpiresOn())) {
            log.info("Consent expired: " + consent);
            return false;
        }

        if (accountId != null) {
            boolean hasAccount = false;
            for (ConsentAccount account : consent.getAccounts()) {
                if (accountId.equals(account.getAccountId())) {
                    hasAccount = true;
                    break;
                }
            }
            if (!hasAccount) {
                log.info("Consent has no account permission: " + apiPermission + ", account:" + accountId);
                return false;
            }
        }

        return consent.getPermission(apiPermission) != null;
    }

    private int getNextEventSeqNo() {
        ConsentEvent max = consentEventRepository.findTopByOrderBySeqNoDesc();
        return max == null ? 0 : max.getSeqNo() + 1;
    }
}
