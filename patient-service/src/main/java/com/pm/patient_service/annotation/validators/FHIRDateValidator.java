package com.pm.patient_service.annotation.validators;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateFHIRDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the HL7 FHIR R4 primitive type {@code date}.
 * <p>
 * Allowed forms (FHIR R4, datatype "date"):
 * <ul>
 * <li>{@code YYYY}</li>
 * <li>{@code YYYY-MM}</li>
 * <li>{@code YYYY-MM-DD}</li>
 * </ul>
 * In addition to the syntactic pattern, the value must exist in the real
 * (proleptic Gregorian) calendar, e.g. {@code 2023-02-29} and
 * {@code 2024-02-30} are rejected even though their shape matches.
 */
public class FHIRDateValidator implements ConstraintValidator<ValidateFHIRDate, String> {

    // Syntax Validation

    // Date-only forms YYYY(-MM(-DD)?)?
    // YYYY: 0001 -> 9999 ([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)
    // MM: 01 -> 12 (0[1-9]|1[0-2])
    // DD: 01 -> 31 (0[1-9]|[1-2][0-9]|3[0-1])
    private static final String DATE_REGEX = "\\A" +
            "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)" +
            "(-(0[1-9]|1[0-2])" +
            "(-(0[1-9]|[1-2][0-9]|3[0-1]))?" +
            ")?" +
            "\\z";

    private static final Pattern PATTERN = Pattern.compile(DATE_REGEX);

    // Calendar Validation

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return PATTERN.matcher(value).matches() && hasValidCalendarDate(value);
    }

    private boolean hasValidCalendarDate(String value) {

        String[] parts = value.split("-");

        try {
            if (parts.length == 1) {
                int year = Integer.parseInt(parts[0]);
                return year > 0;
            }

            if (parts.length == 2) {
                YearMonth.parse(value);
                return true;
            }

            if (parts.length == 3) {
                LocalDate.parse(value);
                return true;
            }

            return false;

        } catch (DateTimeException | NumberFormatException ex) {
            return false;
        }
    }

}
