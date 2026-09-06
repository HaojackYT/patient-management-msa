package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.model.datatype.ContactPoint;
import com.pm.patient_service.model.enums.ContactPointSystem;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ContactPointValidatorTest {

    private ContactPointValidator validator;
    private ContactPoint contactPoint;

    private static Validator jakartaValidator;

    private static final String CPT_2_MESSAGE = "Invalid ContactPoint: " +
            "A system is required if a value is provided.";

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ContactPointValidator();
        contactPoint = new ContactPoint();
    }

    @Test
    void constraintViolationReportedViaValidatorFactory() {

        // Without system
        ContactPoint invalid = new ContactPoint();
        invalid.setValue("0123456789");

        Set<ConstraintViolation<ContactPoint>> violations = jakartaValidator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertEquals(CPT_2_MESSAGE, violations.iterator().next().getMessage());

        ContactPoint valid = new ContactPoint();
        valid.setSystem(ContactPointSystem.phone);
        valid.setValue("0123456789");

        assertTrue(jakartaValidator.validate(valid).isEmpty());
    }

    // Custom Validator range

    @Test
    void nullObjectIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    // cpt-2 rule: "A system is required if a value is provided."

    @Test
    void valueWithoutSystemIsInvalid() {
        contactPoint.setValue("john.doe@example.org");
        assertFalse(validator.isValid(contactPoint, null));
    }

    @Test
    void valueWithSystemIsValid() {
        contactPoint.setSystem(ContactPointSystem.email);
        contactPoint.setValue("john.doe@example.org");
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void systemWithoutValueIsValid() { // when value is absent
        contactPoint.setSystem(ContactPointSystem.email);
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void bothSystemAndValueMissingIsValid() {
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void blankValueIsTreatedAsAbsent() {
        contactPoint.setSystem(ContactPointSystem.sms);
        contactPoint.setValue("   ");
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void blankValueWithoutSystemIsStillValid() {
        contactPoint.setValue("   ");
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void emptyStringValueIsTreatedAsAbsent() {
        contactPoint.setValue("");
        assertTrue(validator.isValid(contactPoint, null));
    }

    @Test
    void valueOnlyWhitespaceDifferenceStillRequiresSystem() {
        contactPoint.setValue(" 0123456789 ");
        assertFalse(validator.isValid(contactPoint, null));
    }

}