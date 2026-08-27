package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.PeriodValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PeriodValidator.class)
public @interface ValidatePeriod {

    public String message() default "Invalid period: start date must be <= end date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
