package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.model.datatype.Period;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class PeriodValidatorTest {

    private PeriodValidator validator;
    private Period period;

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PeriodValidator();
        period = new Period();
    }

    @Test // integration between @PeriodValidator and Jakarta Bean Validation Factory
    void constraintViolationReportedViaValidatorFactory() {
        Period invalid = new Period();
        // start > end
        invalid.setStart("2024-12-31T10:00:00Z");
        invalid.setEnd("2024-01-01T10:00:00Z");

        Set<ConstraintViolation<Period>> violations = jakartaValidator.validate(invalid);
        assertFalse(violations.isEmpty());

        Period valid = new Period();
        // start < end
        valid.setStart("2024-01-01T10:00:00Z");
        valid.setEnd("2024-12-31T10:00:00Z");

        assertTrue(jakartaValidator.validate(valid).isEmpty());
    }

    // Custom Validator range

    @Test
    void nonPeriodObjectIsValid() {
        assertTrue(validator.isValid("not a period", null));
    }

    @Test
    void bothNullOrMissingIsValid() {
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void missingStartIsValid() {
        period.setEnd("2024-12-31T10:00:00Z");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void missingEndIsValid() {
        period.setStart("2024-01-01T10:00:00Z");
        assertTrue(validator.isValid(period, null));
    }

    // Temporal Business Rules tests

    @Test
    void startLaterThanEndIsInvalid() {
        period.setStart("2024-12-31T10:00:00Z");
        period.setEnd("2024-01-01T10:00:00Z");
        assertFalse(validator.isValid(period, null));
    }

    @Test
    void startEqualToEndIsValid() {
        period.setStart("2024-06-15T12:30:00Z");
        period.setEnd("2024-06-15T12:30:00Z");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void startEarlierThanEndIsValid() {
        period.setStart("2024-01-01T10:00:00Z");
        period.setEnd("2024-12-31T10:00:00Z");
        assertTrue(validator.isValid(period, null));
    }

    // Flexible Date Parsing tests

    @Test
    void dateOnlyFormsAreSupported() {
        period.setStart("2024");
        period.setEnd("2024-12-31");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void dateOnlySameDayIsValid() {
        period.setStart("2024-06-15");
        period.setEnd("2024-06-15");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void differentTimezonesAreComparedAsInstants() {
        // 2024-01-01T17:00:00+07:00 == 2024-01-01T10:00:00Z (same instant)
        period.setStart("2024-01-01T17:00:00+07:00");
        period.setEnd("2024-01-01T10:00:00Z");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void startDifferentTimezoneStillComparedByInstant() {
        // start = 2024-01-02T00:00:00+07:00 == 2024-01-01T17:00:00Z
        period.setStart("2024-01-02T00:00:00+07:00");
        period.setEnd("2024-01-01T18:00:00Z");
        assertTrue(validator.isValid(period, null));
    }

    @Test
    void datetimeWithoutOffsetAssumedUtc() {
        period.setStart("2024-01-01T10:00:00");
        period.setEnd("2024-01-01T11:00:00");
        assertTrue(validator.isValid(period, null));
    }

}