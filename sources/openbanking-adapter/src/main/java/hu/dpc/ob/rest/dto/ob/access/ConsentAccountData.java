/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.ob.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.Consent;
import hu.dpc.ob.domain.entity.ConsentAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentAccountData {

    @JsonProperty(value = "AccountId", required = true)
    @NotEmpty
    @Size(max = 40)
    private String accountId;

    public ConsentAccountData(@NotEmpty @Size(max = 40) String accountId) {
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
