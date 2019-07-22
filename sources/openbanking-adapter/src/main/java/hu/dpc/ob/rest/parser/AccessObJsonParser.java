/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.parser;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.model.internal.ApiSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("access-ob-parser")
public class AccessObJsonParser extends AccessJsonParser {

    @Autowired
    public AccessObJsonParser(AccessSettings accessSettings) {
        super(accessSettings);
    }

    @Override
    public ApiSchema getSchema() {
        return ApiSchema.OB;
    }

    @Override
    public Class getBodyClass(AccessSettings.AccessBinding binding) {
        return null;
    }
}
