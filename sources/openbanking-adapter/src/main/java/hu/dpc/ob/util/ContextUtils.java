/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import hu.dpc.ob.rest.ExchangeHeader;
import org.apache.camel.Exchange;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContextUtils {

    private final static DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#.####");

    public static final String ID_PREPARE = "prepare";
    public static final String ID_VALIDATE = "validate";
    public static final String ID_PROCESSOR = "processor";
    public static final String ID_PREPARE_PROCESSOR = ID_PREPARE + '-' + ID_PROCESSOR;
    public static final String ID_VALIDATE_PROCESSOR = ID_VALIDATE + '-' + ID_PROCESSOR;
    public static final String ID_PARSER = "parser";

    public static final String CONFIG_PATH_PARAM_STRART = "{";
    public static final String CONFIG_PATH_PARAM_END = "}";
    public static final String PATH_PARAM_SEPARATOR = "/";

    public static final String PARAM_PARTY_ID = "partyId";
    public static final String PARAM_CONSENT_ID = "consentId";
    public static final String PARAM_ACCOUNT_ID = "accountId";
    public static final String PARAM_PAYMENT_ID = "paymentId";
    public static final String PARAM_CLIENT_PAYMENT_ID = "clientPaymentId";


    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public static String parsePathParam(String pathInfo, int numberOfParams, int index) {
        //TODO: URL parameter handling should be rewritten
        int revIdx = numberOfParams - index;
        int from = 0;
        String parse = pathInfo;
        while (--revIdx >= 0) {
            int idx = parse.lastIndexOf('/');
            if (idx < 0)
                return null;

            parse = parse.substring(0, idx);
            from = idx + 1;
        }
        int to = pathInfo.indexOf('/', from);
        if (to < 0)
            to = pathInfo.indexOf('?', from);
        if (to < 0)
            to = pathInfo.length();
        return pathInfo.substring(from, to);
    }

    public static HashMap<String, String> parsePathParams(String pathInfo, String pathConfig) {
        if (pathInfo == null || pathConfig == null)
            return null;

        int noOfParams = StringUtils.countOccurrencesOf(pathConfig, CONFIG_PATH_PARAM_STRART);
        HashMap<String, String> result = new HashMap<>(noOfParams);
        int pathPos = 0;
        int pos = 0;
        for (int i = 0; i < noOfParams; i++) {
            int start = pathConfig.indexOf(CONFIG_PATH_PARAM_STRART, pos);
            int end = pathConfig.indexOf(CONFIG_PATH_PARAM_END, start);
            String key = pathConfig.substring(start + 1, end);
            pos = end + 1;
            if (i > 0) {
                start = pathInfo.indexOf(PATH_PARAM_SEPARATOR, pathPos) + 1;
            }
            end = pathInfo.indexOf(PATH_PARAM_SEPARATOR, start);
            if (end < 0)
                end = pathInfo.length();
            String value = pathInfo.substring(start, end);
            pathPos = end + 1;
            result.put(key, value);
        }
        return result;
    }

    public static String getPathParam(Exchange exchange, String paramName) {
        Map pathParams = exchange.getProperty(ExchangeHeader.PATH_PARAMS.getKey(), Map.class);
        return pathParams == null ? null : (String) pathParams.get(paramName);
    }

    public static String resolvePathParam(String pathInfo, Map<String, String> params) {
        if (pathInfo == null || params == null)
            return pathInfo;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String paramName = entry.getKey();
            paramName = paramName.startsWith(CONFIG_PATH_PARAM_STRART) ? paramName : (CONFIG_PATH_PARAM_STRART + paramName + CONFIG_PATH_PARAM_END);
            String value = entry.getValue();
            pathInfo.replace(paramName, value == null ? "" : value);
        }
        return pathInfo;
    }

    public static String resolvePathParams(String pathConfig, String... values) {
        if (pathConfig == null)
            return pathConfig;

        int noOfParams = StringUtils.countOccurrencesOf(pathConfig, CONFIG_PATH_PARAM_STRART);
        if (noOfParams == 0)
            return pathConfig;
        if (values == null || noOfParams != values.length)
            throw new IllegalArgumentException("Can not format path configuration " + pathConfig + ". Number of parameters do not match for " + Arrays.toString(values));

        StringBuilder pathInfo = new StringBuilder();

        int pos = 0;
        for (int i = 0; i < noOfParams; i++) {
            int start = pathConfig.indexOf(CONFIG_PATH_PARAM_STRART, pos);
            int end = pathConfig.indexOf(CONFIG_PATH_PARAM_END, start);

            pathInfo.append(pathConfig.substring(pos, start)).append(values[i]);
            pos = end + 1;
        }
        if (pos < pathConfig.length()) {
            pathInfo.append(pathConfig.substring(pos));
        }
        return pathInfo.toString();
    }

    public static BigDecimal parseAmount(String amount) {
        return amount == null ? null : new BigDecimal(amount);
    }

    public static String formatAmount(BigDecimal amount) {
        return amount == null ? null : AMOUNT_FORMAT.format(amount);
    }

    public static void assertNotNull(Object o) {
        assertNotNull(o, "Assert object is null");
    }

    public static void assertNotNull(Object o, String msg) {
        if (o == null)
            throw new AssertionError(msg);
    }

    public static void assertEq(Object o1, Object o2) {
        assertEq(o1, o2, "Assert objects are not equal. Expected " + o1 + ", got " + o2);
    }

    public static void assertEq(Object o1, Object o2, String msg) {
        if (o1 == null ? o2 != null : !o1.equals(o2))
            throw new AssertionError(msg);
    }
}
