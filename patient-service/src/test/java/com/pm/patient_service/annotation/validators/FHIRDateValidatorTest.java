package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateFHIRDate;
import com.pm.patient_service.model.entity.Patient;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FHIRDateValidatorTest {

    private FHIRDateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FHIRDateValidator();
    }

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator jakartaValidator = factory.getValidator();

            Set<ConstraintViolation<Patient>> violations = jakartaValidator.validateValue(
                    Patient.class,
                    "birthDate",
                    "2023-02-29"); // Invalid calendar date
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getConstraintDescriptor().getAnnotation() instanceof ValidateFHIRDate));

            assertTrue(jakartaValidator.validateValue(
                    Patient.class,
                    "birthDate",
                    "2024-02-29").isEmpty()); // Valid leap-year date

            // Year-only form
            assertTrue(jakartaValidator.validateValue(
                    Patient.class,
                    "birthDate",
                    "1995").isEmpty());
            // Year-month form
            assertTrue(jakartaValidator.validateValue(
                    Patient.class,
                    "birthDate",
                    "1995-09").isEmpty());

            assertTrue(jakartaValidator.validateValue(
                    Patient.class,
                    "birthDate",
                    null).isEmpty());
        }
    }

    @Test
    void nullValueIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void emptyStringIsInvalid() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    void whitespaceOnlyIsInvalid() {
        assertFalse(validator.isValid("   ", null));
    }

    @Test
    void freeTextIsInvalid() {
        assertFalse(validator.isValid("not-a-date", null));
    }

    // Calendar-valid forms

    @Test
    void yearOnlyIsValid() {
        assertTrue(validator.isValid("2024", null));
        assertTrue(validator.isValid("0001", null)); // FHIR years start at 0001
    }

    @Test
    void yearMonthIsValid() {
        assertTrue(validator.isValid("2024-05", null));
    }

    @Test
    void fullDateIsValid() {
        assertTrue(validator.isValid("2024-05-17", null));
    }

    @Test
    void leapYearFebTwentyNineIsValid() {
        assertTrue(validator.isValid("2024-02-29", null));
        assertTrue(validator.isValid("2000-02-29", null));
    }

    @Test
    void thirtyOneDayMonthsAreValid() {
        assertTrue(validator.isValid("2024-01-31", null));
        assertTrue(validator.isValid("2024-12-31", null));
    }

    // Calendar-invalid forms

    @Test
    void nonLeapYearFebTwentyNineIsRejected() {
        assertFalse(validator.isValid("2023-02-29", null));
        assertFalse(validator.isValid("1900-02-29", null));
    }

    @Test
    void impossibleDateIsRejected() {
        assertFalse(validator.isValid("2024-02-30", null));
    }

    @Test
    void dayThirtyOneInThirtyDayMonthIsRejected() {
        assertFalse(validator.isValid("2024-04-31", null));
        assertFalse(validator.isValid("2024-06-31", null));
        assertFalse(validator.isValid("2024-09-31", null));
        assertFalse(validator.isValid("2024-11-31", null));
    }

    @Test
    void monthThirteenIsInvalid() {
        assertFalse(validator.isValid("2024-13-01", null));
    }

    @Test
    void monthZeroIsInvalid() {
        assertFalse(validator.isValid("2024-00-15", null));
    }

    @Test
    void dayThirtyTwoIsInvalid() {
        assertFalse(validator.isValid("2024-05-32", null));
    }

    @Test
    void dayZeroIsInvalid() {
        assertFalse(validator.isValid("2024-05-00", null));
    }

    @Test
    void yearZeroIsInvalid() {
        assertFalse(validator.isValid("0000", null));
    }

    @Test
    void dateTimeValueIsRejectedForDateType() {
        assertFalse(validator.isValid("2024-05-17T14:30:00Z", null));
    }

    @Test
    void partialTrailingHyphenIsInvalid() {
        assertFalse(validator.isValid("2024-", null));
        assertFalse(validator.isValid("2024-05-", null));
    }

    @Test
    void shortYearIsInvalid() {
        assertFalse(validator.isValid("24-05-17", null));
    }

}
