package com.pm.patient_service.annotation.validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pm.patient_service.annotation.ValidateFHIRReferenceTarget;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateFHIRReferenceTarget}.
 * 
 * <p>
 * Enforces the <b>target resource types</b> of each {@link Reference} in a
 * {@code List}.
 * </p>
 * 
 * <p>
 * The target resource type of each {@link Reference} is derived syntactically
 * from:
 * <ul>
 * <li>{@code Reference.type} ({@code uri}): the last path segment of the URI.
 * ({@code http://.../.../.../Practitioner} → {@code Practitioner})</li>
 * <li>{@code Reference.reference}: the type segment of a literal reference.
 * (relative {@code Practitioner/123} or absolute
 * {@code http://.../.../Practitioner/123} → {@code Practitioner}).</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Limitations:
 * <ul>
 * <li>{@code Reference} datatype can't resolve contained references
 * so the target type of contained references cannot be verified, therefore
 * contain as valid.</li>
 * <li>URN-style references ({@code urn:uuid:...}) and references without
 * a resolvable type segment carry no target type information are treated
 * as valid.</li>
 * <li>A {@code null} element in the list is treated as absent.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * When a reference resolves to a disallowed target type, a violation is
 * reported per element (the disallowed type and its index are included in the
 * message) instead of failing the whole list.
 * </p>
 */
public class ReferenceTargetValidator implements ConstraintValidator<ValidateFHIRReferenceTarget, List<Reference>> {

    private Set<String> allowedTargetTypes;

    private String message;

    @Override
    public void initialize(ValidateFHIRReferenceTarget constraintAnnotation) {
        allowedTargetTypes = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(List<Reference> references, ConstraintValidatorContext context) {

        if (references == null) {
            return true;
        }

        boolean valid = true;

        for (int i = 0; i < references.size(); i++) {
            Reference reference = references.get(i);

            // A null element is treated as absent
            if (reference == null) {
                continue;
            }

            String targetType = typeFromTypeUri(reference.getType());

            if (targetType == null) {
                targetType = typeFromLiteralReference(reference.getReference());
            }

            // Target type cannot be determined syntactically -> constraint satisfied
            if (targetType == null) {
                continue;
            }

            if (!allowedTargetTypes.contains(targetType)) {
                valid = false;

                if (context != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message
                            + " (references[" + i + "]: '" + targetType + "')")
                            .addConstraintViolation();
                }
            }
        }

        return valid;
    }

    /**
     * Extracts the <b>target resource type</b> from the {@code type} URI.
     * ({@code http://.../.../.../Practitioner} → {@code Practitioner}).
     *
     * @return the last non-blank path segment of the URI or {@code null} if
     *         it cannot be determined
     */
    private String typeFromTypeUri(String type) {
        if (isBlank(type)) {
            return null;
        }

        String[] segments = type.trim().split("/");
        String lastSegment = segments[segments.length - 1].trim();

        return lastSegment.isEmpty() ? null : lastSegment;
    }

    /**
     * Extracts the target resource type from a literal reference, e.g.
     * {@code Practitioner/123} or {@code http://.../.../Practitioner/123} →
     * {@code Practitioner}.
     *
     * @return the resource type segment or {@code null} if it cannot be
     *         determined (contained {@code #id}, URN or missing id segment)
     */
    private String typeFromLiteralReference(String reference) {
        if (isBlank(reference)) {
            return null;
        }

        String value = reference.trim();

        // Local reference: target type cannot be resolved at the datatype level
        if (value.startsWith("#")) {
            return null;
        }

        // Absolute URL: skip the scheme://authority portion
        int schemeEnd = value.indexOf("//");
        if (schemeEnd >= 0) {
            // "+ 2" to skip the "//" after the scheme
            int pathStart = value.indexOf('/', schemeEnd + 2);

            if (pathStart < 0) {
                return null;
            }

            value = value.substring(pathStart + 1);
        } else if (hasUriScheme(value)) {
            // URN or scheme-only reference carries no type segment
            return null;
        }

        String[] segments = value.split("/");

        // Skip trailing empty segments (".../.../" → ".../...")
        int lastIndex = segments.length - 1;
        while (lastIndex >= 0 && segments[lastIndex].trim().isEmpty()) {
            lastIndex--;
        }

        // Literal reference: [path/]ResourceType/id
        if (lastIndex < 1) {
            return null;
        }

        String id = segments[lastIndex].trim();
        String type = segments[lastIndex - 1].trim();

        if (id.isEmpty() || type.isEmpty()) {
            return null;
        }

        return type;
    }

    private boolean hasUriScheme(String value) {
        return value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*"); // RFC 3986
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}