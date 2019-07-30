/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.routebuilder;

import hu.dpc.ob.config.AdapterSettings;
import hu.dpc.ob.config.ApiSettings;
import hu.dpc.ob.config.TenantConfig;
import hu.dpc.ob.model.internal.ApiSchema;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRequestRouteBuilder extends OpenbankingRouteBuilder {

    private static Logger log = LoggerFactory.getLogger(ApiRequestRouteBuilder.class);

    private ApiSettings apiSettings;

    @Autowired
    public ApiRequestRouteBuilder(CamelContext camelContext, ApplicationContext appContext, ApiSettings apiSettings) {
        super(camelContext, appContext);
        this.apiSettings = apiSettings;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        AdapterSettings adapterSettings = apiSettings.getAdapterSettings();
        adapterSettings.getSchemas().forEach(schema -> {
            adapterSettings.getTenants().forEach(tenant -> {
                buildConsumerRoutes(schema, tenant);
            });
            buildDirectRoutes(schema);
        });
    }

    private void buildConsumerRoutes(ApiSchema schema, TenantConfig tenant) {
        buildConsumerRoutes(schema, tenant, apiSettings);
    }

    private void buildDirectRoutes(ApiSchema schema) {
        buildDirectRoutes(schema, apiSettings);
    }
}
