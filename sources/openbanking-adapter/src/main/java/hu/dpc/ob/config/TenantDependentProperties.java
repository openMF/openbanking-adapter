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

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class TenantDependentProperties extends UriProperties implements DefaultProperties {

    @Getter(lazy = true)
    private final List<TenantProperties> tenants = new ArrayList<>(1);

    protected TenantDependentProperties(String name) {
        super(name);
    }

    protected abstract String getDefaultName();

    @Override
    public boolean isDefault() {
        return getName().equals(getDefaultName());
    }

    public TenantProperties getTenant(String tenant) {
        if (tenant == null)
            return null;
        for (TenantProperties channelTenant : getTenants()) {
            if (tenant.equals(channelTenant.getName()))
                return channelTenant;
        }
        return null;
    }

    protected TenantProperties addTenant(TenantProperties tenant) {
        getTenants().add(tenant);
        return tenant;
    }

    void postConstruct(TenantDependentProperties defaultProps) {
        super.postConstruct(defaultProps); // tenant independent default settings

        if (defaultProps != null && defaultProps != this) {
            for (TenantProperties parentTenant : defaultProps.getTenants()) {
                TenantProperties tenant = getTenant(parentTenant.getName());
                if (tenant == null)
                    addTenant(new TenantProperties(parentTenant.getName()));
            }
        }

        for (TenantProperties tenant : getTenants()) {
            tenant.postConstruct(this); // tenant independent settings - strongest
            if (defaultProps != null && defaultProps != this) {
                tenant.postConstruct(defaultProps.getTenant(tenant.getName())); // update with default tenant dependent settings - weaker
                tenant.postConstruct(defaultProps); // update with default tenant independent default settings - weakest
            }
        }
    }
}
