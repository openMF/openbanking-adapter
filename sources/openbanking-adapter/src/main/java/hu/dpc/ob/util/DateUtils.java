/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public final static String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static DateTimeFormatter ISO8601_UTC_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(ISO8601_DATE_TIME_PATTERN);

    private final static SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @NotNull
    public static String getTimezoneIdOfTenant() {
        return ZoneId.of("UTC").getId();
    }

    @NotNull
    public static ZoneId getZoneIdOfTenant() {
        return ZoneId.of(getTimezoneIdOfTenant());
    }

    @NotNull
    public static TimeZone getTimeZoneOfTenant() {
        return TimeZone.getTimeZone(getTimezoneIdOfTenant());
    }

    private static long getMillisOfTenant() {
        return System.currentTimeMillis();
    }

    /** @return business date of the current tenant */
    public static Date getDateOfTenant() {
        return new Date(getMillisOfTenant());
    }

    @NotNull
    public static Timestamp getDateTimeOfTenant() {
        return new Timestamp(getMillisOfTenant());
    }

    @NotNull
    public static LocalDate getLocalDateOfTenant() {
        return LocalDate.now(getZoneIdOfTenant());
    }

    @NotNull
    public static LocalDate getLocalDateOfTenant(Date date) {
        return date == null ? null : date.toInstant().atZone(getZoneIdOfTenant()).toLocalDate();
    }

    @NotNull
    public static LocalDateTime getLocalDateTimeOfTenant() {
        return LocalDateTime.now(getZoneIdOfTenant());
    }

    @NotNull
    public static LocalDateTime getLocalDateTimeOfTenant(Timestamp stamp) {
        return stamp == null ? null : stamp.toLocalDateTime();
    }

    public static int compareDatePart(@NotNull Date first, @NotNull Date second) {
        return getDatePartOf(first).compareTo(getDatePartOf(second));
    }

    public static boolean isEquals(@NotNull Date first, @NotNull Date second) {
        return first == null
                ? second == null
                : second != null && compareDatePart(first, second) == 0;
    }

    public static boolean isEquals(@NotNull LocalDate first, @NotNull LocalDate second) {
        return first == null
                ? second == null
                : second != null && compareDatePart(first, second) == 0;
    }

    public static boolean isBefore(@NotNull Date first, @NotNull Date second) {
        return first == null || (second != null && compareDatePart(first, second) < 0);
    }

    public static boolean isBefore(@NotNull LocalDate first, @NotNull LocalDate second) {
        return first == null || (second != null && compareDatePart(first, second) < 0);
    }

    public static boolean isAfter(@NotNull Date first, @NotNull Date second) {
        return first != null && (second == null || compareDatePart(first, second) > 0);
    }

    public static boolean isAfter(@NotNull LocalDate first, @NotNull LocalDate second) {
        return first != null && (second == null || compareDatePart(first, second) > 0);
    }

    /** @return the date which is not null and earlier than the other. Still can return null if both dates are null */
    public static Date getEarlierNotNull(@NotNull Date first, @NotNull Date second) {
        if (first == null)
            return second;
        if (second == null)
            return first;
        return isBefore(first, second) ? first : second;
    }

    public static int compareDatePart(@NotNull LocalDate first, @NotNull LocalDate second) {
        return first.compareTo(second);
    }

    @NotNull
    public static int compareToDateOfTenant(Date date) {
        return compareDatePart(date, getDateOfTenant());
    }

    @NotNull
    public static int compareToDateOfTenant(LocalDate date) {
        return compareDatePart(date, getLocalDateOfTenant());
    }

    @NotNull
    public static boolean isBeforeDateOfTenant(Date date) {
        return compareToDateOfTenant(date) < 0;
    }

    @NotNull
    public static boolean isBeforeDateOfTenant(LocalDate date) {
        return compareToDateOfTenant(date) < 0;
    }

    @NotNull
    public static boolean isAfterDateOfTenant(Date date) {
        return compareToDateOfTenant(date) > 0;
    }

    @NotNull
    public static boolean isAfterDateOfTenant(LocalDate date) {
        return compareToDateOfTenant(date) > 0;
    }

    @NotNull
    public static Date getDatePartOf(Date date) {
        return toDate(toLocalDate(date));
    }

    public static Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(getZoneIdOfTenant()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(getZoneIdOfTenant()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(getZoneIdOfTenant()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(getZoneIdOfTenant()).toLocalDateTime();
    }

    public static Date plusDays(Date date, int days) {
        return date == null
                ? null
                : days == 0 ? date : toDate(toLocalDate(date).plusDays(days));
    }

    public static LocalDate plusDays(LocalDate date, int days) {
        return date == null
                ? null
                : date.plusDays(days);
    }

    public static Date minusDays(Date date, int days) {
        return date == null
                ? null
                : days == 0 ? date : toDate(toLocalDate(date).minusDays(days));
    }

    public static LocalDate minusDays(LocalDate date, int days) {
        return date == null
                ? null
                : date.minusDays(days);
    }

    public static long daysBetween(@NotNull Date first, @NotNull Date second) {
        ZoneId zoneId = getZoneIdOfTenant();
        return ChronoUnit.DAYS.between(first.toInstant().atZone(zoneId), second.toInstant().atZone(zoneId));
    }

    public static long daysBetween(@NotNull LocalDate first, @NotNull LocalDate second) {
        return ChronoUnit.DAYS.between(first, second);
    }

    public static LocalDate parseLocalDate(final String stringDate, final String pattern, final Locale clientLocale) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withLocale(clientLocale).withZone(getZoneIdOfTenant());
        return LocalDate.parse(stringDate, formatter);
    }

    public static String formatToSqlDate(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(getTimeZoneOfTenant());
        final String formattedSqlDate = df.format(date);
        return formattedSqlDate;
    }

    public static LocalDateTime parseLocalFormatDateTime(String date) {
        if (date == null)
            return null;
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(LOCAL_DATE_TIME_FORMAT.parse(date).getTime()), ZoneOffset.UTC);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatLocalFormatDateTime(LocalDateTime date) {
        return date == null ? null : LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
    }

    @NotNull
    public static String toIsoString(@NotNull Date date) {
        return toIsoString(LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")));
    }

    @NotNull
    public static String toIsoString(@NotNull LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    @NotNull
    public static LocalDateTime fromIsoString(@NotNull String isoDateTimeString) {
        Assert.notNull(isoDateTimeString, "ISO date time must be given.");
        return LocalDateTime.from(Instant.parse(isoDateTimeString).atZone(ZoneOffset.UTC));
    }

    @NotNull
    public static LocalDate dateFromIsoString(@NotNull String isoDateString) {
        Assert.notNull(isoDateString, "ISO date time must be given.");
        int zIndex = isoDateString.indexOf("Z");
        String shortenedString = isoDateString.substring(0, zIndex);
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(shortenedString));
    }

    @NotNull
    public static String toIsoString(@NotNull LocalDate localDate) {
        Assert.notNull(localDate, "LocalDateTime must be given.");
        return localDate.format(DateTimeFormatter.ISO_DATE) + "Z";
    }

    @NotNull
    public static LocalDate toLocalDate(@NotNull LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.toLocalDate();
    }
}
