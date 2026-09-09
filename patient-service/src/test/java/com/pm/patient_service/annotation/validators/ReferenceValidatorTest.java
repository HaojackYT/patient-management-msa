package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.model.datatype.Identifier;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ReferenceValidatorTest {

    private ReferenceValidator validator;
    private Reference reference;

    private static Validator jakartaValidator;

    private static final String REF_1_MESSAGE = "Invalid Reference: " +
            "SHALL have a contained resource if a local reference is provided";

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ReferenceValidator();
        reference = new Reference();
    }

    @Test
    void constraintViolationReportedViaValidatorFactory() {

        Reference invalid = new Reference();
        invalid.setReference("#/Organization/1"); // '#/...'

        Set<ConstraintViolation<Reference>> violations = jakartaValidator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertEquals(REF_1_MESSAGE, violations.iterator().next().getMessage());

        Reference valid = new Reference();
        valid.setReference("#Organization/1");

        assertTrue(jakartaValidator.validate(valid).isEmpty());
    }

    // Custom Validator range

    @Test
    void nullObjectIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void nullReferenceIsValid() {
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void emptyStringReferenceIsTreatedAsAbsent() {
        reference.setReference("");
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void blankReferenceIsTreatedAsAbsent() {
        reference.setReference("   ");
        assertTrue(validator.isValid(reference, null));
    }

    // ref-1: "SHALL have a contained resource if a local reference is provided."

    // Non-local reference tests

    @Test
    void literalRelativeReferenceIsValid() {
        reference.setReference("Patient/123");
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void absoluteUrlReferenceIsValid() {
        reference.setReference("http://example.org/fhir/Patient/123");
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void urnReferenceIsValid() {
        reference.setReference("urn:uuid:0c3151bd-1cbf-4d64-b04d-cd9187a4c6e0");
        assertTrue(validator.isValid(reference, null));
    }

    // Local reference tests

    @Test
    void localReferenceWithoutSlashIsValid() {
        reference.setReference("#my-contained-organization");
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void localReferenceStartingWithSlashIsInvalid() {
        reference.setReference("#/Organization/1");
        assertFalse(validator.isValid(reference, null));
    }

    @Test
    void localReferenceWithSlashLaterIsValid() {
        reference.setReference("#Organization/1");
        assertTrue(validator.isValid(reference, null));
    }

    // Edge case tests

    @Test
    void bareHashIsValidPerLiteralR4Expression() {
        reference.setReference("#");
        assertTrue(validator.isValid(reference, null));
    }

    @Test
    void whitespaceBeforeHashIsTreatedAsExternalReference() {
        reference.setReference(" #/Organization/1");
        assertTrue(validator.isValid(reference, null));
    }

    // Cascade integration

    @Test
    void localReferenceViolationCascadesThroughIdentifierAssigner() {
        Reference invalidAssigner = new Reference();
        invalidAssigner.setReference("#/Organization/1");

        Identifier identifier = new Identifier();
        identifier.setSystem("http://example.org/identifier-systems/mrn");
        identifier.setValue("12345");
        identifier.setAssigner(invalidAssigner);

        Set<ConstraintViolation<Identifier>> violations = jakartaValidator.validate(identifier);
        assertFalse(violations.isEmpty());
        assertEquals(REF_1_MESSAGE, violations.iterator().next().getMessage());
    }

    @Test
    void validLocalReferenceCascadesThroughIdentifierAssignerWithoutViolation() {
        Reference assigner = new Reference();
        assigner.setReference("#contained-org");

        Identifier identifier = new Identifier();
        identifier.setSystem("http://example.org/identifier-systems/mrn");
        identifier.setValue("12345");
        identifier.setAssigner(assigner);

        Set<ConstraintViolation<Identifier>> violations = jakartaValidator.validate(identifier);
        assertTrue(violations.isEmpty());
    }

}
