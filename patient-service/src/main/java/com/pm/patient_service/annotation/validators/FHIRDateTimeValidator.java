package com.pm.patient_service.annotation.validators;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateFHIRDateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FHIRDateTimeValidator implements
        ConstraintValidator<ValidateFHIRDateTime, String> {

    // Syntax Validation

    private static final Pattern PATTERN = Pattern.compile("\\A" +
            "(" +
            // Date-only forms YYYY(-MM(-DD)?)?
            "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)" +
            "(-(0[1-9]|1[0-2])" +
            "(-(0[1-9]|[1-2][0-9]|3[0-1]))?" +
            ")?" +
            "|" +
            // Date-time form with mandatory timezone YYYY-MM-DDTHH:MM:SS(.S+)?(Z|±HH:MM)
            "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)" +
            "-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])" +
            "T([01][0-9]|2[0-3]):[0-5][0-9]:" +
            "([0-5][0-9]|60)(\\.[0-9]+)?" +
            "(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))" +
            ")" +
            "\\z");

    // Calendar Validation

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return PATTERN.matcher(value).matches() && hasValidCalendarDate(value);
    }

    private boolean hasValidCalendarDate(String value) {

        String datePart = value.contains("T")
                ? value.substring(0, value.indexOf('T'))
                : value;

        String[] parts = datePart.split("-");

        try {
            if (parts.length == 1) {
                int year = Integer.parseInt(parts[0]);
                return year > 0;
            }

            if (parts.length == 2) {
                YearMonth.parse(datePart);
                return true;
            }

            if (parts.length == 3) {
                LocalDate.parse(datePart);
                return true;
            }

            return false;

        } catch (DateTimeException ex) {
            return false;
        }
    }

}
