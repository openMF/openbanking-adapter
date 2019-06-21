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

import javax.validation.constraints.NotNull;

@Getter
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public abstract class UriProperties extends BaseUriProperties {

    @NotNull
    private String method;

    private Class bodyClass;

    protected UriProperties(String name) {
        super(name);
    }

    @NotNull
    public HttpMethod getHttpMethod() {
        return HttpMethod.resolve(method);
    }

    void postConstruct(UriProperties oProps) {
        if (oProps == null || oProps == this)
            return;
        super.postConstruct(oProps);

        // empty is a valid value
        if (method == null)
            method = oProps.getMethod();
    }
}
