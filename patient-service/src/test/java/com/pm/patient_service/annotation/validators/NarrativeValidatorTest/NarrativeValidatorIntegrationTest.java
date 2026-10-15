package com.pm.patient_service.annotation.validators.NarrativeValidatorTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.validators.NarrativeValidator;
import com.pm.patient_service.model.datatype.Narrative;
import com.pm.patient_service.model.enums.NarrativeStatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Integration tests for {@link NarrativeValidator}
 */
class NarrativeValidatorIntegrationTest {

    private static final String TXT_1_MESSAGE = "" +
            "The narrative SHALL contain only the basic html formatting elements and attributes";
    private static final String TXT_2_MESSAGE = "" +
            "The narrative SHALL have some non-whitespace content";
    private static final String XHTML_MESSAGE = "" +
            "The narrative div SHALL be a single XHTML 'div' element";

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    // Helper methods

    private static Narrative narrative(String div) {
        Narrative narrative = new Narrative();
        narrative.setStatus(NarrativeStatus.generated);
        narrative.setDiv(div);
        return narrative;
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        Narrative narrative = narrative("<div><script/></div>");
        Set<ConstraintViolation<Narrative>> violations = jakartaValidator.validate(narrative);
        assertFalse(violations.isEmpty());
        assertEquals(TXT_1_MESSAGE, violations.iterator().next().getMessage());

        narrative.setDiv("<div>  </div>");
        assertEquals(TXT_2_MESSAGE, jakartaValidator.validate(narrative).iterator().next().getMessage());
        narrative.setDiv("no div element");
        assertEquals(XHTML_MESSAGE, jakartaValidator.validate(narrative).iterator().next().getMessage());

        narrative.setDiv("<div>Patient is 45 years old</div>");
        assertTrue(jakartaValidator.validate(narrative).isEmpty());
    }

    @Test
    void mandatoryElementsReportedViaValidatorFactory() {
        Narrative narrative = new Narrative();
        Set<ConstraintViolation<Narrative>> violations = jakartaValidator.validate(narrative);
        assertEquals(2, violations.size());
    }
}
