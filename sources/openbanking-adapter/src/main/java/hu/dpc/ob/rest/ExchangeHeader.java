/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest;

public enum ExchangeHeader {

    SCHEMA("schema"), // ApiSchema
    SCOPE("scope"), // ApiScope
    SOURCE("source"), // RequestSource
    BINDING("binding"), // Binding

    PATH_PARAMS("path-params"), // Map<String, String> (parsed path parameters mapped by param config name)

    CLIENT_ID("client-id"), // String (TPP id)
    PSP_ID("psp-id"), // PspId

    USER_ID("user-id"), // Integer (User Id)
    API_USER_ID("api-user-id"), // String (Web User Id)
    PSP_USER_ID("psp-user-id"), // String (Bank User Id)

    REQUEST_DTO("request-dto"), // request body dto
    ;

    private String key;

    private ExchangeHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
