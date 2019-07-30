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
import hu.dpc.ob.domain.type.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

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

    /** @return the identifier type which can be used for deptor account to start payment, null if there is no special need */
    public static InteropIdentifierType getRequestAccountIdentifier() {
        return InteropIdentifierType.MSISDN;
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
//        BALANCES("balances", "Read balance information"),
        TRANSACTIONS("transactions", "Read payment information"),
        IDENTIFIERS("identifiers", "Read secondary identifiers for an accountId"),
        PARTY_BY_IDENTIFIER("party-by-identifier", "Read Interoperation Account by secondary identifier"),
        PARTY_BY_SUBIDENTIFIER("party-by-subidentifier", "Read Interoperation Account by secondary sub-identifier"),
        QUOTE_CREATE("quote-create", "Calculate Interoperation Quote"),
        PAYMENT_CREATE("payment-create", "Start payment transaction"),
        PAYMENT("payment", "Read payment status"),
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
