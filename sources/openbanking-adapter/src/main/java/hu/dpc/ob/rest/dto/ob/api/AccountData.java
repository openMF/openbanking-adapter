/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import hu.dpc.ob.rest.dto.ob.api.type.AccountStatus;
import hu.dpc.ob.rest.dto.ob.api.type.AccountSubType;
import hu.dpc.ob.rest.dto.ob.api.type.AccountType;
import hu.dpc.ob.rest.dto.psp.PspAccountsGuarantorData;
import hu.dpc.ob.rest.dto.psp.PspAccountsLoanData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsShareData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountData {

    @JsonProperty(value = "AccountId", required = true)
    @NotEmpty
    @Length(max = 40)
    private String accountId;

    @JsonProperty(value = "Status")
    private AccountStatus status;

    @JsonProperty(value = "StatusUpdateDateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime statusUpdateDateTime;

    @JsonProperty(value = "Currency", required = true)
    @JsonFormat(pattern = "^[A-Z]{3,3}$")
    @NotEmpty
    private String currency;

    @JsonProperty(value = "AccountType", required = true)
    @NotNull
    private AccountType accountType;

    @JsonProperty(value = "AccountSubType", required = true)
    @NotNull
    private AccountSubType accountSubType;

    @JsonProperty(value = "Nickname")
    @Length(max = 70)
    private String nickname;


    public AccountData(@NotEmpty @Length(max = 40) String accountId, AccountStatus status, LocalDateTime statusUpdateDateTime,
                       @NotEmpty String currency, @NotNull AccountType accountType, @NotNull AccountSubType accountSubType,
                       @Length(max = 70) String nickname) {
        this.accountId = accountId;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime;
        this.currency = currency;
        this.accountType = accountType;
        this.accountSubType = accountSubType;
        this.nickname = nickname;
    }

    public AccountData(@NotEmpty @Length(max = 40) String accountId, @NotEmpty String currency, @NotNull AccountType accountType,
                       @NotNull AccountSubType accountSubType) {
        this(accountId, null, null, currency, accountType, accountSubType, null);
    }

    static AccountData transform(@NotNull PspAccountsSavingsData pspAccount, boolean detail, String accountId) {
        String externalId = pspAccount.getExternalId();
        if (accountId != null && !accountId.equals(externalId)) {
            return null;
        }
        AccountData transform = new AccountData(externalId, pspAccount.getCurrency().getCode(), pspAccount.getApiAccountType(), AccountSubType.SAVINGS);
        transform.setNickname(pspAccount.getApiNickName()); // TODO add OBReadAccount4/Data/Account/Account in case of detail
        return transform;
    }

    static AccountData transform(@NotNull PspAccountsLoanData pspAccount, boolean detail, String accountId) {
        return null;
    }

    static AccountData transform(@NotNull PspAccountsGuarantorData pspAccount, boolean detail, String accountId) {
        return null;
    }

    static AccountData transform(@NotNull PspAccountsShareData pspAccount, boolean detail, String accountId) {
        return null;
    }
}
