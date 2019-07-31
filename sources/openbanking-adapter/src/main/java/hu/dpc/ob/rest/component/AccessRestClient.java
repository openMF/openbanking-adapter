/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.component;

import hu.dpc.ob.config.AccessSettings;
import hu.dpc.ob.config.OperationProperties;
import hu.dpc.ob.config.UriProperties;
import hu.dpc.ob.config.type.AuthEncodeType;
import hu.dpc.ob.config.type.AuthProfileType;
import hu.dpc.ob.model.internal.ApiSchema;
import hu.dpc.ob.rest.dto.ob.access.IntrospectResponseDto;
import hu.dpc.ob.rest.dto.ob.access.UserInfoResponseDto;
import hu.dpc.ob.util.JsonUtils;
import lombok.AccessLevel;
import lombok.Getter;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
        OperationProperties opProps = accessSettings.getOperationProps(schema, AccessSettings.AccessOperation.USER_INFO);
        log.debug(String.format("Call Identity " + opProps.getMethod() + " /" + opProps.getName() + ", access: %s", accessCode));

        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = tenantProps.getUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION.asString(), accessCode);

        String responseJson = restClient.call(url, opProps.getHttpMethod(), headers, null);

        log.debug(String.format("Response Identity " + opProps.getMethod() + " /" + opProps.getName() + ", access: %s, payload: %s", accessCode, responseJson));
        return JsonUtils.toPojo(responseJson, UserInfoResponseDto.class);
    }

    public IntrospectResponseDto callIntrospect(@NotNull ApiSchema schema, @NotNull String tenant, @NotNull String userAccessCode) {
        OperationProperties opProps = accessSettings.getOperationProps(schema, AccessSettings.AccessOperation.INTROSPECT);
        log.debug(String.format("Call Identity " + opProps.getMethod() + " /" + opProps.getName() + ", access: %s", userAccessCode));

        UriProperties tenantProps = opProps.getTenantProps(tenant);
        String url = tenantProps.getUrl();

        String user = tenantProps.getUser();
        String pwd = tenantProps.getPassword();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.AUTHORIZATION.asString(), AuthProfileType.BASIC.encode(AuthEncodeType.BASE64.encode(user + ':' + pwd)));
        headers.put(HttpHeader.CONTENT_TYPE.asString(), MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String token = userAccessCode;
        @NotNull String prefix = AuthProfileType.OAUTH.getPrefix();
        if (token.startsWith(prefix))
            token = token.substring(prefix.length());

        String body = "token=" + token;
        String responseJson = restClient.call(url, opProps.getHttpMethod(), headers, body);

        log.debug(String.format("Response Identity " + opProps.getMethod() + " /" + opProps.getName() + ", access: %s, payload: %s", userAccessCode, responseJson));
        return JsonUtils.toPojo(responseJson, IntrospectResponseDto.class);
    }
}
