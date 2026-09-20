package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.OrganizationValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OrganizationValidator.class)
public @interface ValidateOrganization {

    String message() default "Invalid Organization: " +
            "SHALL at least have a name or an identifier, and possibly more than one.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
