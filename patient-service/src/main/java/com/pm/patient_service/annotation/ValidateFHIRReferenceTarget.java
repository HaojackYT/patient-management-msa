package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.ReferenceTargetValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ReferenceTargetValidator.class)
public @interface ValidateFHIRReferenceTarget {

    /**
     * The allowed FHIR resource types for the referenced targets
     * {@code {"Organization", "Practitioner", "PractitionerRole"}}.
     */
    String[] value();

    public String message() default "Invalid Reference target: " +
            "the referenced resource type SHALL be one of the configured target types";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}