/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private final static SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @NotNull
    public static ZoneId getZoneIdOfTenant() {
        return ThreadLocalContext.getTenant().getTimeZone();
    }

    @NotNull
    public static String getTimezoneIdOfTenant() {
        return getZoneIdOfTenant().getId();
    }

    @NotNull
    public static TimeZone getTimeZoneOfTenant() {
        return TimeZone.getTimeZone(getZoneIdOfTenant());
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

    @NotNull
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        return localDate == null ? null : localDate.atStartOfDay();
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

    public static boolean isAfter(@NotNull LocalDateTime first, @NotNull LocalDateTime second) {
        return first != null && (second == null || first.isAfter(second));
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
    public static int compareToDateOfTenant(@NotNull LocalDate date) {
        return compareDatePart(date, getLocalDateOfTenant());
    }

    @NotNull
    public static int compareToDateTimeOfTenant(@NotNull LocalDateTime dateTime) {
        return dateTime.compareTo(getLocalDateTimeOfTenant());
    }

    @NotNull
    public static boolean isBeforeDateOfTenant(Date date) {
        return date == null || compareToDateOfTenant(date) < 0;
    }

    @NotNull
    public static boolean isBeforeDateOfTenant(LocalDate date) {
        return date == null || compareToDateOfTenant(date) < 0;
    }

    @NotNull
    public static boolean isBeforeDateTimeOfTenant(LocalDateTime dateTime) {
        return dateTime == null || compareToDateTimeOfTenant(dateTime) < 0;
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
    public static boolean isAfterDateTimeOfTenant(LocalDateTime dateTime) {
        return dateTime == null || compareToDateTimeOfTenant(dateTime) > 0;
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

    public static LocalDateTime parseLocalFormatDateTime(String date) {
        return date == null ? null : LocalDateTime.ofInstant(OffsetDateTime.parse(date).toInstant(), ZoneOffset.UTC);
    }

    public static String formatLocalFormatDateTime(LocalDateTime date) {
        return date == null ? null : LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
    }

    public static LocalDateTime parseIsoDateTime(String date) {
        if (date == null)
            return null;

        if (date.length() > 10 && date.charAt(10) == 'T' && date.endsWith("Z"))
            return LocalDateTime.ofInstant(Instant.parse(date), ZoneOffset.UTC);

        return date.length() == 0 ? null : LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String formatIsoDateTime(LocalDateTime date) {
        return date == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date);
    }

    public static void main(String[] args) throws ParseException {
//        2019-12-31T11:16:31.663+01:00
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//        LocalDateTime parse1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.parse("2019-12-31T11:22:33+01:00").getTime()), ZoneOffset.UTC);
//        LocalDateTime parse2 = LocalDateTime.ofInstant(OffsetDateTime.parse("2019-12-31T11:22:33+01:00").toInstant(), ZoneOffset.UTC);
//        System.out.println("parse1 = " + parse1);
//        System.out.println("parse2 = " + parse2);
//        System.out.println("format1 = " + LOCAL_DATE_TIME_FORMAT.format(Date.from(parse1.toInstant(ZoneOffset.UTC))));
//        System.out.println("format2 = " + OffsetDateTime.of(parse2, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
//        final String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        LocalDateTimeDeserializer deserializer = LocalDateTimeDeserializer.INSTANCE;
//        LocalDateTimeSerializer serializer = LocalDateTimeSerializer.INSTANCE;
//        String dateS = "2019-12-31T11:00:00.000+02:00";
//        LocalDateTime date = LocalDateTime.parseDate(dateS);
//
//        LocalDateTime localDateTime = LocalDateTime.parseDate(dateS, DateTimeFormatter.ISO_DATE_TIME);
//
//        System.out.println("ZonedDateTime formatDate OFFSET: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate ZONED: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_ZONED_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate LOCAL: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE TIME: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE));
//
//        System.out.println("\nlocalDateTime formatDate LOCAL: " + localDateTime.formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE TIME: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE));
//
//        SimpleDateFormat zoneOrZuluFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSXXX");
//        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN);
//        SimpleDateFormat nozoneFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
//
//        "yyyy-mm-dd hh:mm:ss[.fffffffff]"
//        Timestamp timestamp = Timestamp.valueOf("2019-12-31 11:00:00.001"); // no zone allowed
//        System.out.println("\ntimestamp formatDate: " + zoneOrZuluFormat.formatDate(timestamp));
//
//        Date date = zoneOrZuluFormat.parseDate(dateS);
//
//        Instant instant = Instant.ofEpochMilli(date.getTime());
//        localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//
//        System.out.println("\ndate formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(date));
//        System.out.println("date formatDate isoDateFormat: " + isoDateFormat.formatDate(date));
//        System.out.println("date formatDate nozone: " + nozoneFormat.formatDate(date));
//
//
//        Date dateFromLocal = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate isoDateFormat: " + isoDateFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate nozone: " + nozoneFormat.formatDate(dateFromLocal));
//
//        zoneOrZuluFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        isoDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        nozoneFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//
//        System.out.println("\ndate formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(date)); //OK
//        System.out.println("date formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(date)); //OK
//        System.out.println("date formatDate nozone UTC: " + nozoneFormat.formatDate(date));
//
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate nozone UTC: " + nozoneFormat.formatDate(dateFromLocal));
//        BigDecimal amount;
//        System.out.println("123.45: " + AMOUNT_FORMAT.format(new BigDecimal("123.45")));
//        System.out.println("123.00: " + AMOUNT_FORMAT.format(new BigDecimal("123.00")));
//        System.out.println("123.40: " + AMOUNT_FORMAT.format(new BigDecimal("123.40")));
//        System.out.println("123.040: " + AMOUNT_FORMAT.format(new BigDecimal("123.040")));
//        System.out.println("0.00: " + AMOUNT_FORMAT.format(new BigDecimal("0.00")));
//        System.out.println("23456.70: " + AMOUNT_FORMAT.format(new BigDecimal("23456.70")));
//        System.out.println("23456789.10: " + AMOUNT_FORMAT.format(new BigDecimal("23456789.10")));
//        System.out.println("123456789.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("123456789.123456789")));
//        System.out.println("0.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("0.123456789")));
//        System.out.println("1234567890123456789.023456089: " + AMOUNT_FORMAT.format(new BigDecimal("1234567890123456789.023456089")));
    }
}
