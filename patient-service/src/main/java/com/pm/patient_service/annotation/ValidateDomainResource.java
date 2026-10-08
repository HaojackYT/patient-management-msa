package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.DomainResourceValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DomainResourceValidator.class)
public @interface ValidateDomainResource {

    String message() default "Invalid DomainResource: " +
            "a contained resource SHALL NOT contain nested Resources (dom-2), " +
            "SHALL NOT have a meta.versionId or a meta.lastUpdated (dom-4) " +
            "and SHALL NOT have a security label (dom-5)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}