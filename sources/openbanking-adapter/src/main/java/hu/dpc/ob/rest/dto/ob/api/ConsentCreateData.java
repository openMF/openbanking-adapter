/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import hu.dpc.ob.domain.type.ApiPermission;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class ConsentCreateData extends ConsentData {

    ConsentCreateData(@NotNull List<ApiPermission> permissions, LocalDateTime expirationDateTime, LocalDateTime transactionFromDateTime, LocalDateTime transactionToDateTime) {
        super(permissions, expirationDateTime, transactionFromDateTime, transactionToDateTime);
    }

    ConsentCreateData(@NotNull List<ApiPermission> permissions) {
        this(permissions, null, null, null);
    }

}
