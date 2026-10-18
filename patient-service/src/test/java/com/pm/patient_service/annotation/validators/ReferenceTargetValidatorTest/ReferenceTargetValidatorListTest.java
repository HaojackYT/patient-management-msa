package com.pm.patient_service.annotation.validators.ReferenceTargetValidatorTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateFHIRReferenceTarget;
import com.pm.patient_service.annotation.validators.ReferenceTargetValidator;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Tests for {@link ReferenceTargetValidator} when validate
 * {@code List<Reference>}
 */
class ReferenceTargetValidatorListTest {

    // Mirror Patient.generalPractitioner
    static class GeneralPractitionerHolder {

        @ValidateFHIRReferenceTarget({ "Organization", "Practitioner", "PractitionerRole" })
        @Valid
        private List<Reference> generalPractitioner = new ArrayList<>();

        public List<Reference> getGeneralPractitioner() {
            return generalPractitioner;
        }

        public void setGeneralPractitioner(List<Reference> generalPractitioner) {
            this.generalPractitioner = generalPractitioner;
        }

    }

    // Mirror Patient.managingOrganization
    static class ManagingOrganizationHolder {

        @ValidateFHIRReferenceTarget({ "Organization" })
        @Valid
        private List<Reference> managingOrganization;

        public List<Reference> getManagingOrganization() {
            return managingOrganization;
        }

        public void setManagingOrganization(List<Reference> managingOrganization) {
            this.managingOrganization = managingOrganization;
        }

    }

    private static Validator jakartaValidator;

    private static final String MESSAGE = "Invalid Reference target: "
            + "the referenced resource type SHALL be one of the configured target types";

    private ReferenceTargetValidator validator;

    private ValidateFHIRReferenceTarget constraint;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        Field field = GeneralPractitionerHolder.class.getDeclaredField("generalPractitioner");
        constraint = field.getAnnotation(ValidateFHIRReferenceTarget.class);
        validator = new ReferenceTargetValidator();
        validator.initialize(constraint);
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        GeneralPractitionerHolder generalPractitionerHolder = new GeneralPractitionerHolder();

        Reference valid = new Reference();
        valid.setReference("Practitioner/123");

        Reference invalid = new Reference();
        invalid.setReference("Device/123"); // not allowed target type

        generalPractitionerHolder.setGeneralPractitioner(List.of(valid, invalid));

