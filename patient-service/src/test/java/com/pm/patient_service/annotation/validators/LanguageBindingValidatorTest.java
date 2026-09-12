package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.model.backboneElement.PatientCommunication;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.Coding;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LanguageBindingValidatorTest {

    private static final String BCP47 = "urn:ietf:bcp:47";

    private static final String BINDING_MESSAGE = "Invalid language binding: " +
            "SHALL consist of a BCP-47 language subtag with an optional region modifier";

    private LanguageBindingValidator validator;
    private CodeableConcept codeableConcept;
    private PatientCommunication patientCommunication;

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new LanguageBindingValidator();
        codeableConcept = new CodeableConcept();
        patientCommunication = new PatientCommunication();
    }

    private static CodeableConcept codeableConceptWithCoding(String system, String code) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(system);
        coding.setCode(code);
        codeableConcept.setCoding(List.of(coding));
        return codeableConcept;
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        // Script subtag excluded
        CodeableConcept invalid = codeableConceptWithCoding(BCP47, "en-US-Latn");
        patientCommunication.setLanguage(invalid);

        Set<ConstraintViolation<PatientCommunication>> violations = jakartaValidator
                .validate(patientCommunication);
        assertFalse(violations.isEmpty());
        assertEquals(BINDING_MESSAGE, violations.iterator().next().getMessage());

        CodeableConcept valid = codeableConceptWithCoding(BCP47, "en-US");
        patientCommunication.setLanguage(valid);
        assertTrue(jakartaValidator.validate(patientCommunication).isEmpty());
    }

    // TODO: ensure standard Bean Validation annotations operate independently
    @Test
    void notNullViolationStillReportedIndependently() {
        patientCommunication.setLanguage(null);

        Set<ConstraintViolation<PatientCommunication>> violations = jakartaValidator
                .validate(patientCommunication);
        assertFalse(violations.isEmpty());
        assertEquals("Patient communication language is mandatory",
                violations.iterator().next().getMessage());
    }

    // Custom Validator range

    @Test
    void nullObjectIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void nullCodingListIsInvalid() {
        codeableConcept.setCoding(null);
        assertFalse(validator.isValid(codeableConcept, null));
    }

    @Test
    void emptyCodingListIsInvalid() {
        codeableConcept.setCoding(List.of());
        assertFalse(validator.isValid(codeableConcept, null));
    }

    @Test
    void textOnlyConceptIsInvalid() {
        codeableConcept.setText("English");
        assertFalse(validator.isValid(codeableConcept, null));
    }

    // Valid BCP-47 forms

    @Test
    void twoLetterLanguageCodeIsValid() {
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "en"), null));
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "vi"), null));
    }

    @Test
    void threeLetterLanguageCodeIsValid() {
        // haw = Hawaiian
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "haw"), null));
    }

    @Test
    void languageWithTwoLetterRegionIsValid() {
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "en-US"), null));
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "vi-VN"), null));
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "fr-FR"), null));
    }

    @Test
    void languageWithThreeDigitRegionIsValid() {
        // UN M.49 numeric region, 419 = Latin America and the Caribbean
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "es-419"), null));
    }

    @Test
    void subtagsAreCaseInsensitive() {
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "EN-us"), null));
        assertTrue(validator.isValid(
                codeableConceptWithCoding(BCP47, "EN"), null));
    }

    // Invalid BCP-47 forms excluded by the Simple Language value set

    @Test
    void scriptSubtagIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "en-US-Latn"), null));
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "zh-Hans"), null));
    }

    @Test
    void variantSubtagIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "de-DE-1901"), null));
    }

    @Test
    void extLangSubtagIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "i-klingon"), null));
    }

    @Test
    void privateUseSubtagIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "x-pig-latin"), null));
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "en-US-x-twain"), null));
    }

    // Malformed codes

    @Test
    void LanguageSubtagLengthIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "abcd"), null));
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "a"), null));
    }

    @Test
    void RegionSubtagLengthIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "en-USA"), null));
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, "en-4191"), null));
    }

    @Test
    void emptyCodeIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, ""), null));
    }

    @Test
    void whitespaceCodeIsInvalid() {
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, " en-US "), null));
        assertFalse(validator.isValid(
                codeableConceptWithCoding(BCP47, " "), null));
    }

    @Test
    void nullCodeInBcp47CodingIsInvalid() {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(BCP47);
        coding.setCode(null);
        codeableConcept.setCoding(List.of(coding));

        assertFalse(validator.isValid(codeableConcept, null));
    }

    // Alternate code systems (extensible binding semantics)

    @Test
    void codingWithNonBcp47SystemIsInvalid() {
        assertFalse(validator.isValid(codeableConceptWithCoding(
                "http://example.org/languages", "ENGLISH"), null));
    }

    @Test
    void additionalNonBcp47CodingIsToleratedWhenBcp47CodingPresent() {
        CodeableConcept codeableConcept = new CodeableConcept();

        Coding bcp47 = new Coding();
        bcp47.setSystem(BCP47);
        bcp47.setCode("vi-VN");

        Coding local = new Coding();
        local.setSystem("http://example.org/languages");
        local.setCode("ENGLISH");

        codeableConcept.setCoding(List.of(local, bcp47));

        assertTrue(validator.isValid(codeableConcept, null));
    }

    @Test
    void invalidBcp47CodingCannotBeOverriddenByLocalCoding() {
        CodeableConcept codeableConcept = new CodeableConcept();

        Coding bcp47 = new Coding();
        bcp47.setSystem(BCP47);
        bcp47.setCode("en-US-Latn"); // excluded by value set

        Coding local = new Coding();
        local.setSystem("http://example.org/languages");
        local.setCode("ENGLISH");

        codeableConcept.setCoding(List.of(bcp47, local));

        assertFalse(validator.isValid(codeableConcept, null));
    }

}
