/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.rest.dto.psp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import hu.dpc.ob.util.DateUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class PspAccountsSavingsTimelineData {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate submittedOnDate;
    private String submittedByUsername;
    private String submittedByFirstname;
    private String submittedByLastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate rejectedOnDate;
    private String rejectedByUsername;
    private String rejectedByFirstname;
    private String rejectedByLastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate withdrawnOnDate;
    private String withdrawnByUsername;
    private String withdrawnByFirstname;
    private String withdrawnByLastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate approvedOnDate;
    private String approvedByUsername;
    private String approvedByFirstname;
    private String approvedByLastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activatedOnDate;
    private String activatedByUsername;
    private String activatedByFirstname;
    private String activatedByLastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate closedOnDate;
    private String closedByUsername;
    private String closedByFirstname;
    private String closedByLastname;


    @Transient
    public LocalDateTime getStatusUpdateDateTime() {
        LocalDate date = null;
        if (closedOnDate != null)
            date = closedOnDate;
        else if (withdrawnOnDate != null)
            date = withdrawnOnDate;
        else if (activatedOnDate != null)
            date = activatedOnDate;
        else if (rejectedOnDate != null)
            date = rejectedOnDate;
        else if (approvedOnDate != null)
            date = approvedOnDate;
        else if (submittedOnDate != null)
            date = submittedOnDate;
        return DateUtils.toLocalDateTime(date);
    }
}
