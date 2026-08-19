package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidatePeriod;
import com.pm.patient_service.model.datatype.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PeriodValidator implements ConstraintValidator<ValidatePeriod, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (!(value instanceof Period period)) {
            return true;
        }

        String start = period.getStart();
        String end = period.getEnd();

        if (start == null || end == null) {
            return true;
        }

        ZonedDateTime startZdt = parseFlexible(start);
        ZonedDateTime endZdt = parseFlexible(end);

        // Expect unexpected values to avoid a NullPointerException.
        if (startZdt == null || endZdt == null) {
            return true;
        }

        // start <= end
        return !startZdt.isAfter(endZdt);
    }

    /**
     * Parses a flexible ISO-8601 date-time string supported by the Period datatype.
     * <p>
     * Supported forms:
     * <ul>
     * <li>{@code YYYY}</li>
     * <li>{@code YYYY-MM}</li>
     * <li>{@code YYYY-MM-DD}</li>
     * <li>{@code YYYY-MM-DDTHH:MM:SS} (with optional milliseconds)</li>
     * <li>{@code YYYY-MM-DDTHH:MM:SS[.fff](Z|±HH:MM)}</li>
     * </ul>
     * Date-only values are treated as the start of the day (00:00:00).
     * Values without an explicit offset are assumed to be UTC.
     *
     * @param value the date-time string to parse
     * @return the parsed {@link ZonedDateTime}, or {@code null} if it cannot be
     *         parsed
     */
    private ZonedDateTime parseFlexible(String value) {

        try {
            String normalized = value.trim();

            // Date-only forms -> midnight (00:00:00) UTC
            // YYYY
            if (normalized.length() == 4) {
                return ZonedDateTime.of(
                        Integer.parseInt(normalized), 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            }

            // YYYY-MM
            if (normalized.length() == 7) {
                return ZonedDateTime.of(
                        Integer.parseInt(normalized.substring(0, 4)), // YYYY
                        Integer.parseInt(normalized.substring(5, 7)), // MM
                        1, 0, 0, 0, 0, ZoneOffset.UTC);
            }

            // YYYY-MM-DD
            if (normalized.length() == 10) {
                return ZonedDateTime.of(
                        Integer.parseInt(normalized.substring(0, 4)), // YYYY
                        Integer.parseInt(normalized.substring(5, 7)), // MM
                        Integer.parseInt(normalized.substring(8, 10)), // DD
                        0, 0, 0, 0, ZoneOffset.UTC);
            }

            // Date-time forms
            if (hasZoneSuffix(normalized)) {
                return ZonedDateTime.parse(normalized, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }

            // No offset -> assume UTC
            return OffsetDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException | NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Detects whether the date-time string carries a timezone/offset suffix.
     * The time portion ({@code HH:MM:SS}) never contains {@code +} or {@code -},
     * so any of those characters (or a trailing {@code Z}/{@code z}) after the
     * {@code T} separator indicates the presence of an explicit zone.
     */
    private boolean hasZoneSuffix(String value) {

        int tIndex = value.indexOf('T');

        if (tIndex < 0) {
            return false;
        }

        String tail = value.substring(tIndex + 1);

        return tail.indexOf('Z') >= 0
                || tail.indexOf('z') >= 0
                || tail.indexOf('+') >= 0
                || tail.indexOf('-') >= 0;
    }

}