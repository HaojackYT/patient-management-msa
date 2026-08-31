package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.AttachmentValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AttachmentValidator.class)
public @interface ValidateFHIRAttachment {

    String message() default "Invalid FHIR R4 Attachment.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}