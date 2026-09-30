package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.FHIRIdValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Bean Validation constraint for the HL7 FHIR R4 primitive type {@code id}:
 * "Any combination of letters, numerals, "-" and ".", with a length limit
 * of 64 characters".
 *
 * <p>
 * Accepted form: {@code [A-Za-z0-9\-\.]{1,64}} — ids are case-insensitive so no
 * lowercase constraint is applied.
 * </p>
 *
 * @see <a href="https://hl7.org/fhir/R4/datatypes.html#id">FHIR R4 id</a>
 * @see FHIRIdValidator
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FHIRIdValidator.class)
public @interface ValidateFHIRId {

    public String message() default "Invalid FHIR R4 id format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
