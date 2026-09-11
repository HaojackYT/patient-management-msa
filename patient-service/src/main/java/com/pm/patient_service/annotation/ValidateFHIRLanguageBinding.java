package com.pm.patient_service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pm.patient_service.annotation.validators.LanguageBindingValidator;
import com.pm.patient_service.model.datatype.CodeableConcept;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Bean Validation constraint for the terminology binding of the element
 * {@code Patient.communication.language}
 * 
 * <ul>
 * <li>Binding: Language codes with language and optionally a region modifier
 * (extensible)</li>
 * <li>Value Set:
 * <a href="https://hl7.org/fhir/us/core/ValueSet/simple-language">
 * Simple Language</a></li>
 * <li>Code System: urn:ietf:bcp:47 (RFC 5646)</li>
 * </ul>
 * 
 * <p>
 * Note: Use a 2 character language code if one exists and a 3 character code
 * if a 2 character code does not exist. Only the {@code language} subtag is
 * required, the {@code region} modifier is optional.
 * </p>
 *
 * <p>
 * Accepted {@link CodeableConcept} forms:
 * </p>
 * 
 *
 * <ul>
 * <li>{@code language}: 2 or 3 characters, e.g. {@code vi}, {@code haw}</li>
 * <li>{@code language-region}: language followed by a 2 letter (ISO 3166-1)
 * or 3 digit (UN M.49) region modifier, e.g. {@code vi-VN}, {@code es-419}</li>
 * <li>Subtags are matched case-insensitively as per BCP-47 syntax, e.g.
 * {@code EN-us} is accepted</li>
 * <li>Additional codings bound to other code systems are tolerated alongside a
 * valid BCP-47 coding (the binding strength is {@code extensible} and
 * alternate codings are allowed by FHIR)</li>
 * </ul>
 *
 * <p>
 * Rejected forms (non-exhaustive):
 * </p>
 * 
 * <ul>
 * <li>Concepts with no BCP-47 coding at all (e.g. only {@code text} or only a
 * local-system coding)</li>
 * <li>Codes carrying excluded subtags, e.g. {@code en-US-Latn} (script),
 * {@code de-DE-1901} (variant), {@code i-klingon} (ext-lang),
 * {@code x-pig-latin} (private-use)</li>
 * <li>Malformed codes, e.g. wrong subtag lengths ({@code abcd}, {@code en-USA})
 * or empty/whitespace codes</li>
 * </ul>
 *
 * <p>
 * Limitation: the region modifier is only validated for its syntactic
 * shape (2 letters or 3 digits), regardless of whether it is an actually
 * registered ISO 3166-1 / UN M.49 region code (e.g. {@code xx-ZZ}).
 * </p>
 *
 * @see LanguageBindingValidator
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LanguageBindingValidator.class)
public @interface ValidateFHIRLanguageBinding {

    public String message() default "Invalid language binding: " +
            "SHALL consist of a BCP-47 language subtag with an optional region modifier";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
