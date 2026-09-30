package com.pm.patient_service.annotation.validators;

import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateFHIRId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the HL7 FHIR R4 primitive type {@code id}.
 *
 * <p>
 * Accepted form:
 * {@code [A-Za-z0-9\-\.]{1,64}} — any combination of letters, numerals,
 * {@code "-"} and {@code "."}, with a length limit of 64 characters.
 * </p>
 *
 * <p>
 * Note: ids are case-insensitive, so both lower and upper case letters are
 * accepted.
 * </p>
 *
 * <p>
 * Limitation: only the syntax of the id is verified, uniqueness within a server
 * and the "once assigned, this value never changes" rule cannot be verified.
 * </p>
 */
public class FHIRIdValidator implements ConstraintValidator<ValidateFHIRId, String> {

    // \\- = -, \\. = ., \\A = beginning of string, \\z = end of string
    private static final String ID_REGEX = "\\A[A-Za-z0-9\\-\\.]{1,64}\\z";

    private static final Pattern PATTERN = Pattern.compile(ID_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return PATTERN.matcher(value).matches();
    }

}
