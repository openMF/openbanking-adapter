/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.api;

import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.Payment;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ConsentService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.api.FundsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspAccountResponseDto;
import hu.dpc.ob.util.ContextUtils;
import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.MathUtils;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component("api-ob-funds-processor")
public class FundsRequestProcessor extends ApiRequestProcessor {

    private PspRestClient pspRestClient;
    private ConsentService consentService;

    @Autowired
    public FundsRequestProcessor(PspRestClient pspRestClient, ConsentService consentService) {
        this.pspRestClient = pspRestClient;
        this.consentService = consentService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String consentId = ContextUtils.getPathParam(exchange, ContextUtils.PARAM_CONSENT_ID);
        @NotNull Consent consent = consentService.getConsentById(consentId);
        Payment payment = consent.getPayment();

        PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
        String accountId = payment.getDebtorAccountId(); // can not be null if consent was authorized

        PspAccountResponseDto accountResponse = pspRestClient.callAccount(accountId, pspId);

        LocalDateTime fundsDateTime = DateUtils.getLocalDateTimeOfTenant();
        boolean fundsAvailable = MathUtils.isGreaterThanOrEqualTo(accountResponse.getAvailableBalance(), payment.getRequiredAmount());

        FundsResponseDto response = FundsResponseDto.create(fundsDateTime, fundsAvailable);
        exchange.getIn().setBody(response);
    }
}
