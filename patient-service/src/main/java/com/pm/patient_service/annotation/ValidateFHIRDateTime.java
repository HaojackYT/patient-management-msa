package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.FHIRDateTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({
    ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FHIRDateTimeValidator.class)
public @interface ValidateFHIRDateTime {

    public String message() default "Invalid FHIR R4 dateTime format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
