/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.domain.entity.AccountIdentification;
import hu.dpc.ob.domain.entity.Charge;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.domain.type.EventReasonCode;
import hu.dpc.ob.domain.type.IdentificationCode;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.psp.PspAccountResponseDto;
import hu.dpc.ob.rest.dto.psp.PspPartyByIdentifierResponseDto;
import hu.dpc.ob.rest.dto.psp.PspQuoteRequestDto;
import hu.dpc.ob.rest.dto.psp.PspQuoteResponseDto;
import hu.dpc.ob.rest.processor.ob.ObRequestProcessor;
import hu.dpc.ob.util.MathUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

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
        AccountIdentification debtorIdentification = payment.getDebtorIdentification();
        String accountId = null;
        if (debtorIdentification != null && payment.getConsent().getAccounts().isEmpty()) {
            accountId = getAccountIdByIdentifier(debtorIdentification, pspId);
        }

        BigDecimal balance = null;
        Charge charge = null;
        EventReasonCode failedReason = accountId == null ? EventReasonCode.ACCOUNT_IDENTIFIER_NOT_FOUND : null;
        if (failedReason == null) {
            PspAccountResponseDto accountResponse = pspRestClient.callAccount(accountId, pspId);
            balance = accountResponse.getAccountBalance();
            if (!accountResponse.getApiAccountStatus().isEnabled())
                failedReason = ACCOUNT_NOT_ENABLED;
            else if (MathUtils.isLessThan(balance, payment.getAmount()))
                failedReason = NOT_ENOUGH_FUNDS;

            if (failedReason == null) {
                PspQuoteRequestDto quoteRequest = PspQuoteRequestDto.create(payment, UUID.randomUUID().toString(), accountId); // TODO store generated quote id
                PspQuoteResponseDto quoteResponse = pspRestClient.callQuoteCreate(quoteRequest, pspId);
                charge = quoteResponse.mapToEntity(payment);
                if (!quoteResponse.getState().isAccepted())
                    failedReason = PAYMENT_QUOTE_FAILED;
            }
        }
        return new DebtorInit(accountId, balance, charge, failedReason);
    }

    @RequiredArgsConstructor
    public static class DebtorInit {
        public final String accountId;
        public final BigDecimal balance;
        public final Charge charge;
        public final EventReasonCode failedReason;
    }

    @Transactional(MANDATORY)
    protected String getAccountIdByIdentifier(AccountIdentification identification, PspId pspId) {
        @NotNull IdentificationCode scheme = identification.getScheme();
        InteropIdentifierType interopType = scheme.getInteropType();
        if (interopType == null)
            return null;
        if (interopType == InteropIdentifierType.ACCOUNT_ID)
            return identification.getIdentification();

        PspPartyByIdentifierResponseDto partyResponse = pspRestClient.callPartyByIdentitier(interopType, identification.getIdentification(),
                identification.getSecondaryIdentification(), pspId);
        return partyResponse == null ? null : partyResponse.getAccountId();
    }
}
