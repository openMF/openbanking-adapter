/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.ConsentAccount;
import hu.dpc.ob.rest.dto.ob.api.type.AccountStatus;
import hu.dpc.ob.rest.dto.ob.api.type.AccountSubType;
import hu.dpc.ob.rest.dto.ob.api.type.AccountType;
import hu.dpc.ob.rest.dto.psp.PspAccountsGuarantorData;
import hu.dpc.ob.rest.dto.psp.PspAccountsLoanData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsShareData;
import hu.dpc.ob.service.ConsentService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentAccountData {

    @JsonProperty(value = "AccountId", required = true)
    @NotEmpty
    @Length(max = 40)
    private String accountId;

    public ConsentAccountData(@NotEmpty @Length(max = 40) String accountId) {
        this.accountId = accountId;
    }

    @NotNull
    static ConsentAccountData create(@NotNull ConsentAccount account) {
        return new ConsentAccountData(account.getAccountId());
    }

    @NotNull
    static List<ConsentAccountData> create(@NotNull Consent consent) {
        return consent.getAccounts().stream().map(ConsentAccountData::create).collect(Collectors.toList());
    }
}
