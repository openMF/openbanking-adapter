/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.parser;

import hu.dpc.ob.cache.TransactionContextHolder;
import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.config.Binding;
import hu.dpc.ob.config.BindingProperties;
import hu.dpc.ob.config.Schema;
import hu.dpc.ob.rest.internal.ApiSchema;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component("api-ob-parser")
public class ApiObJsonParser extends ApiJsonParser {

    @Autowired
    public ApiObJsonParser(ApiSettings apiSettings) {
        super(apiSettings);
    }

    @Override
    public ApiSchema getSchema() {
        return ApiSchema.OB;
    }

    @Override
    public Class getBodyClass(ApiSettings.ApiBinding binding) {
        return null;
    }
}
