/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.dpc.ob.domain.type.ConsentStatusCode;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PisConsentResponseData extends PisConsentData {

    @JsonProperty(value = "ConsentId", required = true)
    @NotEmpty
    @Size(max = 128)
    private String consentId;

    @JsonProperty(value = "CreationDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime creationDateTime;

    @JsonProperty(value = "Status", required = true)
    @NotNull
    private ConsentStatusCode status;

    @JsonProperty(value = "StatusUpdateDateTime", required = true)
    @NotNull
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime statusUpdateDateTime;

    @JsonProperty(value = "ExpectedExecutionDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expectedExecutionDateTime;

    @JsonProperty(value = "ExpectedSettlementDateTime")
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
    private LocalDateTime expectedSettlementDateTime;

    @JsonProperty(value = "Charges")
    private List<ChargeData> charges;

    public PisConsentResponseData(@NotNull PisInitiationData initiation, AuthorizationData authorization, ScaSupportData scaSupportData,
                                  @NotEmpty @Size(max = 128) String consentId, @NotNull LocalDateTime creationDateTime,
                                  @NotNull ConsentStatusCode status, @NotNull LocalDateTime statusUpdateDateTime, LocalDateTime expectedExecutionDateTime,
                                  LocalDateTime expectedSettlementDateTime, List<ChargeData> charges) {
        super(initiation, authorization, scaSupportData);
        this.consentId = consentId;
        this.creationDateTime = creationDateTime;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime;
        this.expectedExecutionDateTime = expectedExecutionDateTime;
        this.expectedSettlementDateTime = expectedSettlementDateTime;
        this.charges = charges;
    }
}
