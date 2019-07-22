/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.InteropExtension;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ExtensionData {

    @JsonProperty(required = true)
    @NotEmpty
    @Size(min = 1, max = 32)
    private String key; // mandatory, String(1..32)
    @JsonProperty(required = true)
    @NotEmpty
    private String value; // mandatory, String(1..128)

    public ExtensionData(@NotEmpty @Size(min = 1, max = 32) String key, @NotEmpty String value) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    public static ExtensionData create(InteropExtension extension) {
        return extension == null ? null : new ExtensionData(extension.getKey(), extension.getValue());
    }
}
