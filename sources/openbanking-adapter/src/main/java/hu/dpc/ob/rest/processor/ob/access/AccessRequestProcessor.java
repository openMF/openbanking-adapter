/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.PspSettings;
import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.EventReasonCode;
import hu.dpc.ob.domain.type.IdentificationCode;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.psp.*;
import hu.dpc.ob.rest.processor.ob.ObRequestProcessor;
import hu.dpc.ob.util.MathUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.EventReasonCode.*;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Getter
public abstract class AccessRequestProcessor extends ObRequestProcessor {

    private PspRestClient pspRestClient;

    public AccessRequestProcessor(PspRestClient pspRestClient) {
        this.pspRestClient = pspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);
    }

    @Transactional(MANDATORY)
    @NotNull
    protected DebtorInit calcDebtorInit(@NotNull Payment payment, PspId pspId) {
        EventReasonCode failedReason = null;
        String failedMsg = null;

        AccountIdentification idIdentification = payment.getDebtorIdentification(InteropIdentifierType.ACCOUNT_ID);
        Map<InteropIdentifierType, AccountIdentification> identificationMap = null;
        String accountId = null;
        if (idIdentification != null && idIdentification.getSecondaryIdentification() == null)
            accountId = idIdentification.getIdentification();
        else {
            identificationMap = calcAccountIdIdentifications(payment.getOrigDebtorIdentification(), pspId);
            if (identificationMap == null) {
                failedReason = EventReasonCode.ACCOUNT_IDENTIFIER_NOT_FOUND;
                failedMsg = "Could not initialize debtor account identifiers";
            } else {
                idIdentification = identificationMap.get(InteropIdentifierType.ACCOUNT_ID);
                if (idIdentification == null) {
                    failedReason = EventReasonCode.ACCOUNT_IDENTIFIER_NOT_FOUND;
                    failedMsg = "Debtor account identifier " + InteropIdentifierType.ACCOUNT_ID + " is not registered";
                } else
                    accountId = idIdentification.getIdentification();
            }
        }

        InteropIdentifierType requestIdentifier = PspSettings.getRequestAccountIdentifier();
        if (requestIdentifier != null && payment.getDebtorIdentification(requestIdentifier) == null) {
            identificationMap = identificationMap == null ? calcAccountIdIdentifications(payment.getOrigDebtorIdentification(), pspId) : identificationMap;
            if (identificationMap == null) {
                failedReason = EventReasonCode.ACCOUNT_IDENTIFIER_NOT_FOUND;
                failedMsg = "Could not initialize debtor account identifiers";
            }
            else {
                AccountIdentification requestIdentification = identificationMap.get(requestIdentifier);
                if (requestIdentification == null) {
                    failedReason = EventReasonCode.ACCOUNT_IDENTIFIER_NOT_FOUND;
                    failedMsg = "Debtor account identifier " + requestIdentifier + " is not registered";
                }
            }
        }

        BigDecimal balance = null;
        Charge charge = null;
        if (failedReason == null) {
            PspAccountResponseDto accountResponse = pspRestClient.callAccount(accountId, pspId);
            balance = accountResponse.getAvailableBalance();
            if (!accountResponse.getApiAccountStatus().isEnabled()) {
                failedReason = ACCOUNT_NOT_ENABLED;
                failedMsg = failedReason.getDisplayText() + ", status: " + accountResponse.getApiAccountStatus();
            }
            else if (MathUtils.isLessThan(balance, payment.getAmount())) {
                failedReason = NOT_ENOUGH_FUNDS;
                failedMsg = failedReason.getDisplayText() + ", available: " + balance;
            }

            if (failedReason == null) {
                PspQuoteRequestDto quoteRequest = PspQuoteRequestDto.create(payment, UUID.randomUUID().toString(), accountId); // TODO store generated quote id
                PspQuoteResponseDto quoteResponse = pspRestClient.callQuoteCreate(quoteRequest, pspId);
                charge = quoteResponse.mapToEntity(payment);
                if (!quoteResponse.getState().isAccepted())
                    failedReason = PAYMENT_QUOTE_FAILED;
            }
        }
        return new DebtorInit(identificationMap, balance, charge, failedReason, failedMsg);
    }

    @RequiredArgsConstructor
    public static class DebtorInit {
        public final Map<InteropIdentifierType, AccountIdentification> identifierMap;
        public final BigDecimal balance;
        public final Charge charge;
        public final EventReasonCode failedReason;
        public final String failedMsg;

        public String getAccountId() {
            AccountIdentification idIdentification = identifierMap.get(InteropIdentifierType.ACCOUNT_ID);
            return idIdentification == null ? null : idIdentification.getIdentification();
        }
    }

    @Transactional(MANDATORY)
    protected Map<InteropIdentifierType, AccountIdentification> calcAccountIdIdentifications(AccountIdentification identification, PspId pspId) {
        if (identification == null)
            return null;

        @NotNull IdentificationCode scheme = identification.getScheme();
        InteropIdentifierType interopType = scheme.getInteropType();
        if (interopType == null)
            return null;

        String accountId;
        InteropIdentifierType idType = InteropIdentifierType.ACCOUNT_ID;
        if (interopType == idType && identification.getSecondaryIdentification() == null)
            accountId = identification.getIdentification();
        else {
            PspPartyByIdentifierResponseDto partyResponse = pspRestClient.callPartyByIdentitier(interopType, identification.getIdentification(),
                    identification.getSecondaryIdentification(), pspId);
            accountId = partyResponse == null ? null : partyResponse.getAccountId();
        }

        PspIdentifiersResponseDto identifiersResponse = pspRestClient.callIdentifiers(accountId, pspId);
        @NotNull List<AccountIdentification> identifications = identifiersResponse.mapToEntities();
        Map<InteropIdentifierType, AccountIdentification> identifierMap = identifications.stream().collect(Collectors.toMap(i -> i.getScheme().getInteropType(), i -> i));

        identifierMap.putIfAbsent(idType, new AccountIdentification(IdentificationCode.forInteropIdType(idType), accountId));

        return identifierMap;
    }
}
