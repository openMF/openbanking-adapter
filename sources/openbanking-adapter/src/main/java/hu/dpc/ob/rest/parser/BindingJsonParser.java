/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.parser;

import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.model.internal.ApiSchema;

public abstract class BindingJsonParser<_B extends Binding> {

    public abstract ApiSchema getSchema();

    public abstract boolean hasBody(_B binding);

    public abstract Class getBodyClass(_B binding);
}
