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
import java.util.ArrayList;
import java.util.List;

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
    private final List<TenantProperties> tenants = new ArrayList<>();

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

    public TenantProperties getTenantProps(String tenant) {
        for (TenantProperties channelTenant : getTenants()) {
            if (channelTenant.getName().equals(tenant))
                return channelTenant;
        }
        return null;
    }

    protected TenantProperties addTenantProps(TenantProperties properties) {
        getTenants().add(properties);
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
            for (TenantProperties defaultTenant : defaultProps.getTenants()) {
                @NotEmpty String tenantName = defaultTenant.getName();
                TenantProperties tenantProps = getTenantProps(tenantName);
                if (tenantProps == null)
                    addTenantProps(new TenantProperties(tenantName));
            }
        }

        for (TenantProperties tenantProps : getTenants()) {
             tenantProps.postConstruct(this); // tenant independent settings - strongest
            if (defaultProps != null && defaultProps != this) {
                tenantProps.postConstruct(defaultProps.getTenantProps(tenantProps.getName())); // update with default tenant dependent settings - weaker
                tenantProps.postConstruct(defaultProps); // update with default tenant independent default settings - weakest
            }
        }
    }
}
