/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.config.type.Header;
import hu.dpc.ob.config.type.Operation;
import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.domain.type.RequestSource;
import hu.dpc.ob.model.internal.ApiSchema;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.PermissionCode.*;

@ConfigurationProperties("api-settings")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ApiSettings extends SchemaSettings<ApiSettings.ApiHeader, ApiSettings.ApiOperation, ApiSettings.ApiBinding> {

    @Autowired
    public ApiSettings(AdapterSettings adapterSettings) {
        super(adapterSettings);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

    @Override
    @NotNull
    public RequestSource getSource() {
        return RequestSource.API;
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
    public List<PermissionCode> getValidPermissions(ApiSchema schema, ApiScope scope) {
        return PermissionCode.getPermissions(scope).stream().filter(p -> getSchema(schema).getPermissions().contains(p.getId())).collect(Collectors.toList());
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum ApiHeader implements Header {
        AUTH_DATE("auth-date"),
        CUSTOMER_IP_ADDRESS("customer-ip-address"),
        INTERACTION_ID("interaction-id"),
        TRANSACTIONS_FROM("transactions_from"),
        TRANSACTIONS_TO("transactions_to"),
        ;

        private @NotNull final String configName;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum ApiBinding implements Binding {
        ACCOUNTS("accounts", ApiScope.AIS, ConsentActionCode.QUERY_ACCOUNT, true, "Read accounts list",
                new PermissionCode[]{READ_ACCOUNTS_BASIC, READ_ACCOUNTS_DETAIL}, new PermissionCode[]{READ_ACCOUNTS_DETAIL}),
        ACCOUNT("account", ApiScope.AIS, ConsentActionCode.QUERY_ACCOUNT, true, "Read account information",
                new PermissionCode[]{READ_ACCOUNTS_BASIC, READ_ACCOUNTS_DETAIL}, new PermissionCode[]{READ_ACCOUNTS_DETAIL}),
        BALANCES("balances", ApiScope.AIS, ConsentActionCode.QUERY_BALANCE, true, "Read balances of owned accounts",
                new PermissionCode[]{READ_BALANCES}),
        BALANCE("balance", ApiScope.AIS, ConsentActionCode.QUERY_BALANCE, true, "Read account balance",
                new PermissionCode[]{READ_BALANCES}),
        TRANSACTIONS("transactions", ApiScope.AIS, ConsentActionCode.QUERY_TRANSACTION, true, "Read balances of owned accounts",
                new PermissionCode[]{READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL}, new PermissionCode[]{READ_TRANSACTIONS_DETAIL}),
        TRANSACTION("transaction", ApiScope.AIS, ConsentActionCode.QUERY_TRANSACTION, true, "Read account balance",
                new PermissionCode[]{READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL}, new PermissionCode[]{READ_TRANSACTIONS_DETAIL}),
        PARTY_PSU("psu-party", ApiScope.AIS, ConsentActionCode.QUERY_PARTY, true, "Read party information on the PSU logged in",
                new PermissionCode[]{READ_PARTY_PSU}),
        PARTY("account-party", ApiScope.AIS, ConsentActionCode.QUERY_PARTY, true, "Read account owner information",
                new PermissionCode[]{READ_PARTY}),
        AIS_CONSENT_CREATE("ais-consent-create", ApiScope.AIS, ConsentActionCode.CREATE, false, "Create a new AIS consent resource"),
        AIS_CONSENT("ais-consent", ApiScope.AIS, ConsentActionCode.QUERY_CONSENT, false, "Read an AIS consent resource"),
        AIS_CONSENT_DELETE("ais-consent-delete", ApiScope.AIS, ConsentActionCode.DELETE, false, "User deletes an AIS consent resource"),
        PIS_CONSENT_CREATE("pis-consent-create", ApiScope.PIS, ConsentActionCode.CREATE, false, "Create a new domestic PIS consent resource"),
        PIS_CONSENT("pis-consent", ApiScope.PIS, ConsentActionCode.QUERY_CONSENT, false, "Read a domestic PIS consent resource"),
        FUNDS("funds", ApiScope.PIS, ConsentActionCode.QUERY_CONSENT, true, "Read funds on a domestic PIS consent resource",
                new PermissionCode[]{READ_BALANCES, READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL}),
        PAYMENT_CREATE("payment-create", ApiScope.PIS, ConsentActionCode.PAYMENT, true, "Initiate domestic payment",
                new PermissionCode[]{READ_BALANCES, READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL}),
        PAYMENT("payment", ApiScope.PIS, ConsentActionCode.QUERY_PAYMENT, false, "Read a domestic PIS resource"),
        CLIENT_PAYMENT("client-payment", ApiScope.PIS, ConsentActionCode.QUERY_PAYMENT, false, "Read a domestic PIS resource by client reference identifier",
                new PermissionCode[]{READ_BALANCES, READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL}),
        PAYMENT_DETAILS("payment-details", ApiScope.PIS, ConsentActionCode.QUERY_PAYMENT, false, "Read a domestic PIS details"),
        ;


        private @NotNull final String configName;
        private @NotNull final ApiScope scope;
        private @NotNull final ConsentActionCode actionCode;
        private @NotNull final boolean userRequest;
        private @NotNull final String displayText;
        private @NotNull final PermissionCode[] permissions;
        private @NotNull final PermissionCode[] detailPermissions;

        ApiBinding(@NotNull String configName, @NotNull ApiScope scope, @NotNull ConsentActionCode actionCode, @NotNull boolean userRequest,
                   @NotNull String displayText) {
            this(configName, scope, actionCode, userRequest, displayText, null, null);
        }

        ApiBinding(@NotNull String configName, @NotNull ApiScope scope, @NotNull ConsentActionCode actionCode, @NotNull boolean userRequest,
                   @NotNull String displayText, PermissionCode[] permissions) {
            this(configName, scope, actionCode, userRequest, displayText, permissions, permissions);
        }

        public PermissionCode[] getPermissions(boolean detail) {
            return null;
        }

        @Override
        @NotNull
        public RequestSource getSource() {
            return RequestSource.API;
        }

        @Override
        @NotNull
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
