package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateFHIRAttachment;
import com.pm.patient_service.model.datatype.Attachment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AttachmentValidatorTest {

    private AttachmentValidator validator;
    private Attachment attachment;

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new AttachmentValidator();
        attachment = new Attachment();
    }

    @Test // integration between @ValidateFHIRAttachment and Jakarta Bean Validation
          // Factory
    void constraintViolationReportedViaValidatorFactory() {

        Attachment invalid = new Attachment();
        // Without contentType
        invalid.setData("patient photo".getBytes());

        Set<ConstraintViolation<Attachment>> violations = jakartaValidator.validate(invalid);
        assertFalse(violations.isEmpty());

        // Empty property path -> class-level constraint violation
        ConstraintViolation<Attachment> violation = violations.iterator().next();
        assertEquals("", violation.getPropertyPath().toString());
        assertEquals("If the Attachment has data, it SHALL have a contentType",
                violation.getMessage());
        assertTrue(violation.getConstraintDescriptor().getAnnotation() instanceof ValidateFHIRAttachment);

        Attachment valid = new Attachment();
        valid.setContentType("image/png");
        valid.setLanguage("vi-VN");
        valid.setData("patient photo".getBytes());

        assertTrue(jakartaValidator.validate(valid).isEmpty());
    }

    // Custom Validator range

    @Test
    void nullObjectIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    // data

    @Test
    void nullDataWithoutContentTypeIsValid() {
        assertTrue(validator.isValid(attachment, null));
    }

    @Test
    void emptyDataWithoutContentTypeIsValid() {
        attachment.setData(new byte[0]);
        assertTrue(validator.isValid(attachment, null));
    }

    @Test
    void dataWithoutContentTypeIsInvalid() {
        attachment.setData("patient photo".getBytes());
        assertFalse(validator.isValid(attachment, null));
    }

    @Test
    void dataWithValidContentTypeIsValid() {
        attachment.setData("patient photo".getBytes());
        attachment.setContentType("image/png");
        assertTrue(validator.isValid(attachment, null));
    }

    // contentType

    @Test
    void contentTypeWithoutDataIsStillValidated() {
        attachment.setContentType("not a mime type");
        assertTrue(hasViolationOnPath("contentType"));
    }

    @Test
    void invalidMimeTypesAreRejected() {
        attachment.setData("patient photo".getBytes());
        for (String bad : new String[] { "png", "image/", "/png", "image /png", "a/b/c", "image/png " }) {
            attachment.setContentType(bad);
            assertTrue(hasViolationOnPath("contentType"), "Expected rejection: " + bad);
        }
    }

    @Test
    void validMimeTypesAreAccepted() {
        attachment.setData("patient photo".getBytes());
        for (String good : new String[] { "application/pdf", "image/png", "image/jpeg", "text/plain" }) {
            attachment.setContentType(good);
            assertFalse(hasAnyViolation(), "Expected acceptance: " + good);
        }
    }

    // language: BCP-47 (@Pattern, field-level)

    @Test
    void languageWithoutDataOrContentTypeIsValid() {
        attachment.setLanguage("vi-VN");
        assertFalse(hasAnyViolation());
    }

    @Test
    void invalidLanguagesAreRejected() {
        for (String bad : new String[] { "e", "en_US", "123", "-vi", "en us", "vi-VN-" }) {
            attachment.setLanguage(bad);
            assertTrue(hasViolationOnPath("language"), "Expected rejection: " + bad);
        }
    }

    @Test
    void validLanguagesAreAccepted() {
        for (String good : new String[] { "en", "vi", "vi-VN", "zh-Hans-CN", "es-419", "de" }) {
            attachment.setLanguage(good);
            assertFalse(hasAnyViolation(), "Expected acceptance: " + good);
        }
    }

    // Full-constraint integration

    @Test // all field-level constraints + class-level att-1 together
    void fullyPopulatedValidAttachmentHasNoViolations() {
        attachment.setContentType("application/pdf");
        attachment.setLanguage("en-US");
        attachment.setData("patient photo".getBytes());
        attachment.setUrl("http://example.org/patient-photo.png");
        attachment.setSize("patient photo".getBytes().length);
        attachment.setHash("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
        attachment.setTitle("Patient photo");
        attachment.setCreation("2024-05-17T14:30:00Z");

        assertFalse(hasAnyViolation());
    }

    @Test // a field-level violation and the class-level att-1 violation coexist
    void fieldViolationAndAtt1ViolationAreBothReported() {
        attachment.setData("patient photo".getBytes());
        attachment.setLanguage("en_US"); // invalid BCP-47

        Set<ConstraintViolation<Attachment>> violations = validate(attachment);
        assertTrue(violations.stream().anyMatch(v -> "language".equals(v.getPropertyPath().toString())));
        assertTrue(violations.stream().anyMatch(
                v -> v.getConstraintDescriptor().getAnnotation() instanceof ValidateFHIRAttachment));
    }

    // Helper methods

    private Set<ConstraintViolation<Attachment>> validate(Attachment targetObject) {
        return jakartaValidator.validate(targetObject);
    }

    private boolean hasAnyViolation() {
        return !validate(attachment).isEmpty();
    }

    private boolean hasViolationOnPath(String path) {
        return validate(attachment).stream().anyMatch(v -> path.equals(v.getPropertyPath().toString()));
    }

}