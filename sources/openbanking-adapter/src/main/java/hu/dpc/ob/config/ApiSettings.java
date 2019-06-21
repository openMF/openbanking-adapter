/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.domain.type.ApiPermission;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.DisplayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties("api-settings")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ApiSettings extends SchemaSettings<ApiSettings.ApiHeader, ApiSettings.ApiOperation, ApiSettings.ApiBinding> {

    @Getter(lazy = true)
    private final List<String> permissions = new ArrayList<>(0);

    @Autowired
    public ApiSettings(AdapterSettings adapterSettings) {
        super(adapterSettings);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

    @Override
    protected ApiHeader[] getHeaders() {
        return ApiHeader.values();
    }

    @Override
    protected ApiBinding[] getBindings() {
        return ApiBinding.values();
    }

    @Override
    protected ApiOperation[] getOperations() {
        return ApiOperation.values();
    }

    @NotNull
    public List<ApiPermission> getValidPermissions(ApiScope scope) {
        return ApiPermission.getPermissions(scope).stream().filter(p -> getPermissions().contains(p.getApiName())).collect(Collectors.toList());
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum ApiHeader implements Header {
        AUTH_DATE("auth-date"),
        CUSTOMER_IP_ADDRESS("customer-ip-address"),
        INTERACTION_ID("interaction-id"),
        ;

        private @NotNull final String configName;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum ApiBinding implements Binding {
        ACCOUNTS("accounts", ApiScope.AIS, "Read accounts list"),
        ACCOUNT("account", ApiScope.AIS, "Read account information"),
        ACCOUNT_BALANCES("account-balances", ApiScope.AIS, "Read balance information"),
        ACCOUNT_TRANSACTIONS("account-transactions", ApiScope.AIS, "Read transaction information"),
        AIS_CONSENT("ais-consent", ApiScope.AIS, "Retrieve an AIS consent resource"),
        AIS_CONSENT_CREATE("ais-consent-create", ApiScope.AIS, "Create a new AIS consent resource"),
        ;

        private @NotNull final String configName;
        private @NotNull final ApiScope scope;
        private @NotNull final String displayText;

        @Override
        public String getDisplayLabel() {
            return configName;
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum ApiOperation implements Operation {
        ;

        private @NotNull final String configName;
        private @NotNull final String displayText;

        @Override
        public String getDisplayLabel() {
            return configName;
        }
    }
}
