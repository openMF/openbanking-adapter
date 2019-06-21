/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspAccountsLoanTimelineData {

    private LocalDate submittedOnDate;
    private String submittedByUsername;
    private String submittedByFirstname;
    private String submittedByLastname;

    private LocalDate rejectedOnDate;
    private String rejectedByUsername;
    private String rejectedByFirstname;
    private String rejectedByLastname;

    private LocalDate withdrawnOnDate;
    private String withdrawnByUsername;
    private String withdrawnByFirstname;
    private String withdrawnByLastname;

    private LocalDate approvedOnDate;
    private String approvedByUsername;
    private String approvedByFirstname;
    private String approvedByLastname;

    private LocalDate expectedDisbursementDate;
    private LocalDate actualDisbursementDate;
    private String disbursedByUsername;
    private String disbursedByFirstname;
    private String disbursedByLastname;

    private LocalDate closedOnDate;
    private String closedByUsername;
    private String closedByFirstname;
    private String closedByLastname;

    private LocalDate expectedMaturityDate;
    private LocalDate writeOffOnDate;
    private String writeOffByUsername;
    private String writeOffByFirstname;
    private String writeOffByLastname;
}
