/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.routebuilder;

import hu.dpc.ob.config.Binding;
import hu.dpc.ob.config.BindingProperties;
import hu.dpc.ob.config.ListConfig;
import hu.dpc.ob.config.SchemaSettings;
import hu.dpc.ob.config.TenantProperties;
import hu.dpc.ob.rest.constant.ExchangeHeader;
import hu.dpc.ob.rest.internal.ApiSchema;
import hu.dpc.ob.rest.internal.PspId;
import hu.dpc.ob.util.ContextUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static hu.dpc.ob.util.ContextUtils.*;

public abstract class OpenbankingRouteBuilder extends RouteBuilder {

    private static Logger log = LoggerFactory.getLogger(OpenbankingRouteBuilder.class);

    private ApplicationContext appContext;

    @Autowired
    public OpenbankingRouteBuilder(CamelContext camelContext, ApplicationContext appContext) {
        super(camelContext);
        this.appContext = appContext;
    }

    protected <_B extends Binding> void buildBindingRoutes(ApiSchema schema, String tenant, SchemaSettings<?, ?, _B> settings) {
//        BindingJsonParser<_B> parser = (BindingJsonParser<_B>) getContext().getComponent(buildId(schema, ID_PARSER));
        for (_B binding : settings.getBindings(schema)) {
            BindingProperties bindingProps = settings.getBinding(schema, binding);
            TenantProperties tenantProps = bindingProps.getTenant(tenant);
            HttpMethod method = bindingProps.getHttpMethod();
            String url = tenantProps.getUrl();

            String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + method + "&enableCORS=" + settings.isCorsEnabled();

            String instance = settings.getAdapterSettings().getInstance();
            RouteDefinition from = from(consumerEndpoint);
            from.id(buildId(schema, tenant + '-' + binding.getConfigName(), "consumer"));
            Class bodyClass = bindingProps.getBodyClass();
            if (bodyClass != null) {
                from.unmarshal().json(JsonLibrary.Jackson, bodyClass);
            }
            from.process(exchange -> {
                exchange.setProperty(ExchangeHeader.PSP_ID.getKey(), new PspId(instance, tenant));
                String pathInfo = exchange.getIn().getBody(HttpServletRequest.class).getPathInfo();
                exchange.setProperty(ExchangeHeader.PATH_PARAMS.getKey(), ContextUtils.parsePathParams(pathInfo, tenantProps.getUriPath()));
            })
                    .to("direct:" + buildId(schema, binding.getConfigName(), null))
            ;
        }
    }

    protected <_C extends ListConfig> void buildDirectRoutes(ApiSchema schema, _C[] configs) {
        // general prepare route and processor
        String prepareRouteId = buildId(schema, ID_PREPARE);
        from("direct:" + prepareRouteId)
                .id(prepareRouteId)
                .process(exchange -> {
                    log.debug("Processing " + prepareRouteId);
                })
                .process(buildId(schema, ID_PREPARE_PROCESSOR))
        ;
        // general validation route and processor
        String validateRouteId = buildId(schema, ID_VALIDATE);
        from("direct:" + validateRouteId)
                .id(validateRouteId)
                .process(exchange -> {
                    log.debug("Processing " + validateRouteId);
                })
                .process(buildId(schema, ID_VALIDATE_PROCESSOR))
        ;

        for (_C config : configs) {
            @NotNull String configName = config.getConfigName();
            // service dependent prepare route and processor, if not exists we fall back to general
            String actPrepareRouteId = prepareRouteId;
            String actPrepareProcId = buildId(schema, configName, ID_PREPARE_PROCESSOR);
            if (appContext.containsBeanDefinition(actPrepareProcId)) {
                actPrepareRouteId = buildId(schema, configName, ID_PREPARE);
                final String aarid = actPrepareRouteId;
                from("direct:" + actPrepareRouteId)
                        .id(actPrepareRouteId)
                        .process(exchange -> {
                            log.debug("Processing " + aarid);
                        })
                        .process(actPrepareProcId);
            }
            // service dependent validation route and processor, if not exists we fall back to general
            String actValidateRouteId = validateRouteId;
            String actValidateProcId = buildId(schema, configName, ID_VALIDATE_PROCESSOR);
            if (appContext.containsBeanDefinition(actValidateProcId)) {
                actValidateRouteId = buildId(schema, configName, ID_PREPARE);
                final String avrid = actValidateRouteId;
                from("direct:" + actValidateRouteId)
                        .id(actValidateRouteId)
                        .process(exchange -> {
                            log.debug("Processing " + avrid);
                        })
                        .process(actValidateProcId);
            }
            String id = buildId(schema, configName, null);
            from("direct:" + id)
                    .id(id)
                    .process(exchange -> {
                        log.debug("Processing " + id);
                    })
                    .to("direct:" + actPrepareRouteId)
                    .to("direct:" + actValidateRouteId)
                    .process(buildId(schema, configName, ID_PROCESSOR))
                    .marshal().json(JsonLibrary.Jackson)
            ;
        }
    }

    @NotNull
    protected abstract String getSource();

    protected String buildId(@NotNull ApiSchema schema, String function) {
        return buildId(schema, null, function);
    }

    protected String buildId(@NotNull ApiSchema schema, String configName, String function) {
        return (getSource() + '-' + schema.getConfigName() + (configName == null ? "" : '-' + configName) + (function == null ? "" : '-' + function)).toLowerCase();
    }
}
