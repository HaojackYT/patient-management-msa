package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateFHIRDateTime;
import com.pm.patient_service.model.datatype.Period;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FHIRDateTimeValidatorTest {

    private FHIRDateTimeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FHIRDateTimeValidator();
    }

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator jakartaValidator = factory.getValidator();

            Period invalid = new Period();
            invalid.setStart("2024-02-30T00:00:00Z"); // Invalid calendar date
            invalid.setEnd("2024-05-17T14:30:00Z");

            Set<ConstraintViolation<Period>> violations = jakartaValidator.validate(invalid);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getConstraintDescriptor().getAnnotation() instanceof ValidateFHIRDateTime));

            Period valid = new Period();
            valid.setStart("2024-05-17T14:30:00Z");
            valid.setEnd("2024-12-31T10:00:00Z");
            assertTrue(jakartaValidator.validate(valid).isEmpty());

            Period missingStart = new Period();
            missingStart.setEnd("2024-05-17T14:30:00Z");
            violations = jakartaValidator.validate(missingStart);
            assertTrue(violations.stream()
                    .noneMatch(v -> v.getConstraintDescriptor().getAnnotation() instanceof ValidateFHIRDateTime));
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
    void yearOnlyIsValid() {
        assertTrue(validator.isValid("2024", null));
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
    void dateTimeWithZuluOffsetIsValid() {
        assertTrue(validator.isValid("2024-05-17T14:30:00Z", null));
    }

    @Test
    void dateTimeWithFractionalSecondsAndOffsetIsValid() {
        assertTrue(validator.isValid("2024-05-17T23:59:59.999+07:00", null));
    }

    @Test
    void leapSecondIsValid() { // 60 seconds
        assertTrue(validator.isValid("2016-12-31T23:59:60Z", null));
    }

    @Test
    void timezoneBoundaryFourteenHundredIsValid() {
        assertTrue(validator.isValid("2024-05-17T10:00:00+14:00", null));
    }

    @Test
    void impossibleDateIsRejected() {
        assertFalse(validator.isValid("2024-02-30", null));
    }

    @Test
    void impossibleDateTimeCalendarDateIsRejected() {
        assertFalse(validator.isValid("2024-02-30T10:00:00Z", null));
    }

    @Test
    void nonLeapYearFebTwentyNineIsRejected() {
        assertFalse(validator.isValid("2023-02-29", null));
    }

    @Test
    void monthThirteenIsInvalid() {
        assertFalse(validator.isValid("2024-13-01", null));
    }

    @Test
    void hourTwentyFourIsInvalid() {
        assertFalse(validator.isValid("2024-05-17T24:00:00Z", null));
    }

    @Test
    void missingTimezoneOnFullDateTimeIsInvalid() {
        assertFalse(validator.isValid("2024-05-17T14:30:00", null));
    }

    @Test
    void freeTextIsInvalid() {
        assertFalse(validator.isValid("not-a-date", null));
    }

    @Test
    void whitespaceOnlyIsInvalid() {
        assertFalse(validator.isValid("   ", null));
    }

}
