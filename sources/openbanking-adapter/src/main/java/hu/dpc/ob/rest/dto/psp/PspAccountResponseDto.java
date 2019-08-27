/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import hu.dpc.ob.rest.dto.ob.api.type.AccountStatus;
import hu.dpc.ob.rest.dto.ob.api.type.AccountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PspAccountResponseDto {

    @NotNull
    private String accountId;
    @NotNull
    @JsonProperty("savingProductId")
    private String productId;
    @NotNull
    private String productName;
    @NotNull
    private String shortProductName;
    @NotNull
    private String currency;
    @NotNull
    private BigDecimal accountBalance;
    @NotNull
    private BigDecimal availableBalance;
    @NotNull
    private String status; // PspAccountStatus

    private String subStatus; // PspAccountSubStatus

    private String accountType; // PspAccountType

    private String depositType; // PspDepositAccountType
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activatedOn;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate statusUpdateOn;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate withdrawnOn;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate balanceOn;
    @NotNull
    private List<PspIdentifierData> identifiers;

    @Transient
    public String getApiNickName() {
        return getProductName() == null
                ? (getShortProductName() == null ? getAccountId() : getShortProductName())
                : getProductName();
    }

    @Transient
    public AccountType getApiAccountType() {
        return getAccountType() == null || "INDIVIDUAL".equals(accountType) ? AccountType.PERSONAL : AccountType.BUSINESS;
    }

//    INVALID(0, "accountType.invalid"), //
//    INDIVIDUAL(1, "accountType.individual"), //
//    GROUP(2, "accountType.group"), //
//    JLG(3, "accountType.jlg");// JLG account given in group context


    @Transient
    @NotNull
    public AccountStatus getApiAccountStatus() {
        if (subStatus != null && !"NONE".equals(subStatus ))
            return AccountStatus.DISABLED;
        switch (status) {
            case "ACTIVE":
                return AccountStatus.ENABLED;
            case "SUBMITTED_AND_PENDING_APPROVAL":
            case "APPROVED":
            case "TRANSFER_IN_PROGRESS":
            case "TRANSFER_ON_HOLD":
                return AccountStatus.PENDING;
            default:
                return AccountStatus.DISABLED;
        }
    }

//    status
//    INVALID(0, "savingsAccountStatusType.invalid"), //
//    SUBMITTED_AND_PENDING_APPROVAL(100, "savingsAccountStatusType.submitted.and.pending.approval"), //
//    APPROVED(200, "savingsAccountStatusType.approved"), //
//    ACTIVE(300, "savingsAccountStatusType.active"), //
//    TRANSFER_IN_PROGRESS(303, "savingsAccountStatusType.transfer.in.progress"), //
//    TRANSFER_ON_HOLD(304, "savingsAccountStatusType.transfer.on.hold"), //
//    WITHDRAWN_BY_APPLICANT(400, "savingsAccountStatusType.withdrawn.by.applicant"), //
//    REJECTED(500, "savingsAccountStatusType.rejected"), //
//    CLOSED(600, "savingsAccountStatusType.closed"), PRE_MATURE_CLOSURE(700, "savingsAccountStatusType.pre.mature.closure"), MATURED(800,
//
//    subStatus                                                                                                                               "savingsAccountStatusType.matured");
//    NONE(0, "SavingsAccountSubStatusEnum.none"), //
//    INACTIVE(100, "SavingsAccountSubStatusEnum.inactive"), //
//    DORMANT(200, "SavingsAccountSubStatusEnum.dormant"),
//    ESCHEAT(300,"SavingsAccountSubStatusEnum.escheat"),
//    BLOCK(400, "SavingsAccountSubStatusEnum.block"),
//    BLOCK_CREDIT(500, "SavingsAccountSubStatusEnum.blockCredit"),
//    BLOCK_DEBIT(600, "SavingsAccountSubStatusEnum.blockDebit");

}
