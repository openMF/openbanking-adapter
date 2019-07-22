/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.domain.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.dpc.ob.domain.entity.PersistentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static hu.dpc.ob.domain.type.ApiScope.AIS;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public enum PermissionCode implements PersistentType<PermissionCode, String>, DisplayType {

    @JsonProperty("ReadAccountsBasic")
    READ_ACCOUNTS_BASIC("ReadAccountsBasic", AIS, "Read basic accountId information"),
    @JsonProperty("ReadAccountsDetail")
    READ_ACCOUNTS_DETAIL("ReadAccountsDetail", AIS, "Read accountId identification details"),
    @JsonProperty("ReadBalances")
    READ_BALANCES("ReadBalances", AIS, "Read all balance information"),
    @JsonProperty("ReadBeneficiariesBasic")
    READ_BENEFICIARY_BASIC("ReadBeneficiariesBasic", AIS, "Read basic beneficiary details"),
    @JsonProperty("ReadBeneficiariesDetail")
    READ_BENEFICIARY_DETAIL("ReadBeneficiariesDetail", AIS, "Read accountId identification details for the beneficiary"),
    @JsonProperty("ReadDirectDebits")
    READ_DIRECT_DEBITS("ReadDirectDebits", AIS, "Read all direct debit information"),
    @JsonProperty("ReadOffers")
    READ_OFFERS("ReadOffers", AIS, "Read all offer information"),
    @JsonProperty("ReadPAN")
    READ_PAN("ReadPAN", AIS, "Access PAN in the clear across the available endpoints"),
    @JsonProperty("ReadParty")
    READ_PARTY("ReadParty", AIS, "Read party information on the accountId owner"),
    @JsonProperty("ReadPartyPSU")
    READ_PARTY_PSU("ReadPartyPSU", AIS, "Read party information on the User logged in"),
    @JsonProperty("ReadProducts")
    READ_PRODUCTS("ReadProducts", AIS, "Read all product information relating to the accountId"),
    @JsonProperty("ReadScheduledPaymentsBasic")
    READ_PAYMENTS_BASIC("ReadScheduledPaymentsBasic", AIS, "Read basic statement details"),
    @JsonProperty("ReadScheduledPaymentsDetail")
    READ_PAYMENTS_DETAIL("ReadScheduledPaymentsDetail", AIS, "Read additional elements in the payload"),
    @JsonProperty("ReadStandingOrdersBasic")
    READ_STANDING_ORDERS_BASIC("ReadStandingOrdersBasic", AIS, "Read basic standing order information"),
    @JsonProperty("ReadStandingOrdersDetail")
    READ_STANDING_ORDERS_DETAIL("ReadStandingOrdersDetail", AIS, "Read accountId identification details for beneficiary of the standing order"),
    @JsonProperty("ReadStatementsBasic")
    READ_STATEMENTS_BASIC("ReadStatementsBasic", AIS, "Read basic statement details"),
    @JsonProperty("ReadStatementsDetail")
    READ_STATEMENTS_DETAIL("ReadStatementsDetail", AIS, "Download the statement file"),
    @JsonProperty("ReadTransactionsBasic")
    READ_TRANSACTIONS_BASIC("ReadTransactionsBasic", AIS, "Read basic payment information"),
    @JsonProperty("ReadTransactionsCredits")
    READ_TRANSACTIONS_CREDITS("ReadTransactionsCredits", AIS, "Access to credit transactions"),
    @JsonProperty("ReadTransactionsDebits")
    READ_TRANSACTIONS_DEBITS("ReadTransactionsDebits", AIS, "Access to debit transactions"),
    @JsonProperty("ReadTransactionsDetail")
    READ_TRANSACTIONS_DETAIL("ReadTransactionsDetail", AIS, "Read statement data elements which may leak other information about the accountId"),
    ;


    private static final Map<String, PermissionCode> BY_ID = Arrays.stream(PermissionCode.values()).collect(Collectors.toMap(PermissionCode::getId, e -> e));
    private static final Map<ApiScope, List<PermissionCode>> BY_SCOPE = Arrays.stream(PermissionCode.values()).collect(Collectors.groupingBy(PermissionCode::getScope));

    @NotNull
    private final String id;
    @NotNull
    private final ApiScope scope;
    @NotNull
    private final String displayName;


    public static PermissionCode fromId(String id) {
        return BY_ID.get(id);
    }

    @Override
    public String toId() {
        return id;
    }

    @Override
    public String getDisplayLabel() {
        return id;
    }

    @Override
    public String getDisplayText() {
        return displayName;
    }

    public boolean isValid(ApiScope scope) {
        return this.scope == scope;
    }

    public static List<PermissionCode> getPermissions(ApiScope scope) {
        return BY_SCOPE.get(scope);
    }
}
