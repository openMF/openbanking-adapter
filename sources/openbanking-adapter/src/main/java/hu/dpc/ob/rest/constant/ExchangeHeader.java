/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.constant;

public enum ExchangeHeader {

    PATH_PARAMS("path-params"), // Map<String, String> (parsed path parameters mapped by param config name)

    TRANSACTION_ID("transaction-id"), // String

    SCHEMA("schema"), // String

    USER_ID("user-id"), // Integer (User Id)
    API_USER_ID("api-user-id"), // String (Web User Id)
    PSP_USER_ID("psp-user-id"), // String (Bank User Id)

    CONSENT_ID("consent-id"), // String
    CLIENT_ID("client-id"), // String (TPP id)
    PSP_ID("psp-id"), // PspId
    ;

    private String key;

    private ExchangeHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
