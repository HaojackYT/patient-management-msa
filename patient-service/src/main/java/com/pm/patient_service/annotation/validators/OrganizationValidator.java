package com.pm.patient_service.annotation.validators;

import java.util.List;

import com.pm.patient_service.annotation.ValidateOrganization;
import com.pm.patient_service.model.datatype.Identifier;
import com.pm.patient_service.model.datatype.Organization;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateOrganization}.
 *
 * <p>
 * Implements the HL7 FHIR R4 {@code Organization} invariant <b>org-1</b>:
 * "SHALL at least have a name or an identifier, and possibly more than one."
 * (FHIRPath: {@code identifier.count() + name.count() > 0})
 * </p>
 *
 * <p>
 * Notes:
 * <ul>
 * <li>In the current model {@code Organization.name} is a single (0..1)
 * string, so {@code name.count()} contributes 1 when a non-blank name is
 * present and 0 otherwise; "possibly more than one" therefore applies to
 * {@code identifier} (0..*), and a name may be combined with any number of
 * identifiers.</li>
 * <li>A blank {@code name} (only whitespace) is treated as absent.</li>
 * <li>{@code null} entries inside {@code identifier} are ignored when
 * counting.</li>
 * </ul>
 * </p>
 */
public class OrganizationValidator implements
        ConstraintValidator<ValidateOrganization, Organization> {

    @Override
    public boolean isValid(Organization value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        // org-1: identifier.count() + name.count() > 0
        return countIdentifiers(value.getIdentifier()) + (hasName(value.getName()) ? 1 : 0) > 0;
    }

    private int countIdentifiers(List<Identifier> identifiers) {

        if (identifiers == null) {
            return 0;
        }

        int count = 0;
        for (Identifier identifier : identifiers) {
            if (identifier != null) {
                count++;
            }
        }
        return count;
    }

    private boolean hasName(String name) {
        return name != null && !name.trim().isEmpty();
    }

}