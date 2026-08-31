package com.pm.patient_service.annotation.validators;

import java.util.regex.Pattern;

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
public class AttachmentValidator implements ConstraintValidator<ValidateFHIRAttachment, Object> {

    private static final Pattern MIME_TYPE_PATTERN = Pattern.compile(
            "^[^/\\s]+/[^/\\s]+$");

    private static final Pattern BCP_47_PATTERN = Pattern.compile(
            "^[A-Za-z]{2,8}(-[A-Za-z0-9]{1,8})*$");

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (!(value instanceof Attachment attachment)) {
            return true;
        }

        if (context != null) {
            context.disableDefaultConstraintViolation();
        }

        boolean valid = true;
        boolean hasData = attachment.getData() != null && attachment.getData().length > 0;

        String contentType = attachment.getContentType();
        String language = attachment.getLanguage();

        // att-1: data.empty() or contentType.exists()
        if (hasData && (contentType == null || contentType.isBlank())) {
            valid = violation(context,
                    "If the Attachment has data, it SHALL have a contentType");
        }

        // contentType: FHIR R4 MimeType pattern
        if (contentType != null && !contentType.isBlank()
                && !MIME_TYPE_PATTERN.matcher(contentType).matches()) {
            valid = violation(context,
                    "Attachment contentType must be a valid MIME type (e.g. 'image/png')");
        }

        // language: FHIR R4 BCP-47 binding
        if (language != null && !language.isBlank()
                && !BCP_47_PATTERN.matcher(language).matches()) {
            valid = violation(context,
                    "Attachment language must be a valid BCP-47 code (e.g. 'vi-VN')");
        }

        return valid;
    }

    private boolean violation(ConstraintValidatorContext context, String message) {
        if (context != null) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return false;
    }

}