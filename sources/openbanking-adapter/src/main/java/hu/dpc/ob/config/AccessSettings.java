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
@ConfigurationProperties("access-settings")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class AccessSettings extends SchemaSettings<AccessSettings.AccessHeader, AccessSettings.AccessOperation, AccessSettings.AccessBinding> {

    @Autowired
    public AccessSettings(AdapterSettings adapterSettings) {
        super(adapterSettings);
    }

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

    @Override
    @NotNull
    public RequestSource getSource() {
        return RequestSource.ACCESS;
    }

    @Override
    protected AccessHeader[] getHeaders() {
        return AccessHeader.values();
    }

    @Override
    protected AccessBinding[] getBindings() {
        return AccessBinding.values();
    }

    @Override
    protected AccessOperation[] getOperations() {
        return AccessOperation.values();
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum AccessHeader implements Header {
        USER("user"),
        ;

        private final String configName;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum AccessOperation implements Operation {
        USER_INFO("userinfo", "To obtain the claims for a user"),
        INTROSPECT("introspect", "User information that is contained within access tokens"),
        ;

        private @NotNull final String configName;
        private @NotNull final String displayText;

        @Override
        public String getDisplayLabel() {
            return configName;
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum AccessBinding implements Binding {
        ACCOUNTS_HELD("consent-accounts", ApiScope.AIS, ConsentActionCode.QUERY_ACCOUNT, true, "Read consent accounts list"),
        PARTY("consent-party", ApiScope.AIS, ConsentActionCode.QUERY_PARTY, true, "Read PSP user details"),
        AIS_CONSENT("ais-consent", ApiScope.AIS, ConsentActionCode.QUERY_CONSENT, true, "Read user consent resourceId details"),
        AIS_CONSENT_UPDATE("ais-consent-update", ApiScope.AIS, ConsentActionCode.AUTHORIZE, true, "Authorize user consent"),
        PIS_CONSENT_INIT("pis-consent-init", ApiScope.PIS, ConsentActionCode.PREPARE, true, "Prepare user payment consent"),
        PIS_CONSENT_UPDATE("pis-consent-update", ApiScope.PIS, ConsentActionCode.AUTHORIZE, true, "Authorize user payment consent"),
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
        public String getDisplayLabel() {
            return configName;
        }

        @Override
        @NotNull
        public RequestSource getSource() {
            return RequestSource.ACCESS;
        }
    }
}
