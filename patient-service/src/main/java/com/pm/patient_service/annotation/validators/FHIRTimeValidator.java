package com.pm.patient_service.annotation.validators;

import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateFHIRTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the HL7 FHIR R4 primitive type {@code time}.
 *
 * <p>
 * Accepted form:
 * {@code HH:MM:SS(.S+)?}
 * </p>
 *
 * <ul>
 * <li>hours: {@code 00} -> {@code 23}</li>
 * <li>minutes: {@code 00} -> {@code 59}</li>
 * <li>seconds: {@code 00} -> {@code 59}, {@code 60} is allowed for leap
 * seconds</li>
 * <li>optional fractional seconds, arbitrary precision</li>
 * </ul>
 *
 * <p>
 * The accepted pattern is the R4 regex of the {@code time} primitive type:
 * {@code ([01][0-9]|2[0-3]):[0-5][0-9]:([0-5][0-9]|60)(\.[0-9]+)?}.
 * </p>
 *
 * <p>
 * Limitation: only the syntax of the value is verified, it is not checked
 * whether the value corresponds to an actual time of day in a specific
 * timezone.
 * </p>
 */
public class FHIRTimeValidator implements ConstraintValidator<ValidateFHIRTime, String> {

    private static final String TIME_REGEX = "\\A" +
            "([01][0-9]|2[0-3])" +
            ":[0-5][0-9]" +
            ":([0-5][0-9]|60)" +
            "(\\.[0-9]+)?" +
            "\\z";

    private static final Pattern PATTERN = Pattern.compile(TIME_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return PATTERN.matcher(value).matches();
    }

}
