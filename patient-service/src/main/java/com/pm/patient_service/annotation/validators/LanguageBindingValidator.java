package com.pm.patient_service.annotation.validators;

import java.util.Objects;
import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateFHIRLanguageBinding;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.Coding;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateFHIRLanguageBinding}.
 *
 * <p>
 * Implements the binding of {@code Patient.communication.language} to the
 * <a href="https://hl7.org/fhir/us/core/ValueSet/simple-language">
 * Simple Language</a></li>
 * ("Language codes with language and optionally a region modifier", strength
 * {@code extensible}).
 * </p>
 *
 * <p>
 * A {@link CodeableConcept} is considered valid when it carries at least one
 * {@link Coding} bound to {@code urn:ietf:bcp:47} whose code is a
 * {@code language} subtag optionally followed by a {@code region} modifier:
 * </p>
 *
 * <pre>
 * language        = 2 to 3 letters
 * language(-region) = 2 to 3 letters "-" (2 letters / 3 digits)
 * </pre>
 */
public class LanguageBindingValidator implements ConstraintValidator<ValidateFHIRLanguageBinding, CodeableConcept> {

    public static final String BCP47_SYSTEM = "urn:ietf:bcp:47";

    private static final Pattern LANGUAGE_REGION_PATTERN = Pattern
            .compile("\\A[a-zA-Z]{2,3}(-[a-zA-Z]{2}|-[0-9]{3})?\\z");

    @Override
    public boolean isValid(CodeableConcept value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        if (value.getCoding() == null) {
            return false;
        }

        // At least one coding MUST be a syntactically valid BCP-47
        // language(-region) code bound to the urn:ietf:bcp:47 code system
        return value.getCoding().stream()
                .filter(Objects::nonNull)
                // default system is BCP-47 if not specified
                .filter(coding -> coding.getSystem() == null || BCP47_SYSTEM.equals(coding.getSystem()))
                .anyMatch(coding -> hasValidLanguageCode(coding.getCode()));
    }

    private boolean hasValidLanguageCode(String code) {

        if (code == null) {
            return false;
        }

        return LANGUAGE_REGION_PATTERN.matcher(code).matches();
    }

}
