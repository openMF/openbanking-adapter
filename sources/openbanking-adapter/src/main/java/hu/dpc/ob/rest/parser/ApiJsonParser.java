/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.parser;

import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.config.BindingProperties;
import org.springframework.http.HttpMethod;

public abstract class ApiJsonParser extends BindingJsonParser<ApiSettings.ApiBinding> {

    private ApiSettings apiSettings;

    public ApiJsonParser(ApiSettings apiSettings) {
        this.apiSettings = apiSettings;
    }

    @Override
    public boolean hasBody(ApiSettings.ApiBinding binding) {
        BindingProperties bindingProps = apiSettings.getBindingProps(getSchema(), binding);
        return bindingProps.getHttpMethod() == HttpMethod.POST;
    }
}
