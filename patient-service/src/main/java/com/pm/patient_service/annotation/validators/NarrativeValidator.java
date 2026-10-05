package com.pm.patient_service.annotation.validators;

import java.util.regex.Pattern;

import com.pm.patient_service.annotation.ValidateNarrative;
import com.pm.patient_service.model.datatype.Narrative;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidateNarrative}.
 *
 * <p>
 * Executes the FHIR R4 constraints of the {@code Narrative} datatype:
 * <ul>
 * <li><b>txt-1</b> — "The narrative SHALL contain only the basic html
 * formatting elements and attributes described in chapters 7-11 (except section
 * 4 of chapter 9) and 15 of the HTML 4.0 standard, {@code <a>} elements (either
 * name or href), images and internally contained style attributes"</li>
 * <li><b>txt-2</b> — "The narrative SHALL have some non-whitespace
 * content"</li>
 * </ul>
 * </p>
 *
 * <p>
 * In addition, the {@code xhtml} type requirement is verified: {@code div} must
 * be a (single) XHTML {@code div} element containing the narrative content.
 * </p>
 *
 * <p>
 * Note:
 * <ul>
 * <li>A {@code null} {@code div} is treated as absent.</li>
 * <li>Only the outer {@code div} element is taken into account to determine
 * whether the narrative carries non-whitespace content.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Limitations: txt-1 is enforced with a conservative blacklist of non-permitted
 * markup elements ({@code script}, {@code style}, {@code iframe}, form
 * controls) and {@code on*} event handler attributes, not with a full HTML 4.0
 * whitelist.
 * </p>
 */
// TODO: validator XHTML check ({@code htmlChecks()})
public class NarrativeValidator implements ConstraintValidator<ValidateNarrative, Narrative> {

    private static final String TXT_1_MESSAGE = "" +
            "The narrative SHALL contain only the basic html formatting elements and attributes";

    private static final String TXT_2_MESSAGE = "" +
            "The narrative SHALL have some non-whitespace content";

    private static final String XHTML_MESSAGE = "" +
            "The narrative div SHALL be a single XHTML 'div' element";

    // Non-permitted markup of the basic html formatting subset
    // TODO: (non-exhaustive)
    private static final Pattern DISALLOWED_ELEMENTS = Pattern.compile(
            "(?i)<\\s*/?\\s*"
                    + "(script|style|iframe|frame|frameset|object|embed|applet"
                    + "|form|input|button|select|textarea"
                    + "|html|head|body|base|link|meta)\\b");

    // Event handler attributes (onclick, onload, ...)
    private static final Pattern EVENT_HANDLER_ATTRIBUTES = Pattern.compile(
            "(?i)\\son[a-z]+\\s*=");

    @Override
    public boolean isValid(Narrative value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        String div = value.getDiv();
        if (div == null) {
            return true;
        }

        String xhtml = div.trim();
        // txt-2: the narrative SHALL have some non-whitespace content
        if (xhtml.isEmpty()) {
            return violation(context, TXT_2_MESSAGE);
        }

        // xhtml type: div must be a single XHTML div element
        if (!isDivElement(xhtml)) {
            return violation(context, XHTML_MESSAGE);
        }

        // txt-2: content of the outer div element
        if (!hasNonWhitespaceContent(xhtml)) {
            return violation(context, TXT_2_MESSAGE);
        }

        // txt-1: non-permitted markup
        if (containsDisallowedMarkup(xhtml)) {
            return violation(context, TXT_1_MESSAGE);
        }

        return true;
    }

    /**
     * Checks that the value starts with a {@code div} element ({@code <div},
     * {@code <div ...>} or {@code <div/>}) and ends with a closing bracket.
     */
    private boolean isDivElement(String xhtml) {

        if (!xhtml.regionMatches(true, 0, "<div", 0, 4) ||
                !xhtml.endsWith(">")) {

            return false;
        }

        if (xhtml.length() == 4) { // "<div" only
            return false;
        }

        char next = xhtml.charAt(4); // after "<div"
        return next == '>' || next == '/' || Character.isWhitespace(next);
    }

    /**
     * Checks whether the content between the outer {@code div} tags
     * contains some non-whitespace content (text or child elements).
     */
    private boolean hasNonWhitespaceContent(String xhtml) {

        int openTagEnd = xhtml.indexOf('>');
        int closeTagStart = xhtml.lastIndexOf('<');

        if (openTagEnd < 0 || closeTagStart <= openTagEnd) {
            return false;
        }

        // openTagEnd + 1: after the opening div tag
        // closeTagStart: before the closing div tag
        return !xhtml.substring(openTagEnd + 1, closeTagStart).trim().isEmpty();
    }

    private boolean containsDisallowedMarkup(String xhtml) {

        return DISALLOWED_ELEMENTS.matcher(xhtml).find() ||
                EVENT_HANDLER_ATTRIBUTES.matcher(xhtml).find();
    }

    private boolean violation(ConstraintValidatorContext context, String message) {

        if (context != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return false;
    }

}
