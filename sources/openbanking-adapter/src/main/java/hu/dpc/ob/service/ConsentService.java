/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.service;

import hu.dpc.ob.domain.ConsentStateMachine;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.ConsentAccount;
import hu.dpc.ob.domain.entity.ConsentEvent;
import hu.dpc.ob.domain.entity.ConsentPermission;
import hu.dpc.ob.domain.entity.ConsentRepository;
import hu.dpc.ob.domain.entity.ConsentStatusStep;
import hu.dpc.ob.domain.entity.ConsentTransaction;
import hu.dpc.ob.domain.entity.User;
import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionType;
import hu.dpc.ob.domain.type.ConsentStatus;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.access.ConsentAccountData;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateData;
import hu.dpc.ob.rest.dto.ob.access.ConsentUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsentService {

    private static Logger log = LoggerFactory.getLogger(PspRestClient.class);

    private final ConsentRepository consentRepository;

    @Autowired
    public ConsentService(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
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

        List<ConsentTransaction> transactions = consent.getTransactions();
        int size = transactions.size();
        if (size == 0)
            return consent.getCreatedOn();

        transactions.sort(Comparator.comparing(ConsentTransaction::getSeqNo)); // TODO: must be sorted by sqNo
        return transactions.get(size - 1).getEvent().getCreatedOn();
    }

    @Transactional
    public static List<ApiPermission> getPermissions(Consent consent) {
        if (consent == null)
            return null;
        return consent.getPermissions().stream().map(ConsentPermission::getPermission).collect(Collectors.toList());
    }

    @Transactional
    public Consent authorizeConsent(@NotNull User user, @NotNull ConsentUpdateRequestDto request) {
        if (user == null)
            throw new UnsupportedOperationException("User is not specified");

        // no need to implement update of already authorized consent - revoke and create new
        @NotNull ConsentUpdateData data = request.getData();
        @NotNull Consent consent = getConsentById(data.getConsentId());

        @NotNull ConsentEvent event = consent.action(data.getAction(), data.getReasonCode(), data.getReasonDesc());

        for (Consent userConsent : user.getConsents()) {
            if (userConsent.isAlive() && !userConsent.getId().equals(consent.getId()) && consent.getScope() == userConsent.getScope()) {
                log.info("Revoke user consent with id " + userConsent.getConsentId() + ", new consent was accepted " + consent.getConsentId());
                userConsent.action(ConsentActionType.REVOKE, event);
            }
        }

        consent.setUser(user);
        consent.setUpdatedOn(event.getCreatedOn());
        if (consent.isActive()) {
            data.getPermissions().forEach(p -> consent.addPermission(p, event));
            data.getAccounts().forEach(a -> consent.addAccount(a.getAccountId(), event));
        }
        return consent;
    }

    @Transactional
    public boolean hasPermission(Consent consent, @NotNull ApiPermission apiPermission, String accountId) {
        if (consent == null)
            return false;

        if (accountId != null) {
            boolean hasAccount = false;
            for (ConsentAccount account : consent.getAccounts()) {
                if (accountId.equals(account.getAccountId())) {
                    hasAccount = true;
                    break;
                }
            }
            if (!hasAccount)
                return false;
        }

        for (ConsentPermission permission : consent.getPermissions()) {
            if (permission.getPermission() == apiPermission)
                return true;
        }
        return false;
    }
}
