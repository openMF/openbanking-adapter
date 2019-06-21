/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.processor;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.internal.ApiSchema;
import hu.dpc.ob.service.ApiService;
import hu.dpc.ob.util.ContextUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PrepareProcessor implements Processor {


    public abstract ApiSchema getSchema();

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setProperty(ExchangeHeader.SCHEMA.getKey(), getSchema());
    }
}
