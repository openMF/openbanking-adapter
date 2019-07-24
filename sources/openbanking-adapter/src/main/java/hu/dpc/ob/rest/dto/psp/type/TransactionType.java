/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hu.dpc.ob.rest.dto.psp.type;

import hu.dpc.ob.rest.dto.ob.api.type.CreditDebitType;

public enum TransactionType {
    INVALID,
    DEPOSIT,
    WITHDRAWAL,
    INTEREST_POSTING,
    WITHDRAWAL_FEE,
    ANNUAL_FEE,
    WAIVE_CHARGES,
    PAY_CHARGE,
    DIVIDEND_PAYOUT,
    INITIATE_TRANSFER,
    APPROVE_TRANSFER,
    WITHDRAW_TRANSFER,
    REJECT_TRANSFER,
    OVERDRAFT_INTEREST,
    WITHHOLD_TAX,
    ESCHEAT,
    AMOUNT_HOLD,
    AMOUNT_RELEASE,
    ;

    public CreditDebitType toCreditDebitType() {
        switch (this) {
            case DEPOSIT:
            case INTEREST_POSTING:
            case DIVIDEND_PAYOUT:
            case AMOUNT_RELEASE:
                return CreditDebitType.CREDIT;
            case WITHDRAWAL:
            case WITHDRAWAL_FEE:
            case ANNUAL_FEE:
            case PAY_CHARGE:
            case WITHDRAW_TRANSFER:
            case OVERDRAFT_INTEREST:
            case WITHHOLD_TAX:
                return CreditDebitType.DEBIT;
            default:
                return null;
        }
    }
}
