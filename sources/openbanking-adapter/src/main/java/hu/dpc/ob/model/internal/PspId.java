/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.model.internal;

public class PspId {

    private String instance;
    private String tenant;

    public PspId(String instance, String tenant) {
        this.instance = instance;
        this.tenant = tenant;
    }

    public String getInstance() {
        return instance;
    }

    public String getTenant() {
        return tenant;
    }

    public String getId() {
        return instance + tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PspId pspId = (PspId) o;

        if (!instance.equals(pspId.instance)) return false;
        return tenant.equals(pspId.tenant);
    }

    @Override
    public int hashCode() {
        int result = instance.hashCode();
        result = 31 * result + tenant.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PspId{" +
                "instance:'" + instance + '\'' +
                ", tenant:'" + tenant + '\'' +
                '}';
    }
}
