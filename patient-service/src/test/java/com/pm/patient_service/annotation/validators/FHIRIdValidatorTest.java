package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateFHIRId;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FHIRIdValidatorTest {

    static class IdHolder {

        @ValidateFHIRId
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    private FHIRIdValidator validator;

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new FHIRIdValidator();
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        IdHolder holder = new IdHolder();
        holder.setId("patient/123");

        Set<ConstraintViolation<IdHolder>> violations = jakartaValidator.validate(holder);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid FHIR R4 id format.", violations.iterator().next().getMessage());

        holder.setId("patient-123");
        assertTrue(jakartaValidator.validate(holder).isEmpty());
    }

    // Custom Validator range

    @Test
    void nullIdIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void lettersNumeralsDashAndDotAreValid() {
        assertTrue(validator.isValid("1", null));
        assertTrue(validator.isValid("patient-123", null));
        assertTrue(validator.isValid("Patient.123", null));
        assertTrue(validator.isValid("123e4567-e89b-12d3-a456-426614174000", null));
    }

    @Test
    void idOf64CharactersIsValid() {
        assertTrue(validator.isValid("a".repeat(64), null));
    }

    @Test
    void idLongerThan64CharactersIsInvalid() {
        assertFalse(validator.isValid("a".repeat(65), null));
    }

    @Test
    void emptyIdIsInvalid() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    void idWithOtherCharactersIsInvalid() {
        assertFalse(validator.isValid("patient 123", null));
        assertFalse(validator.isValid("patient/123", null));
        assertFalse(validator.isValid("patient_123", null));
        assertFalse(validator.isValid("#patient-123", null));
    }

}
