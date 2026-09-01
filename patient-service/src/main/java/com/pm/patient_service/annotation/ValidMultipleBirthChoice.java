package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.MultipleBirthChoiceValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MultipleBirthChoiceValidator.class)
public @interface ValidMultipleBirthChoice {

    String message() default "Must have either multipleBirthBoolean or multipleBirthInteger, but not both";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
