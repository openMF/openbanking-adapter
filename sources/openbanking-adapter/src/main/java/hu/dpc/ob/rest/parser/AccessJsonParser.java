/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.parser;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.config.BindingProperties;
import org.springframework.http.HttpMethod;

public abstract class AccessJsonParser extends BindingJsonParser<AccessSettings.AccessBinding> {

    private AccessSettings accessSettings;

    public AccessJsonParser(AccessSettings accessSettings) {
        this.accessSettings = accessSettings;
    }

    @Override
    public boolean hasBody(AccessSettings.AccessBinding binding) {
        BindingProperties bindingProps = accessSettings.getBindingProps(getSchema(), binding);
        return bindingProps.getHttpMethod() == HttpMethod.POST;
    }
}
