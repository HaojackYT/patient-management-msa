package com.pm.patient_service.annotation.validators;

import java.util.List;

import com.pm.patient_service.annotation.ValidateDomainResource;
import com.pm.patient_service.model.datatype.Meta;
import com.pm.patient_service.model.datatype.resource.AbstractResource;
import com.pm.patient_service.model.datatype.resource.domainResource.AbstractDomainResource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateDomainResource}.
 *
 * <p>
 * Enforces the FHIR R4 {@code DomainResource} invariants that can be evaluated
 * from a single resource instance.
 * </p>
 *
 * <ul>
 * <li><b>dom-2</b> — "If the resource is contained in another resource, it
 * SHALL NOT contain nested Resources"
 * ({@code contained.contained.empty()})</li>
 * <li><b>dom-4</b> — "If a resource is contained in another resource, it SHALL
 * NOT have a meta.versionId or a meta.lastUpdated"
 * ({@code contained.meta.versionId.empty() and contained.meta.lastUpdated.empty()})</li>
 * <li><b>dom-5</b> — "If a resource is contained in another resource, it SHALL
 * NOT have a security label"
 * ({@code contained.meta.security.empty()})</li>
 * </ul>
 *
 * <p>
 * Notes:
 * <ul>
 * <li>A {@code null} element inside {@code contained} is treated as
 * absent.</li>
 * <li>When a contained resource violates a rule, a violation is reported per
 * element (the index of the contained resource and the constraint key are
 * included in the message) instead of failing the whole list.</li>
 * <li>A single contained resource may violate several constraints:
 * one violation is reported per violated constraint.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Limitations:
 * <ul>
 * <li><b>dom-3</b> (a contained resource SHALL be referred to from elsewhere in
 * the resource or SHALL refer to the containing resource)
 * requires analysing every descendant reference of the containing resource,
 * so another validator is needed.</li>
 * <li><b>dom-6</b> (a resource should have narrative for robust management)
 * is a best practice guideline, so it is not enforced.</li>
 * </ul>
 * </p>
 */
// TODO: dom-3, dom-6
public class DomainResourceValidator implements
        ConstraintValidator<ValidateDomainResource, AbstractDomainResource> {

    private String message;

    @Override
    public void initialize(ValidateDomainResource constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AbstractDomainResource value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        List<AbstractResource> contained = value.getContained();

        if (contained == null || contained.isEmpty()) {
            return true;
        }

        boolean valid = true;
        for (int i = 0; i < contained.size(); i++) {
            AbstractResource resource = contained.get(i);

            // A null element is treated as absent
            if (resource == null) {
                continue;
            }

            if (!isValidContainedResource(resource, i, context)) {
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Enforces dom-2, dom-4 and dom-5 for a single contained resource and
     * reports one violation per violated constraint.
     *
     * @param resource the contained resource to check
     * @param index    of the contained resource, used in the violation message
     * @return {@code false} if at least one constraint is violated
     */
    private boolean isValidContainedResource(AbstractResource resource, int index,
            ConstraintValidatorContext context) {

        boolean valid = true;

        // dom-2: contained.contained.empty()
        // a domain resource only has a contained element
        if (resource instanceof AbstractDomainResource domainResource &&
                hasContainedResources(domainResource)) {

            valid = false;
            violation(context, index, "dom-2 nested contained resources");
        }

        Meta meta = resource.getMeta();

        if (meta == null) {
            return valid;
        }

        // dom-4: contained.meta.versionId.empty() and
        // contained.meta.lastUpdated.empty()
        if (meta.getVersionId() != null || meta.getLastUpdated() != null) {
            valid = false;
            violation(context, index,
                    "dom-4 contained resource has meta.versionId/meta.lastUpdated");
        }

        // dom-5: contained.meta.security.empty()
        if (meta.getSecurity() != null && !meta.getSecurity().isEmpty()) {
            valid = false;
            violation(context, index, "dom-5 contained resource has meta.security");
        }

        return valid;
    }

    private boolean hasContainedResources(AbstractDomainResource domainResource) {
        List<AbstractResource> nested = domainResource.getContained();
        return nested != null && !nested.isEmpty();
    }

    private void violation(ConstraintValidatorContext context, int index, String detail) {

        if (context != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    message + " (contained[" + index + "]: " + detail + ")")
                    .addConstraintViolation();
        }
    }

}