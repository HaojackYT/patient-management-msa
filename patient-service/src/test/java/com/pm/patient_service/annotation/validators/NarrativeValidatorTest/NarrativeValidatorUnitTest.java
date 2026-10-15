package com.pm.patient_service.annotation.validators.NarrativeValidatorTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.validators.NarrativeValidator;
import com.pm.patient_service.model.datatype.Narrative;
import com.pm.patient_service.model.enums.NarrativeStatus;

/**
 * Unit tests for {@link NarrativeValidator}
 */
class NarrativeValidatorUnitTest {

    private final NarrativeValidator validator = new NarrativeValidator();

    // Helper methods

    private static Narrative narrative(String div) {
        Narrative narrative = new Narrative();
        narrative.setStatus(NarrativeStatus.generated);
        narrative.setDiv(div);
        return narrative;
    }

    // Unit tests

    @Test
    void nullNarrativeIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void nullDivIsTreatedAsAbsent() {
        assertTrue(validator.isValid(narrative(null), null));
    }

    @Test
    void divWithTextIsValid() {
        assertTrue(validator.isValid(narrative(
                "<div xmlns=\"http://www.w3.org/1999/xhtml\">Patient is 45 years old</div>"),
                null));
    }

    @Test
    void divWithChildElementsIsValid() {
        assertTrue(validator.isValid(narrative(
                "<div><p>Holiday closure</p></div>"), null));
        assertTrue(validator.isValid(narrative(
                "<div><img src=\"x.png\" alt=\"x\"/></div>"), null));
    }

    @Test
    void divWithStyleAttributeIsValid() {
        assertTrue(validator.isValid(narrative(
                "<div><span style=\"font-weight:bold\">x</span></div>"), null));
    }

    @Test
    void blankDivIsInvalid() {
        assertFalse(validator.isValid(narrative("   "), null));
    }

    @Test
    void emptyDivIsInvalid() {
        assertFalse(validator.isValid(narrative("<div></div>"), null));
    }

    @Test
    void whitespaceOnlyDivIsInvalid() {
        assertFalse(validator.isValid(narrative("<div> \n </div>"), null));
    }

    @Test
    void valueWithoutDivElementIsInvalid() {
        assertFalse(validator.isValid(narrative("Patient is 45 years old"), null));
    }

    @Test
    void divWithScriptElementIsInvalid() {
        assertFalse(validator.isValid(narrative(
                "<div><script>alert(1)</script></div>"), null));
    }

    @Test
    void divWithStyleElementIsInvalid() {
        assertFalse(validator.isValid(narrative(
                "<div><style>p{color:red}</style></div>"), null));
    }

    @Test
    void divWithEmbeddedContentIsInvalid() {
        assertFalse(validator.isValid(narrative(
                "<div><iframe src=\"x.html\"></iframe></div>"), null));
        assertFalse(validator.isValid(narrative(
                "<div><object data=\"x.swf\"></object></div>"), null));
    }

    @Test // form controls: input, button, select, textarea
    void divWithFormControlIsInvalid() {
        assertFalse(validator.isValid(narrative(
                "<div><input type=\"text\"/></div>"), null));
    }

    @Test
    void divWithEventHandlerAttributeIsInvalid() {
        assertFalse(validator.isValid(narrative(
                "<div onclick=\"doIt()\">x</div>"), null));
    }
}
