/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.routebuilder;

import hu.dpc.ob.config.BindingProperties;
import hu.dpc.ob.config.SchemaSettings;
import hu.dpc.ob.config.TenantConfig;
import hu.dpc.ob.config.UriProperties;
import hu.dpc.ob.config.type.Binding;
import hu.dpc.ob.domain.type.RequestSource;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.model.internal.PspId;
import hu.dpc.ob.rest.ExchangeHeader;
import hu.dpc.ob.util.ContextUtils;
import hu.dpc.ob.util.ThreadLocalContext;
import liquibase.util.StringUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
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

    protected <_B extends Binding> void buildConsumerRoutes(ApiSchema schema, TenantConfig tenant, SchemaSettings<?, ?, _B> settings) {
        @NotNull RequestSource source = settings.getSource();
        @NotEmpty String tenantName = tenant.getName();
        for (_B binding : settings.getBindings(schema)) {
            BindingProperties bindingProps = settings.getBindingProps(schema, binding);
            UriProperties tenantProps = bindingProps.getTenantProps(tenantName);
            HttpMethod method = bindingProps.getHttpMethod();
            String url = tenantProps.getUrl();

            String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + method + "&enableCORS=" + settings.isCorsEnabled();

            String instance = settings.getAdapterSettings().getInstance();
            RouteDefinition from = from(consumerEndpoint);
            from.id(buildId(source, schema, tenantName + '-' + binding.getConfigName(), "consumer"));
            Class bodyClass = bindingProps.getBodyClass();
            if (bodyClass != null) {
                from.unmarshal().json(JsonLibrary.Jackson, bodyClass);
            }
            from.process(exchange -> {
                Message in = exchange.getIn();
                HttpServletRequest request = in.getBody(HttpServletRequest.class);
                String pathInfo = request.getPathInfo();
                exchange.setProperty(ExchangeHeader.PSP_ID.getKey(), new PspId(instance, tenantName));
                exchange.setProperty(ExchangeHeader.SCHEMA.getKey(), schema);
                exchange.setProperty(ExchangeHeader.SCOPE.getKey(), binding.getScope());
                exchange.setProperty(ExchangeHeader.SOURCE.getKey(), source);
                exchange.setProperty(ExchangeHeader.BINDING.getKey(), binding);
                exchange.setProperty(ExchangeHeader.PATH_PARAMS.getKey(), ContextUtils.parsePathParams(pathInfo, tenantProps.getUriPath()));

                ThreadLocalContext.setTenant(tenant);

                Object body = null;
                if (bodyClass != null) {
                    body = in.getBody(bodyClass);
                    exchange.setProperty(ExchangeHeader.REQUEST_DTO.getKey(), body);
                }
                log.debug("Incoming request: " + pathInfo + ", method: " + request.getMethod() + ", \nheader: " + StringUtils.join(in.getHeaders(), ",")
                        + ", \nbody: " + body);
            })
                    .to("direct:" + buildId(source, schema, binding.getConfigName(), null))
            ;
        }
    }

    protected <_B extends Binding> void buildDirectRoutes(ApiSchema schema, SchemaSettings<?, ?, _B> settings) {
        // general prepare route and processor
        RequestSource source = settings.getSource();
        String prepareRouteId = buildId(source, schema, ID_PREPARE);
        from("direct:" + prepareRouteId)
                .id(prepareRouteId)
                .process(exchange -> {
                    log.debug("Processing " + prepareRouteId);
                })
                .process(buildId(source, schema, ID_PREPARE_PROCESSOR))
        ;
        // general validation route and processor
        String validateRouteId = buildId(source, schema, ID_VALIDATE);
        from("direct:" + validateRouteId)
                .id(validateRouteId)
                .process(exchange -> {
                    log.debug("Processing " + validateRouteId);
                })
                .process(buildId(source, schema, ID_VALIDATE_PROCESSOR))
        ;

        for (_B binding : settings.getBindings(schema)) {
            @NotNull String configName = binding.getConfigName();
            // service dependent prepare route and processor, if not exists we fall back to general
            String actPrepareRouteId = prepareRouteId;
            String actPrepareProcId = buildId(source, schema, configName, ID_PREPARE_PROCESSOR);
            if (appContext.containsBeanDefinition(actPrepareProcId)) {
                actPrepareRouteId = buildId(source, schema, configName, ID_PREPARE);
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
            String actValidateProcId = buildId(source, schema, configName, ID_VALIDATE_PROCESSOR);
            if (appContext.containsBeanDefinition(actValidateProcId)) {
                actValidateRouteId = buildId(source, schema, configName, ID_PREPARE);
                final String avrid = actValidateRouteId;
                from("direct:" + actValidateRouteId)
                        .id(actValidateRouteId)
                        .process(exchange -> {
                            log.debug("Processing " + avrid);
                        })
                        .process(actValidateProcId);
            }
            String id = buildId(source, schema, configName, null);
            from("direct:" + id)
                    .id(id)
                    .process(exchange -> {
                        log.debug("Processing " + id);
                    })
                    .to("direct:" + actPrepareRouteId)
                    .to("direct:" + actValidateRouteId)
                    .process(buildId(source, schema, configName, ID_PROCESSOR))
                    .marshal().json(JsonLibrary.Jackson)
            ;
        }
    }

    protected String buildId(@NotNull RequestSource source, @NotNull ApiSchema schema, String function) {
        return buildId(source, schema, null, function);
    }

    protected String buildId(@NotNull RequestSource source, @NotNull ApiSchema schema, String configName, String function) {
        return (source.getId() + '-' + schema.getId() + (configName == null ? "" : '-' + configName) + (function == null ? "" : '-' + function)).toLowerCase();
    }
}