        Set<ConstraintViolation<GeneralPractitionerHolder>> violations = jakartaValidator.validate(
                generalPractitionerHolder);
        assertFalse(violations.isEmpty());
        assertEquals(MESSAGE + " (references[1]: 'Device')", violations.iterator().next().getMessage());
    }

    @Test
    void validTargetsPassValidationViaValidatorFactory() {
        GeneralPractitionerHolder generalPractitionerHolder = new GeneralPractitionerHolder();

        Reference practitionerContained = new Reference();
        practitionerContained.setReference("http://example.org/fhir/Practitioner/123");

        Reference practitionerRoleContained = new Reference();
        practitionerRoleContained.setReference(
                "http://example.org/fhir/PractitionerRole/123");
        practitionerRoleContained.setType(
                "http://hl7.org/fhir/StructureDefinition/PractitionerRole");

        generalPractitionerHolder.setGeneralPractitioner(
                List.of(practitionerContained, practitionerRoleContained));

        assertTrue(jakartaValidator.validate(generalPractitionerHolder).isEmpty());
    }

    @Test
    void annotationIsReusableWithDifferentTargets() {
        ManagingOrganizationHolder managingOrganizationHolder = new ManagingOrganizationHolder();

        Reference organizationContained = new Reference();
        organizationContained.setReference("Organization/123");

        Reference practitionerContained = new Reference();
        practitionerContained.setReference("Practitioner/123"); // not allowed target type

        managingOrganizationHolder.setManagingOrganization(
                List.of(organizationContained, practitionerContained));
        assertFalse(jakartaValidator.validate(managingOrganizationHolder).isEmpty());

        managingOrganizationHolder.setManagingOrganization(List.of(practitionerContained));
        assertFalse(jakartaValidator.validate(managingOrganizationHolder).isEmpty());
    }

    // Unit tests

    @Test
    void nullListIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void emptyListIsValid() {
        assertTrue(validator.isValid(new ArrayList<>(), null));
    }

    @Test
    void nullElementIsTreatedAsAbsent() {
        List<Reference> references = new ArrayList<>();
        references.add(null);
        assertTrue(validator.isValid(references, null));
    }

    @Test
    void referenceWithoutTypeInformationIsValid() { // type information = reference or type URI
        Reference reference = new Reference();
        assertTrue(validator.isValid(List.of(reference), null));
    }

    @Test
    void relativePractitionerReferenceIsValid() {
        Reference practitionerContained = new Reference();
        practitionerContained.setReference("Practitioner/123"); // relative
        assertTrue(validator.isValid(List.of(practitionerContained), null));
    }

    @Test
    void relativeOrganizationReferenceIsValid() {
        Reference organizationContained = new Reference();
        organizationContained.setReference("Organization/abc");
        assertTrue(validator.isValid(List.of(organizationContained), null));
    }

    @Test
    void relativePractitionerRoleReferenceIsValid() {
        Reference practitionerRoleContained = new Reference();
        practitionerRoleContained.setReference("PractitionerRole/xyz");
        assertTrue(validator.isValid(List.of(practitionerRoleContained), null));
    }

    @Test
    void absoluteUrlReferenceIsValid() {
        Reference practitionerContained = new Reference();
        practitionerContained.setReference("http://example.org/fhir/Practitioner/123");
        assertTrue(validator.isValid(List.of(practitionerContained), null));
    }

    @Test
    void disallowedRelativeReferenceIsInvalid() {
        Reference deviceContained = new Reference();
        deviceContained.setReference("Device/123"); // not allowed target type
        assertFalse(validator.isValid(List.of(deviceContained), null));
    }

    @Test
    void disallowedAbsoluteUrlReferenceIsInvalid() {
        Reference deviceContained = new Reference();
        deviceContained.setReference("http://example.org/fhir/Device/123");
        assertFalse(validator.isValid(List.of(deviceContained), null));
    }

    @Test
    void typeUriOnlyIsValid() {
        Reference organizationContained = new Reference();
        organizationContained.setType("http://hl7.org/fhir/StructureDefinition/Organization");
        assertTrue(validator.isValid(List.of(organizationContained), null));
    }

    @Test
    void disallowedTypeUriIsInvalid() {
        Reference deviceContained = new Reference();
        deviceContained.setType("http://hl7.org/fhir/StructureDefinition/Device");
        assertFalse(validator.isValid(List.of(deviceContained), null));
    }

    @Test
    void typeUriMismatchingAnAllowedReferenceIsInvalid() {
        Reference practitionerContained = new Reference();
        practitionerContained.setReference("Practitioner/123");
        practitionerContained.setType("http://hl7.org/fhir/StructureDefinition/Device");
        assertFalse(validator.isValid(List.of(practitionerContained), null));
    }

    @Test
    void containedReferenceIsValidPerDatatypeLevelLimitation() {
        Reference localReference = new Reference();
        localReference.setReference("#my-contained-practitioner");
        assertTrue(validator.isValid(List.of(localReference), null));
    }

    @Test
    void localReferenceWithSlashIsValidPerDatatypeLevelLimitation() {
        Reference localReference = new Reference();
        localReference.setReference("#Organization/1");
        assertTrue(validator.isValid(List.of(localReference), null));
    }

    @Test
    void urnReferenceIsValidPerDatatypeLevelLimitation() {
        Reference urnReference = new Reference();
        urnReference.setReference("urn:uuid:0c3151bd-1cbf-4d64-b04d-cd9187a4c6e0");
        assertTrue(validator.isValid(List.of(urnReference), null));
    }

    @Test
    void trailingSlashReferenceIsInvalid() {
        Reference deviceReference = new Reference();
        deviceReference.setReference("Device/123/");
        assertFalse(validator.isValid(List.of(deviceReference), null));
    }

    @Test // lack of type information
    void unsupportedTypeIsNotApplicable() {
        assertTrue(validator.isValid("Practitioner/123", null));
    }
}
