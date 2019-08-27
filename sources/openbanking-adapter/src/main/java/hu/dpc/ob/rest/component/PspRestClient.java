/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.component;

import hu.dpc.ob.config.AuthProperties;
import hu.dpc.ob.config.OperationProperties;
import hu.dpc.ob.config.PspSettings;
import hu.dpc.ob.config.UriProperties;
import hu.dpc.ob.config.type.AuthEncodeType;
import hu.dpc.ob.domain.type.InteropIdentifierType;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.dto.psp.*;
import hu.dpc.ob.util.ContextUtils;
import hu.dpc.ob.util.DateUtils;
import hu.dpc.ob.util.JsonUtils;
import lombok.NoArgsConstructor;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class PspRestClient {

    private static Logger log = LoggerFactory.getLogger(PspRestClient.class);

    private RestClient restClient;

    private PspSettings pspSettings;

    private Map<String, TenantAuth> tenantAuthDataCache = new HashMap<>();


    @Autowired
    public PspRestClient(RestClient restClient, PspSettings pspSettings) {
        this.restClient = restClient;
        this.pspSettings = pspSettings;
    }

    @PostConstruct
    public void postConstruct() {
        AuthEncodeType encode = pspSettings.getAuth().getEncode();
        pspSettings.getAdapterSettings().getTenants().forEach(tenant -> {
            TenantAuth tenantAuthData = new TenantAuth();
            @NotEmpty String tenantName = tenant.getName();
            UriProperties authOp = pspSettings.getOperationProps(PspSettings.PspOperation.AUTH, tenantName);
            tenantAuthData.setUser(authOp.getUser());
            tenantAuthData.setPassword(encode.encode(authOp.getPassword()));
            tenantAuthData.setTenant(tenantName);
            tenantAuthDataCache.put(tenantName, tenantAuthData);
        });
    }

    public PspAccountResponseDto callAccount(String accountId, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.ACCOUNT);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", account: %s", accountId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), accountId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, HttpMethod.GET, headers, null);

        log.debug(String.format("Response PSP  " + method + " /" + opProps.getName() + ", account: %s, payload: %s", accountId, responseJson));
        return JsonUtils.toPojo(responseJson, PspAccountResponseDto.class); // TODO: response class for CN
    }

    public PspAccountsResponseDto callAccounts(String pspUserId, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.ACCOUNTS);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", user: %s", pspUserId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), pspUserId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP  " + method + " /" + opProps.getName() + ", user: %s, payload: %s", pspUserId, responseJson));
        return JsonUtils.toPojo(responseJson, PspAccountsResponseDto.class); // TODO: response class for CN
    }

    public PspTransactionsResponseDto callTransactions(String accountId, PspId pspId, boolean debit, boolean credit, LocalDateTime fromBookingDateTime, LocalDateTime toBookingDateTime) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.TRANSACTIONS);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", account: %s", accountId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), accountId) + "?debit=" + debit + "&credit=" + credit;
        if (fromBookingDateTime != null)
            url += "&fromBookingDateTime=" + DateUtils.formatIsoDateTime(fromBookingDateTime);
        if (toBookingDateTime != null)
            url += "&toBookingDateTime=" + DateUtils.formatIsoDateTime(toBookingDateTime);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, HttpMethod.GET, headers, null);

        log.debug(String.format("Response PSP  " + method + " /" + opProps.getName() + ", account: %s, payload: %s", accountId, responseJson));
        return JsonUtils.toPojo(responseJson, PspTransactionsResponseDto.class); // TODO: response class for CN
    }

    public PspIdentifiersResponseDto callIdentifiers(String accountId, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.IDENTIFIERS);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", psp: %s, accountId: %s", pspId, accountId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), accountId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", psp: %s, accountId: %s, payload: %s", pspId, accountId, responseJson));
        return JsonUtils.toPojo(responseJson, PspIdentifiersResponseDto.class);
    }

    public PspPartyByIdentifierResponseDto callPartyByIdentitier(InteropIdentifierType idType, String idValue, String subIdOrType, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(subIdOrType == null ? PspSettings.PspOperation.PARTY_BY_IDENTIFIER : PspSettings.PspOperation.PARTY_BY_SUBIDENTIFIER);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", psp: %s, idType: %s, idValue: %s, subIdOrType: %s",
                pspId, idType, idValue, subIdOrType));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = subIdOrType == null
                ? ContextUtils.resolvePathParams(tenantProps.getUrl(), idType.name(), idValue)
                : ContextUtils.resolvePathParams(tenantProps.getUrl(), idType.name(), idValue, subIdOrType);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", psp: %s, idType: %s, idValue: %s, " +
                "subIdOrType: %s, payload: %s", pspId, idType, idValue, subIdOrType, responseJson));
        return JsonUtils.toPojo(responseJson, PspPartyByIdentifierResponseDto.class);
    }

    public PspClientResponseDto callClient(String pspUserId, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.CLIENT);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", user: %s", pspUserId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), pspUserId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP  " + method + " /" + opProps.getName() + ", user: %s, payload: %s", pspUserId, responseJson));
        return JsonUtils.toPojo(responseJson, PspClientResponseDto.class); // TODO: response class for CN
    }

    public PspQuoteResponseDto callQuoteCreate(@NotNull PspQuoteRequestDto request, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.QUOTE_CREATE);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", psp: %s, paymentId: %s, quoteId: %s",
                pspId, request.getTransactionCode(), request.getQuoteCode()));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = tenantProps.getUrl();

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, method, headers, JsonUtils.toJson(request));

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", psp: %s, paymentId: %s, quoteId: %s, " +
                "payload: %s", pspId, request.getTransactionCode(), request.getQuoteCode(), responseJson));
        return JsonUtils.toPojo(responseJson, PspQuoteResponseDto.class);
    }

    public PspPaymentCreateResponseDto callPaymentCreate(@NotNull PspPaymentCreateRequestDto request, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.PAYMENT_CREATE);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", psp: %s, paymentId: %s", pspId, request.getClientRefId()));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = tenantProps.getUrl();

        Map<String, String> headers = getHeaders(tenant);
        headers.put(pspSettings.getHeaderProps(PspSettings.PspHeader.TRANSACTION_TENANT).getKey(), tenant);

        String responseJson = restClient.call(url, method, headers, JsonUtils.toJson(request));

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", psp: %s, paymentId: %s, " +
                "payload: %s", pspId, request.getClientRefId(), responseJson));
        return JsonUtils.toPojo(responseJson, PspPaymentCreateResponseDto.class);
    }

    public PspPaymentResponseDto callPayment(@NotNull String transactionId, PspId pspId) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.PAYMENT);
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", psp: %s, transactionId: %s", pspId, transactionId));

        String tenant = pspId.getTenant();
        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = ContextUtils.resolvePathParams(tenantProps.getUrl(), transactionId);

        Map<String, String> headers = getHeaders(tenant);
        headers.put(pspSettings.getHeaderProps(PspSettings.PspHeader.TRANSACTION_TENANT).getKey(), tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", psp: %s, transactionId: %s, " +
                "payload: %s", pspId, transactionId, responseJson));
        return JsonUtils.toPojo(responseJson, PspPaymentResponseDto.class);
    }

    private Map<String, String> getHeaders(String tenant) {
        Map<String, String> headers = new HashMap<>();
        TenantAuth tenantAuthData = getTenantAuthData(tenant);

        headers.put(HttpHeader.AUTHORIZATION.asString(), tenantAuthData.getCachedAuthHeader());
        headers.put(pspSettings.getHeaderProps(PspSettings.PspHeader.TENANT).getKey(), tenant);
        headers.put(pspSettings.getHeaderProps(PspSettings.PspHeader.USER).getKey(), tenantAuthData.getUser());

        return headers;
    }

    private TenantAuth getTenantAuthData(String tenant) {
        TenantAuth tenantAuthData = tenantAuthDataCache.get(tenant);
        if (tenantAuthData == null) {
            throw new RuntimeException(String.format("Could not call login on PSP, because the provided tenant is not configured! Tenant: %s", tenant));
        }
        if (StringUtils.isEmpty(tenantAuthData.getCachedAuthHeader()) || accessTokenExpired(tenantAuthData.getAccessTokenExpiration())) {
            login(tenantAuthData);
        }
        return tenantAuthData;
    }

    /**
     * Logins with the provided tenantAuthData and updates the accessToken and accessTokenExpiration entries in the passed tenantAuthData parameter
     *
     * @param tenantAuthData tenantAuthData that should be used for the login
     */
    private void login(TenantAuth tenantAuthData) {
        OperationProperties opProps = pspSettings.getOperationProps(PspSettings.PspOperation.AUTH);
        String tenant = tenantAuthData.getTenant();
        @NotNull HttpMethod method = opProps.getHttpMethod();
        log.debug(String.format("Call PSP " + method + " /" + opProps.getName() + ", tenant: %s", tenant));

        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = tenantProps.getUrl() + "?grant_type=password&username=" + tenantAuthData.getUser() + "&password=" + tenantAuthData.getPassword();

        Map<String, String> headers = new HashMap<>();
        headers.put(pspSettings.getHeaderProps(PspSettings.PspHeader.TENANT).getKey(), tenant);

        String responseJson = restClient.call(url, method, headers, null);

        log.debug(String.format("Response PSP " + method + " /" + opProps.getName() + ", tenant: %s, payload: %s", tenant, responseJson));

        AuthProperties auth = pspSettings.getAuth();
        PspLoginResponseDto loginResponseDTO = (PspLoginResponseDto) JsonUtils.toPojo(responseJson, opProps.getBodyClass());
        tenantAuthData.setCachedAuthHeader(auth.getProfile().encode(loginResponseDTO.getAccessToken()));
        tenantAuthData.setAccessTokenExpiration(loginResponseDTO.getAccessTokenExpiration());
    }

    private boolean accessTokenExpired(Date accessTokenExpiration) {
        if (accessTokenExpiration == null)
            return false;

        Date fiveMinsFromNow = new Date(System.currentTimeMillis() + 300 * 1000);
        return accessTokenExpiration.before(fiveMinsFromNow);
    }

    public static class TenantAuth {
        private String tenant;
        private String cachedAuthHeader;
        private String user;
        private String password;
        private Date accessTokenExpiration;

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }

        public String getCachedAuthHeader() {
            return cachedAuthHeader;
        }

        public void setCachedAuthHeader(String cachedAuthHeader) {
            this.cachedAuthHeader = cachedAuthHeader;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Date getAccessTokenExpiration() {
            return accessTokenExpiration;
        }

        public void setAccessTokenExpiration(Date accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
        }
    }
}
