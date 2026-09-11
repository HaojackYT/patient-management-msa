package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidateFHIRReference;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateFHIRReference}.
 * <p>
 * Implements the HL7 FHIR R4 {@code Reference} invariant <b>ref-1</b>:
 * "SHALL have a contained resource if a local reference is provided."
 * (FHIRPath:
 * {@code reference.startsWith('#').not() or (reference.substring(1,2) != '/')}).
 * </p>
 * <p>
 * Semantics per <a href="https://hl7.org/fhir/R4/references.html">FHIR R4 -
 * References between Resources</a>:
 * </p>
 * <ul>
 * <li>A <b>local reference</b> is a {@code reference} string starting with
 * {@code '#'}, pointing to a contained resource of the same resource
 * instance.</li>
 * <li>A local reference must be of the form {@code #id}, the {@code id} portion
 * must not contain {@code '/'} (i.e. {@code #/...} is not a resolvable local
 * reference and violates ref-1).</li>
 * </ul>
 * <p>
 * Limitation: the FHIRPath expression of ref-1 can only constrain the syntax of
 * the local reference, because the datatype {@code Reference} has no knowledge
 * of its containing resource (and therefore cannot resolve
 * {@code contained.id}). Verifying that the referenced contained resource
 * actually exists is the responsibility of an instance validator at the
 * resource level (a class-level constraint on the containing resource, e.g.
 * Patient).
 * </p>
 * <p>
 * Note: A blank {@code reference} (only whitespace) is treated as absent.
 * </p>
 */
public class ReferenceValidator implements ConstraintValidator<ValidateFHIRReference, Reference> {

    @Override
    public boolean isValid(Reference value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        String reference = value.getReference();

        // reference.empty() -> constraint satisfied regardless of local/external form
        if (isBlank(reference)) {
            return true;
        }

        // ref-1 does not apply to a local reference
        if (!reference.startsWith("#")) { // -> literal/relative/absolute reference
            return true;
        }

        return !reference.startsWith("#/");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
