package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.ExtensionValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ExtensionValidator.class)
public @interface ValidateFHIRExtension {

    String message() default "Must have either extensions or value[x], not both";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
