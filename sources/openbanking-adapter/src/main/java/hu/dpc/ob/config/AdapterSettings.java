/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import hu.dpc.ob.rest.internal.ApiSchema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("adapter-settings")
@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unused")
public class AdapterSettings implements ApplicationSettings {

    private String instance;
    private Long expiration;
    @Getter(lazy = true)
    private final List<ApiSchema> schemas = new ArrayList<>(0);
    @Getter(lazy = true)
    private final List<String> tenants = new ArrayList<>(0);

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public enum AdapterSchema implements Schema {
        OPEN_BANKING("ob"),
        PSD2("psd2"),
        ;

        private @NotNull
        final String configName;
    }
}
