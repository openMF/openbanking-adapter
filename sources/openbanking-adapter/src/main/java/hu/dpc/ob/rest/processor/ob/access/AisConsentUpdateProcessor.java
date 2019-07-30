/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor.ob.access;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.model.service.ApiService;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.rest.component.PspRestClient;
import hu.dpc.ob.rest.dto.ob.access.AisAccessConsentResponseDto;
import hu.dpc.ob.rest.dto.ob.access.AisConsentUpdateData;
import hu.dpc.ob.rest.dto.ob.access.AisConsentUpdateRequestDto;
import hu.dpc.ob.rest.dto.ob.api.AccountsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Component("access-ob-ais-consent-update-processor")
public class AisConsentUpdateProcessor extends AccessRequestProcessor {

    private final AdapterSettings adapterSettings;
    private final ApiService apiService;

    @Autowired
    public AisConsentUpdateProcessor(PspRestClient pspRestClient, AdapterSettings adapterSettings, ApiService apiService) {
        super(pspRestClient);
        this.adapterSettings = adapterSettings;
        this.apiService = apiService;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        super.process(exchange);

        String clientId = exchange.getProperty(ExchangeHeader.CLIENT_ID.getKey(), String.class);
        String apiUserId = exchange.getProperty(ExchangeHeader.API_USER_ID.getKey(), String.class);

        AisConsentUpdateRequestDto request = exchange.getProperty(ExchangeHeader.REQUEST_DTO.getKey(), AisConsentUpdateRequestDto.class);
        @NotNull AisConsentUpdateData data = request.getData();
        AccountsData accountsData = null;
        if (data.isAuthorize()) {
            String pspUserId = exchange.getProperty(ExchangeHeader.PSP_USER_ID.getKey(), String.class);
            PspId pspId = exchange.getProperty(ExchangeHeader.PSP_ID.getKey(), PspId.class);
            PspAccountsResponseDto pspAccounts = getPspRestClient().callAccounts(pspUserId, pspId);
            accountsData = AccountsData.transform(pspAccounts);
        }

        @NotNull Consent consent = apiService.updateConsent(apiUserId, clientId, request, accountsData, adapterSettings.isTestEnv());

        AisAccessConsentResponseDto response = AisAccessConsentResponseDto.create(consent);
        exchange.getIn().setBody(response);
    }
}
