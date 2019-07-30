/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import hu.dpc.ob.config.TenantConfig;

public class ThreadLocalContext {

    private static ThreadLocal<TenantConfig> tenants = new ThreadLocal<>();

    public static void setTenant(TenantConfig tenant) {
        tenants.set(tenant);
    }

    public static TenantConfig getTenant() {
        return tenants.get();
    }
}
