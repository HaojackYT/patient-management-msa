package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.ReferenceValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ReferenceValidator.class)
public @interface ValidateFHIRReference {

    public String message() default "Invalid Reference: " +
            "SHALL have a contained resource if a local reference is provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
