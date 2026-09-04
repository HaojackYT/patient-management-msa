package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.model.datatype.Extension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ExtensionValidatorTest {

    private static final String URL = "https://patient-management.example/fhir/StructureDefinition/registration-date";

    private ExtensionValidator validator;
    private Extension extension;

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ExtensionValidator();
        extension = new Extension(URL);
    }

    // Integration tests

    @Test // integration between @ValidFHIRExtension and Jakarta Bean Validation Factory
    void constraintViolationReportedViaValidatorFactory() {
        // Having both value[x] and extension -> invalid
        extension.setValueString("2026-08-30");
        Extension child = new Extension("child-url");
        child.setValueCode("male");
        extension.setExtension(new ArrayList<>(List.of(child)));

        Set<ConstraintViolation<Extension>> violations = jakartaValidator.validate(extension);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("not both")));

        Extension valid = new Extension(URL);
        valid.setValueString("2026-08-30");
        assertTrue(jakartaValidator.validate(valid).isEmpty());
    }

    @Test
    void nestedChildIsCascadeValidated() {

        Extension validParent = new Extension(URL);
        validParent.setValueString("parent");

        Extension invalidChild = new Extension("child-url");
        invalidChild.setValueCode("male");

        Extension grandChild = new Extension("grandchild-url");
        grandChild.setValueBoolean(true);

        invalidChild.setExtension(new ArrayList<>(List.of(grandChild)));
        validParent.setExtension(new ArrayList<>(List.of(invalidChild)));

        Set<ConstraintViolation<Extension>> violations = jakartaValidator.validate(validParent);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("extension[0]")));
    }

    // Unit tests

    @Test
    void nullExtensionIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void valueOnlyIsValid() {
        extension.setValueString("2026-08-30");
        assertTrue(validator.isValid(extension, null));
    }

    @Test
    void nestedExtensionsOnlyIsValid() {
        Extension child = new Extension("child-url");
        child.setValueCode("male");
        extension.setExtension(new ArrayList<>(List.of(child)));
        assertTrue(validator.isValid(extension, null));
    }

    @Test
    void complexDynamicValueIsValid() {
        extension.putValue("valueCodeableConcept", Map.of("text", "Married"));
        assertTrue(validator.isValid(extension, null));
    }

    @Test // FHIR ext-1: "Must have either extensions or value[x], not both"
    void bothExtensionsAndValueIsInvalid() {
        extension.setValueDate("2026-08-30");
        Extension child = new Extension("child-url");
        child.setValueCode("male");
        extension.setExtension(new ArrayList<>(List.of(child)));
        assertFalse(validator.isValid(extension, null));
    }

    @Test
    void neitherExtensionsNorValueIsInvalid() {
        assertFalse(validator.isValid(extension, null));
    }

    @Test
    void multipleValueXIsInvalid() {
        extension.setValueString("text");
        extension.setValueCode("code");
        assertFalse(validator.isValid(extension, null));
    }

    @Test
    void missingUrlIsInvalid() {
        Extension noUrl = new Extension();
        noUrl.setValueBoolean(true);
        assertFalse(validator.isValid(noUrl, null));
    }

    @Test
    void blankUrlIsInvalid() {
        Extension blank = new Extension("   ");
        blank.setValueBoolean(true);
        assertFalse(validator.isValid(blank, null));
    }

}
