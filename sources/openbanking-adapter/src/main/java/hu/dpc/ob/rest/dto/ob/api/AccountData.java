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
import hu.dpc.ob.rest.dto.ob.api.type.AccountStatus;
import hu.dpc.ob.rest.dto.ob.api.type.AccountSubType;
import hu.dpc.ob.rest.dto.ob.api.type.AccountType;
import hu.dpc.ob.rest.dto.psp.PspAccountsGuarantorData;
import hu.dpc.ob.rest.dto.psp.PspAccountsLoanData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsStatusData;
import hu.dpc.ob.rest.dto.psp.PspAccountsSavingsTimelineData;
import hu.dpc.ob.rest.dto.psp.PspAccountsShareData;
import hu.dpc.ob.rest.dto.psp.PspIdentifierData;
import hu.dpc.ob.rest.dto.psp.PspIdentifiersResponseDto;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeDeserializer;
import hu.dpc.ob.rest.parser.LocalFormatDateTimeSerializer;
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
    @JsonSerialize(using = LocalFormatDateTimeSerializer.class)
    @JsonDeserialize(using = LocalFormatDateTimeDeserializer.class)
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

    @JsonProperty(value = "Account")
    private List<AccountIdentificationData> identifications;


    AccountData(@NotEmpty @Length(max = 40) String accountId, AccountStatus status, LocalDateTime statusUpdateDateTime,
                       @NotEmpty String currency, @NotNull AccountType accountType, @NotNull AccountSubType accountSubType,
                       @Length(max = 70) String nickname, List<AccountIdentificationData> identifications) {
        this.accountId = accountId;
        this.status = status;
        this.statusUpdateDateTime = statusUpdateDateTime;
        this.currency = currency;
        this.accountType = accountType;
        this.accountSubType = accountSubType;
        this.nickname = nickname;
        this.identifications = identifications;
    }

    AccountData(@NotEmpty @Length(max = 40) String accountId, @NotEmpty String currency, @NotNull AccountType accountType,
                       @NotNull AccountSubType accountSubType) {
        this(accountId, null, null, currency, accountType, accountSubType, null, null);
    }

    static AccountData transform(@NotNull PspAccountsSavingsData pspAccount, PspIdentifiersResponseDto identities, boolean detail, String accountId) {
        String externalId = pspAccount.getExternalId();
        if (accountId != null && !accountId.equals(externalId)) {
            return null;
        }

        PspAccountsSavingsStatusData status = pspAccount.getStatus();
        AccountStatus accountStatus = status == null ? null : status.getAccountStatus();
        PspAccountsSavingsTimelineData timeline = pspAccount.getTimeline();
        LocalDateTime statusUpdateDateTime = timeline == null ? null : timeline.getStatusUpdateDateTime();

        List<AccountIdentificationData> ids = null;
        if (detail && identities != null) {
            ids = new ArrayList<>();
            for (PspIdentifierData identity : identities.getIdentifiers()) {
                AccountIdentificationData transform = AccountIdentificationData.transform(identity);
                if (transform != null)
                    ids.add(transform);
            }
        }
        return new AccountData(externalId, accountStatus, statusUpdateDateTime, pspAccount.getCurrency().getCode(),
                pspAccount.getApiAccountType(), AccountSubType.SAVINGS, pspAccount.getApiNickName(), ids);
    }

    static AccountData transform(@NotNull PspAccountsLoanData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return null;
    }

    static AccountData transform(@NotNull PspAccountsGuarantorData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return null;
    }

    static AccountData transform(@NotNull PspAccountsShareData pspAccount, PspIdentifiersResponseDto identity, boolean detail, String accountId) {
        return null;
    }
}
