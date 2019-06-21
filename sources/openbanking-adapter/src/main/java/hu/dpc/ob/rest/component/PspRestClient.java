/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.component;

import hu.dpc.ob.config.AuthEncodeType;
import hu.dpc.ob.config.AuthProperties;
import hu.dpc.ob.config.OperationProperties;
import hu.dpc.ob.config.PspSettings;
import hu.dpc.ob.config.TenantProperties;
import hu.dpc.ob.rest.dto.psp.PspAccountsResponseDto;
import hu.dpc.ob.rest.dto.psp.PspClientResponseDto;
import hu.dpc.ob.rest.dto.psp.PspLoginResponseDto;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.util.ContextUtils;
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
import javax.validation.constraints.NotNull;
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
            TenantProperties authOp = pspSettings.getOperation(PspSettings.PspOperation.AUTH, tenant);
            tenantAuthData.setUser(authOp.getUser());
            tenantAuthData.setPassword(encode.encode(authOp.getPassword()));
            tenantAuthData.setTenant(tenant);
            tenantAuthDataCache.put(tenant, tenantAuthData);
        });
    }

    public PspAccountsResponseDto callAccounts(String pspUserId, PspId pspId) {
        @NotNull String configName = PspSettings.PspOperation.ACCOUNTS.getConfigName();
        log.debug(String.format("Call PSP GET /" + configName + ". User: %s", pspUserId));

        String tenant = pspId.getTenant();
        TenantProperties operation = pspSettings.getOperation(PspSettings.PspOperation.ACCOUNTS, tenant);
        String url = ContextUtils.resolvePathParams(operation.getUrl(), pspUserId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, HttpMethod.GET, headers, null);

        log.debug(String.format("Response PSP GET /" + configName + ", user: %s, payload: %s", pspUserId, responseJson));
        return JsonUtils.toPojo(responseJson, PspAccountsResponseDto.class); // TODO: response class for CN
    }

    public PspClientResponseDto callClient(String pspUserId, PspId pspId) {
        @NotNull String configName = PspSettings.PspOperation.CLIENT.getConfigName();
        log.debug(String.format("Call GET /" + configName + ", user: %s", pspUserId));

        String tenant = pspId.getTenant();
        TenantProperties operation = pspSettings.getOperation(PspSettings.PspOperation.CLIENT, tenant);
        String url = ContextUtils.resolvePathParams(operation.getUrl(),"pspUserId", pspUserId);

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, HttpMethod.GET, headers, null);

        log.debug(String.format("Response PSP GET /" + configName + ". User: %s, payload: %s", pspUserId, responseJson));
        return JsonUtils.toPojo(responseJson, PspClientResponseDto.class); // TODO: response class for CN
    }

    private Map<String, String> getHeaders(String tenant) {
        Map<String, String> headers = new HashMap<>();
        TenantAuth tenantAuthData = getTenantAuthData(tenant);

        headers.put(HttpHeader.AUTHORIZATION.asString(), tenantAuthData.getCachedAuthHeader());
        headers.put(pspSettings.getHeader(PspSettings.PspHeader.TENANT).getKey(), tenant);
        headers.put(pspSettings.getHeader(PspSettings.PspHeader.USER).getKey(), tenantAuthData.getUser());

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
        String tenant = tenantAuthData.getTenant();
        OperationProperties opProps = pspSettings.getOperation(PspSettings.PspOperation.AUTH);
        TenantProperties tenantProps = pspSettings.getOperation(PspSettings.PspOperation.AUTH, tenant);
        String url = tenantProps.getUrl() + "?grant_type=password&username=" + tenantAuthData.getUser() + "&password=" + tenantAuthData.getPassword();

        Map<String, String> headers = new HashMap<>();
        headers.put(pspSettings.getHeader(PspSettings.PspHeader.TENANT).getKey(), tenant);

        String responseJson = restClient.call(url, HttpMethod.POST, headers, null);

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
