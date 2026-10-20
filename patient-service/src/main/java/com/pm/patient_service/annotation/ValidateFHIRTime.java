package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.FHIRTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FHIRTimeValidator.class)
public @interface ValidateFHIRTime {

    public String message() default "Invalid FHIR R4 time format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
