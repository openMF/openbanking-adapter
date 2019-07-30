/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class TenantDependentProperties extends UriProperties {

    @NotEmpty
    private String name;

    @NotEmpty
    private String method;

    private Class bodyClass;

    @Getter(lazy = true)
    @Valid
    private final Map<String, UriProperties> tenants = new HashMap<>();

    protected TenantDependentProperties(String name) {
        this.name = name;
    }

    protected abstract String getDefaultName();

    public boolean isDefault() {
        return getName().equals(getDefaultName());
    }

    @NotNull
    public HttpMethod getHttpMethod() {
        return HttpMethod.resolve(method);
    }

    public UriProperties getTenantProps(String tenant) {
        return getTenants().get(tenant);
    }

    protected UriProperties addTenantProps(String tenant, UriProperties properties) {
        getTenants().put(tenant, properties);
        return properties;
    }

    void postConstruct(TenantDependentProperties defaultProps) {
        super.postConstruct(defaultProps); // tenant independent default settings

        // empty is a valid value
        if (method == null)
            method = defaultProps.getMethod();
        if (bodyClass == null)
            bodyClass = defaultProps.getBodyClass();

        if (defaultProps != null && defaultProps != this) {
            for (String tenant : defaultProps.getTenants().keySet()) {
                UriProperties tenantProps = getTenantProps(tenant);
                if (tenantProps == null)
                    addTenantProps(tenant, new UriProperties());
            }
        }

        for (Map.Entry<String, UriProperties> tenantEntry : getTenants().entrySet()) {
            UriProperties tenantProps = tenantEntry.getValue();
            tenantProps.postConstruct(this); // tenant independent settings - strongest
            if (defaultProps != null && defaultProps != this) {
                tenantProps.postConstruct(defaultProps.getTenantProps(tenantEntry.getKey())); // update with default tenant dependent settings - weaker
                tenantProps.postConstruct(defaultProps); // update with default tenant independent default settings - weakest
            }
        }
    }
}
