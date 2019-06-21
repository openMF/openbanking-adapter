/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.component;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.config.AuthEncodeType;
import hu.dpc.ob.config.OperationProperties;
import hu.dpc.ob.config.TenantProperties;
import hu.dpc.ob.rest.dto.ob.access.IntrospectRequestDto;
import hu.dpc.ob.rest.dto.ob.access.IntrospectResponseDto;
import hu.dpc.ob.rest.dto.ob.access.UserInfoResponseDto;
import hu.dpc.ob.rest.internal.ApiSchema;
import hu.dpc.ob.util.JsonUtils;
import lombok.NoArgsConstructor;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class AccessRestClient {

    private static Logger log = LoggerFactory.getLogger(AccessRestClient.class);

    private RestClient restClient;

    private AccessSettings accessSettings;


    @Autowired
    public AccessRestClient(RestClient restClient, AccessSettings accessSettings) {
        this.restClient = restClient;
        this.accessSettings = accessSettings;
    }

    public UserInfoResponseDto callUserInfo(@NotNull ApiSchema schema, @NotNull String tenant, @NotNull String accessCode) {
        @NotNull String configName = AccessSettings.AccessOperation.USER_INFO.getConfigName();
        log.debug(String.format("Call Identity GET /" + configName + ", access: %s", accessCode));

        OperationProperties opProps = accessSettings.getOperation(schema, AccessSettings.AccessOperation.USER_INFO);
        TenantProperties tenantProps = opProps.getTenant(tenant);
        String url = tenantProps.getUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION.asString(), accessCode);

        String responseJson = restClient.call(url, opProps.getHttpMethod(), headers, null);

        log.debug(String.format("Response Identity GET /" + configName + ", access: %s", accessCode));
        return JsonUtils.toPojo(responseJson, UserInfoResponseDto.class);
    }

    public IntrospectResponseDto callIntrospect(@NotNull ApiSchema schema, @NotNull String tenant, @NotNull String userAccessCode) {
        @NotNull String configName = AccessSettings.AccessOperation.INTROSPECT.getConfigName();
        log.debug(String.format("Call Identity GET /" + configName + ", access: %s", userAccessCode));

        OperationProperties opProps = accessSettings.getOperation(schema, AccessSettings.AccessOperation.INTROSPECT);
        TenantProperties tenantProps = opProps.getTenant(tenant);
        String url = tenantProps.getUrl();

        String user = tenantProps.getUser();
        String pwd = tenantProps.getPassword();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION.asString(), AuthEncodeType.BASE64.encode(user + ':' + pwd));
        headers.put(HttpHeader.CONTENT_TYPE.asString(), MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        IntrospectRequestDto request = IntrospectRequestDto.create(userAccessCode);

        String responseJson = restClient.call(url, opProps.getHttpMethod(), headers, JsonUtils.toJson(request));

        log.debug(String.format("Response Identity GET /" + configName + ", access: %s", userAccessCode));
        return JsonUtils.toPojo(responseJson, IntrospectResponseDto.class);
    }
}
