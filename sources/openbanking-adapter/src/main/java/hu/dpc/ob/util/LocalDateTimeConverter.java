/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.ob.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute == null ? null : new Timestamp(toEpochMillis(attribute));
    }

    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        return dbData == null ? null : fromEpochMillis(dbData.getTime());
    }

    static long toEpochMillis(@NotNull LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    static long toEpochDay(@NotNull LocalDate localDate) {
        return Long.valueOf(localDate.toEpochDay());
    }

    @NotNull
    static LocalDateTime fromEpochMillis(long epochMillis) {
        return LocalDateTime.from(Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC));
    }
}
