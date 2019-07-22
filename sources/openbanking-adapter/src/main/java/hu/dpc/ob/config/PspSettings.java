/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.domain.type.ApiScope;
import hu.dpc.ob.domain.type.ConsentActionCode;
import hu.dpc.ob.domain.type.PermissionCode;
import hu.dpc.ob.domain.type.RequestSource;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("psp-settings")
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspSettings extends UriSettings<PspSettings.PspHeader, PspSettings.PspOperation, PspSettings.PspBinding> {

    private AuthProperties auth;

    private AdapterSettings adapterSettings;


    @Autowired
    public PspSettings(AdapterSettings adapterSettings) {
        this.adapterSettings = adapterSettings;
    }

    @PostConstruct
    public void postConstruct() {
        postConstruct(adapterSettings);
        postConstruct((UriSettings) null);
    }

    @Override
    public RequestSource getSource() {
        return RequestSource.PSP;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum PspHeader implements Header {
        USER("user"),
        TENANT("tenant"),
        TRANSACTION_TENANT("transaction-tenant"),
        ;

        private final String configName;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum PspBinding implements Binding {
        ;

        private @NotNull final String configName;
        private @NotNull final ApiScope scope;
        private @NotNull final ConsentActionCode actionCode;
        private @NotNull final boolean userRequest;
        private @NotNull final String displayText;

        public PermissionCode[] getPermissions(boolean detail) {
            return null;
        }

        @Override
        @NotNull
        public RequestSource getSource() {
            return RequestSource.PSP;
        }

        @Override
        @NotNull
        public String getDisplayLabel() {
            return configName;
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum PspOperation implements Operation {
        AUTH("auth", "Tech Uses authentication"),
        ACCOUNTS("accounts", "Read accounts list"),
        ACCOUNT("account", "Read account information"),
        CLIENT("client", "Read client information"),
        ACCOUNT_BALANCES("account-balances", "Read balance information"),
        ACCOUNT_TRANSACTIONS("account-transactions", "Read payment information"),
        IDENTIFIERS("identifiers", "Read secondary identifiers for an accountId"),
        PARTY_BY_IDENTIFIER("party-by-identifier", "Query Interoperation Account by secondary identifier"),
        PARTY_BY_SUBIDENTIFIER("party-by-subidentifier", "Query Interoperation Account by secondary sub-identifier"),
        QUOTE_CREATE("quote-create", "Calculate Interoperation Quote"),
        TRANSACTION_CREATE("transaction-create", "Start payment transaction"),
        TRANSACTION("transaction", "Read transaction status"),
        ;

        @NotNull
        private final String configName;
        @NotNull
        private final String displayText;

        @Override
        public String getDisplayLabel() {
            return configName;
        }
    }
}
