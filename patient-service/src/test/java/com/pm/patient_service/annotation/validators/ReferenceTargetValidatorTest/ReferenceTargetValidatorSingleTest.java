package com.pm.patient_service.annotation.validators.ReferenceTargetValidatorTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
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
 * Tests for {@link ReferenceTargetValidator} when validate single
 * {@code Reference}
 */
class ReferenceTargetValidatorSingleTest {

    // Mirror Practitioner.qualification.issuer
    static class QualificationIssuerHolder {

        @ValidateFHIRReferenceTarget({ "Organization" })
        @Valid
        private Reference issuer;

        public Reference getIssuer() {
            return issuer;
        }

        public void setIssuer(Reference v) {
            this.issuer = v;
        }

    }

    private static Validator jakartaValidator;

    private static final String MESSAGE = "Invalid Reference target: "
            + "the referenced resource type SHALL be one of the configured target types";

    private ReferenceTargetValidator singleValidator;

    private ValidateFHIRReferenceTarget singleConstraint;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        Field field = QualificationIssuerHolder.class.getDeclaredField("issuer");
        singleConstraint = field.getAnnotation(ValidateFHIRReferenceTarget.class);
        singleValidator = new ReferenceTargetValidator();
        singleValidator.initialize(singleConstraint);
    }

    // Integration tests

    @Test
    void singleReferenceConstraintViolationReportedViaValidatorFactory() {
        QualificationIssuerHolder qualificationIssuerHolder = new QualificationIssuerHolder();

        Reference practitionerContained = new Reference();
        practitionerContained.setReference("Practitioner/123"); // disallowed target type
        qualificationIssuerHolder.setIssuer(practitionerContained);

        Set<ConstraintViolation<QualificationIssuerHolder>> violations = jakartaValidator.validate(
                qualificationIssuerHolder);
        assertFalse(violations.isEmpty());
        assertEquals(MESSAGE + " (reference: 'Practitioner')",
                violations.iterator().next().getMessage());

        Reference organizationContained = new Reference();
        organizationContained.setReference("Organization/123");
        qualificationIssuerHolder.setIssuer(organizationContained);
        assertTrue(jakartaValidator.validate(qualificationIssuerHolder).isEmpty());
    }

    // Unit tests

    @Test
    void nullSingleReferenceIsValid() {
        assertTrue(singleValidator.isValid(null, null));
    }

    @Test
    void emptySingleReferenceIsValid() {
        assertTrue(singleValidator.isValid(new Reference(), null));
    }

    @Test
    void allowedSingleReferenceIsValid() {
        Reference organizationContained = new Reference();
        organizationContained.setReference("Organization/123");
        assertTrue(singleValidator.isValid(organizationContained, null));
    }

    @Test
    void disallowedSingleReferenceIsInvalid() {
        Reference practitionerContained = new Reference();
        practitionerContained.setReference("Practitioner/123");
        assertFalse(singleValidator.isValid(practitionerContained, null));
    }

    @Test
    void singleReferenceTypeUriIsValidated() {
        Reference organizationContained = new Reference();
        organizationContained.setType("http://hl7.org/fhir/StructureDefinition/Organization");
        assertTrue(singleValidator.isValid(organizationContained, null));

        Reference practitionerContained = new Reference();
        practitionerContained.setType("http://hl7.org/fhir/StructureDefinition/Practitioner");
        assertFalse(singleValidator.isValid(practitionerContained, null));
    }

    @Test
    void singleReferenceWithoutTypeInformationIsValid() { // type information = reference or type URI
        Reference urnContained = new Reference();
        urnContained.setReference("urn:uuid:0c3151bd-1cbf-4d64-b04d-cd9187a4c6e0");
        assertTrue(singleValidator.isValid(urnContained, null));

        Reference localContained = new Reference();
        localContained.setReference("#my-contained-organization");
        assertTrue(singleValidator.isValid(localContained, null));
    }
}
