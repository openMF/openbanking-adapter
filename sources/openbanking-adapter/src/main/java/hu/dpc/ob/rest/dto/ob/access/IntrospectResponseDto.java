/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown=true)
public class IntrospectResponseDto {

    private boolean active; // Boolean indicator of whether the access token is active
    private String scope; // Space-separated list of scopes that are associated with the access token
    @JsonProperty(value = "client_id")
    private String clientId; // Client identifier of the OpenID Connect Client who requested the access token
    private String sub; // Resource owner who authorized the access token
    @JsonProperty(value = "token_type")
    private String tokenType; // Access token type. For OpenID Connect, this value is Bearer
    @JsonProperty(value = "grant_type")
    private String grantType; // String indicating the type of grant that generated the access token. Possible values are: authorization_code, password, refresh_token, client_credentials, resource_owner, implicit, and urn:ietf:params:oauth:grant-type:jwt-bearer
    private Float exp; // Integer timestamp, measured in seconds since January 1, 1970 UTC, indicating when the access token will expire
    @JsonProperty(value = "username")
    private String userName;
    private Integer iat; // Integer timestamp, measured in seconds since January 1, 1970 UTC, indicating when the access token was issued
    private Integer nbf; // ?
    private String realmName; // Realm name of the resourceId owner
    private String uniqueSecurityName; // Unique security name of the resourceId owner
}
