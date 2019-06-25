/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

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
        ACCOUNTS_HELD("accounts-held", "Read consent accounts list"),
        PARTY("party", "Read PSP user details"),
        CONSENT("consent", "GET user consent resource details"),
        CONSENT_PUT("consent-update", "Update a user consent resource"),
        ;

        private @NotNull final String configName;
        private @NotNull final String displayText;

        @Override
        public String getDisplayLabel() {
            return configName;
        }
    }
}
