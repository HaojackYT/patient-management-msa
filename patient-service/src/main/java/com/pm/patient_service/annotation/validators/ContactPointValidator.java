package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidateContactPoint;
import com.pm.patient_service.model.datatype.ContactPoint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateContactPoint}.
 * <p>
 * Implements the HL7 FHIR R4 {@code ContactPoint} invariant <b>cpt-2</b>:
 * "A system is required if a value is provided."
 * (FHIRPath: {@code value.empty() or system.exists()}).
 * </p>
 * <p>
 * Note: A blank {@code value} (only whitespace) is treated as absent.
 * </p>
 */
public class ContactPointValidator implements ConstraintValidator<ValidateContactPoint, ContactPoint> {

    @Override
    public boolean isValid(ContactPoint value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        // value.empty() -> constraint satisfied regardless of system
        if (isBlank(value.getValue())) {
            return true;
        }

        // value exists -> system.exists() is required (cpt-2)
        return value.getSystem() != null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}