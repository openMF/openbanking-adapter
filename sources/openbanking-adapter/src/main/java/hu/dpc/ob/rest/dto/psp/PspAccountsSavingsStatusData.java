/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import hu.dpc.ob.rest.dto.ob.api.type.AccountStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspAccountsSavingsStatusData extends EnumOptionData {

    private boolean submittedAndPendingApproval;
    private boolean approved;
    private boolean rejected;
    private boolean withdrawnByApplicant;
    private boolean active;
    private boolean closed;
    private boolean prematureClosed;
    private boolean transferInProgress;
    private boolean transferOnHold;
    private boolean matured;

    @Transient
    public AccountStatus getAccountStatus() {
        if (active)
            return AccountStatus.ENABLED;
        if (submittedAndPendingApproval || transferInProgress || transferOnHold)
            return AccountStatus.PENDING;
        return AccountStatus.DISABLED;
    }
}
