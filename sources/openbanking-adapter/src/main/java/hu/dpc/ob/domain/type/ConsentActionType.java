/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum ConsentActionType implements PersistentType<ConsentActionType, String> {

    @JsonProperty("Create")
    CREATE("Create"),
    @JsonProperty("Authorize")
    AUTHORIZE("Authorize"),
    @JsonProperty("Reject")
    REJECT("Reject"),
    @JsonProperty("Revoke")
    REVOKE("Revoke"),
    @JsonProperty("AddAccount")
    ADD_ACCOUNT("AddAccount"),
    @JsonProperty("RemoveAccount")
    REMOVE_ACCOUNT("RemoveAccount"),
    @JsonProperty("AddPermission")
    ADD_PERMISSION("AddPermission"),
    @JsonProperty("RemovePermission")
    REMOVE_PERMISSION("RemovePermission"),
    @JsonProperty("ApiTransaction")
    API_TRANSACTION("ApiTransaction"),
    @JsonProperty("InteropTransaction")
    INTEROP_TRANSACTION("InteropTransaction"),
    ;


    private static final Map<String, ConsentActionType> BY_ID = Arrays.stream(ConsentActionType.values()).collect(Collectors.toMap(ConsentActionType::getApiName, e -> e));

    @NotNull
    private final String apiName;

    @Override
    public String toId() {
        return apiName;
    }

}
