package com.pm.patient_service.annotation.validators;

import java.util.Map;
import java.util.Objects;

import com.pm.patient_service.annotation.ValidateFHIRExtension;
import com.pm.patient_service.model.datatype.Extension;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Executes FHIR R4 constraint ext-1 for Extension:
 * "Must have either extensions or value[x], not both".
 *
 * <p>
 * Additionally validates:
 * </p>
 * <ul>
 * <li>Must have at least one of the two: {@code extension} or
 * {@code value[x]}.</li>
 * <li>Maximum of one {@code value[x]} element per Extension.</li>
 * </ul>
 */
public class ExtensionValidator implements ConstraintValidator<ValidateFHIRExtension, Extension> {

    @Override
    public boolean isValid(Extension value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        boolean hasExtensions = value.getExtension() != null && !value.getExtension().isEmpty();

        Map<String, Object> valueX = value.getValueX();
        boolean hasValue = valueX != null && valueX.values().stream().anyMatch(Objects::nonNull);

        if (context != null) {
            context.disableDefaultConstraintViolation();
        }

        if (hasExtensions && hasValue) {
            return violation(
                    context, "Must have either extensions or value[x], not both");
        }

        if (!hasExtensions && !hasValue) {
            return violation(
                    context, "Extension must have either extensions or value[x]");
        }

        if (hasValue && countValues(valueX) > 1) {
            return violation(
                    context, "Extension must have at most one value[x]");
        }

        return true;
    }

    private long countValues(Map<String, Object> valueX) {
        return valueX.values().stream().filter(Objects::nonNull).count();
    }

    private boolean violation(ConstraintValidatorContext context, String message) {
        if (context != null) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return false;
    }

}
