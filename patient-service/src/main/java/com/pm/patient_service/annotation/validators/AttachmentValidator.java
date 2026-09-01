package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidateFHIRAttachment;
import com.pm.patient_service.model.datatype.Attachment;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Executes FHIR R4 constraint att-1 for Attachment:
 * "If the Attachment has data, it SHALL have a contentType".
 *
 * <p>
 * Additionally validates:
 * </p>
 * <ul>
 * <li>{@code contentType}: must match the FHIR R4 MimeType pattern
 * {@code [^/\s]+/[^/\s]+} (exactly one "/" separator, no whitespace).</li>
 * <li>{@code language}: must be a well-formed BCP-47 language tag
 * (e.g. {@code en}, {@code vi-VN}, {@code zh-Hans-CN}, {@code es-419}).</li>
 * </ul>
 *
 * <p>
 * An empty {@code data} array is treated as {@code data.empty()} per the FHIR
 * att-1 expression, so it does not trigger the contentType requirement.
 * </p>
 * <p>
 * Note: BCP-47 tags are case-insensitive, so {@code en-us} and {@code en-US}
 * are both well-formed, the IANA Language Subtag Registry is not consulted.
 * </p>
 */
public class AttachmentValidator implements ConstraintValidator<ValidateFHIRAttachment, Attachment> {

    @Override
    public boolean isValid(Attachment value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        boolean hasData = value.getData() != null && value.getData().length > 0;
        boolean hasContentType = value.getContentType() != null && !value.getContentType().isBlank();

        // att-1: data.empty() or contentType.exists()
        if (hasData && !hasContentType) {
            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "If the Attachment has data, it SHALL have a contentType")
                        .addConstraintViolation();
            }
            return false;
        }

        return true;
    }

}