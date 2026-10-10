package com.pm.patient_service.annotation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateDomainResource;
import com.pm.patient_service.model.datatype.Coding;
import com.pm.patient_service.model.datatype.Meta;
import com.pm.patient_service.model.datatype.resource.AbstractResource;
import com.pm.patient_service.model.datatype.resource.domainResource.AbstractDomainResource;
import com.pm.patient_service.model.datatype.resource.domainResource.Organization;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class DomainResourceValidatorTest {

    static class TestDomainResource extends AbstractDomainResource {
    }

    static class TestResource extends AbstractResource {
    }

    private static final String MESSAGE = "Invalid DomainResource: " +
            "a contained resource SHALL NOT contain nested Resources (dom-2), " +
            "SHALL NOT have a meta.versionId or a meta.lastUpdated (dom-4) " +
            "and SHALL NOT have a security label (dom-5)";

    private static Validator jakartaValidator;

    private DomainResourceValidator validator;

    private TestDomainResource testDomainResource;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        validator = new DomainResourceValidator();
        validator.initialize(AbstractDomainResource.class.getAnnotation(
                ValidateDomainResource.class));
        testDomainResource = new TestDomainResource();
    }

    // Helper methods

    private static Organization organization(String name) {
        Organization organization = new Organization();
        organization.setName(name);
        return organization;
    }

    private static Meta meta(String versionId, Instant lastUpdated) {
        Meta meta = new Meta();
        meta.setVersionId(versionId);
        meta.setLastUpdated(lastUpdated);
        return meta;
    }

    private static Meta metaWithSecurityLabel() {
        Meta meta = new Meta();
        meta.setSecurity(List.of(securityLabel()));
        return meta;
    }

    private static Coding securityLabel() {
        Coding security = new Coding();
        security.setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality");
        security.setCode("R");
        security.setDisplay("restricted");
        return security;
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        Organization contained = organization("General Hospital");
        contained.setMeta(meta("1", null));
        testDomainResource.setContained(List.of(contained));

        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                testDomainResource);
        assertFalse(violations.isEmpty());
        assertEquals(MESSAGE +
                " (contained[0]: dom-4 contained resource has meta.versionId/meta.lastUpdated)",
                violations.iterator().next().getMessage());
    }

    @Test // class level constraint is inherited by every domain resource
    void domainResourceInheritsTheConstraint() {
        Organization organization = organization("General Hospital");

        TestDomainResource contained = new TestDomainResource();
        contained.setContained(List.of(new TestDomainResource())); // dom-2
        organization.setContained(List.of(contained));

        Set<ConstraintViolation<Organization>> violations = jakartaValidator.validate(organization);
        assertFalse(violations.isEmpty());
        assertEquals(MESSAGE + " (contained[0]: dom-2 nested contained resources)",
                violations.iterator().next().getMessage());
    }

    @Test
    void constraintIsNotApplicableToNonDomainResource() {
        TestResource resource = new TestResource();
        resource.setMeta(meta("1", Instant.parse("2026-08-15T00:00:00Z")));

        assertTrue(jakartaValidator.validate(resource).isEmpty());
    }

    // Custom Validator range

    @Test
    void nullResourceIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void nullContainedResourceIsValid() {
        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test
    void emptyContainedListIsValid() {
        testDomainResource.setContained(new ArrayList<>());
        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test
    void nullContainedListIsValid() {
        testDomainResource.setContained(null);
        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test
    void nullContainedElementIsTreatedAsAbsent() {
        List<AbstractResource> contained = new ArrayList<>();
        contained.add(new TestDomainResource());
        contained.add(new TestResource());
        contained.add(null);
        testDomainResource.setContained(contained);

        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourcesWithoutMetaAreValid() {
        testDomainResource.setContained(List.of(new TestDomainResource(), new TestResource()));
        assertTrue(validator.isValid(testDomainResource, null));
    }

    // dom-2: contained.contained.empty()

    @Test
    void containedResourceWithNestedResourcesIsInvalid() {
        TestDomainResource contained = new TestDomainResource();
        contained.setContained(List.of(new TestDomainResource()));
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourceWithEmptyNestedListIsValid() {
        TestDomainResource contained = new TestDomainResource();
        contained.setContained(new ArrayList<>());
        testDomainResource.setContained(List.of(contained));

        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test
    void dom2IsNotApplicableToContainedResourceWithoutContainedElement() {
        // Bundle, Parameters and Binary have no contained element
        testDomainResource.setContained(List.of(new TestResource()));

        assertTrue(validator.isValid(testDomainResource, null));
    }

    // dom-4: contained.meta.versionId.empty() and
    // contained.meta.lastUpdated.empty()

    @Test
    void containedResourceWithMetaVersionIdIsInvalid() {
        TestDomainResource contained = new TestDomainResource();
        contained.setMeta(meta("1", null));
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourceWithMetaLastUpdatedIsInvalid() {
        TestDomainResource contained = new TestDomainResource();
        contained.setMeta(meta(null, Instant.parse("2026-08-15T00:00:00Z")));
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test // dom-4 applies to every contained resource
    void containedNonDomainResourceWithMetaVersionIdIsInvalid() {
        TestResource contained = new TestResource();
        contained.setMeta(meta("1", null));
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    // dom-5: contained.meta.security.empty()

    @Test
    void containedResourceWithSecurityLabelIsInvalid() {
        TestDomainResource contained = new TestDomainResource();
        contained.setMeta(metaWithSecurityLabel());
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedNonDomainResourceWithSecurityLabelIsInvalid() {
        TestResource contained = new TestResource();
        contained.setMeta(metaWithSecurityLabel());
        testDomainResource.setContained(List.of(contained));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourceWithUnrestrictedMetaIsValid() {
        TestDomainResource contained = new TestDomainResource();
        Meta meta = new Meta();
        meta.setSource("http://example.org/fhir");
        meta.setProfile(List.of("http://example.org/fhir/StructureDefinition/organization"));
        meta.setTag(new ArrayList<>());
        contained.setMeta(meta);
        testDomainResource.setContained(List.of(contained));

        assertTrue(validator.isValid(testDomainResource, null));
    }

    @Test // one violation per violated constraint
    void severalViolationsAreReportedForTheSameContainedResource() {
        TestDomainResource contained = new TestDomainResource();
        contained.setContained(List.of(new TestDomainResource())); // dom-2

        Meta meta = new Meta();
        meta.setVersionId("1"); // dom-4
        meta.setSecurity(List.of(securityLabel())); // dom-5
        contained.setMeta(meta);

        testDomainResource.setContained(List.of(contained));
        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                testDomainResource);
        assertEquals(3, violations.size());
    }

    @Test // violation message contains the index of the contained resource
    void violationMessageContainsIndexOfTheContainedResource() {
        TestDomainResource valid = new TestDomainResource();
        TestDomainResource invalid = new TestDomainResource();

        invalid.setContained(List.of(new TestDomainResource())); // dom-2
        testDomainResource.setContained(List.of(valid, invalid));

        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                testDomainResource);
        assertEquals(MESSAGE + " (contained[1]: dom-2 nested contained resources)",
                violations.iterator().next().getMessage());
    }

    @Test
    void validContainedResourceViaValidatorFactory() {
        testDomainResource.setContained(List.of(organization("General Hospital")));
        assertTrue(jakartaValidator.validate(testDomainResource).isEmpty());
    }

}