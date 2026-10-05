package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.NarrativeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NarrativeValidator.class)
public @interface ValidateNarrative {

    String message() default "Invalid Narrative: " +
            "The narrative SHALL contain only the basic html formatting elements and attributes (txt-1) " +
            "and SHALL have some non-whitespace content (txt-2)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
