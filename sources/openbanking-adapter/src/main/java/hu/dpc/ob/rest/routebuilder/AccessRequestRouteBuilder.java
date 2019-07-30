/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.routebuilder;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.config.TenantConfig;
import hu.dpc.ob.model.internal.ApiSchema;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessRequestRouteBuilder extends OpenbankingRouteBuilder {

    private static Logger log = LoggerFactory.getLogger(AccessRequestRouteBuilder.class);

    private AccessSettings accessSettings;

    @Autowired
    public AccessRequestRouteBuilder(CamelContext camelContext, ApplicationContext appContext, AccessSettings accessSettings) {
        super(camelContext, appContext);
        this.accessSettings = accessSettings;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        AdapterSettings adapterSettings = accessSettings.getAdapterSettings();
        adapterSettings.getSchemas().forEach(schema -> {
            adapterSettings.getTenants().forEach(tenant -> {
                buildConsumerRoutes(schema, tenant);
            });
            buildDirectRoutes(schema);
        });
    }

    private void buildConsumerRoutes(ApiSchema schema, TenantConfig tenant) {
        buildConsumerRoutes(schema, tenant, accessSettings);
    }

    private void buildDirectRoutes(ApiSchema schema) {
        buildDirectRoutes(schema, accessSettings);
    }
}
