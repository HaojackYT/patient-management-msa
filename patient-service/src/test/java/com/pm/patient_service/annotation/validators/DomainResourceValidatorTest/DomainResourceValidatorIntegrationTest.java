package com.pm.patient_service.annotation.validators.DomainResourceValidatorTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.validators.DomainResourceValidator;
import com.pm.patient_service.model.datatype.Coding;
import com.pm.patient_service.model.datatype.Meta;
import com.pm.patient_service.model.datatype.resource.AbstractResource;
import com.pm.patient_service.model.datatype.resource.domainResource.AbstractDomainResource;
import com.pm.patient_service.model.datatype.resource.domainResource.Organization;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Integration tests for {@link DomainResourceValidator} via Jakarta Bean
 * Validation.
 */
class DomainResourceValidatorIntegrationTest {

    static class TestDomainResource extends AbstractDomainResource {
    }

    static class TestResource extends AbstractResource {
    }

    private static final String MESSAGE = "Invalid DomainResource: "
            + "a contained resource SHALL NOT contain nested Resources (dom-2), "
            + "SHALL NOT have a meta.versionId or a meta.lastUpdated (dom-4) "
            + "and SHALL NOT have a security label (dom-5)";

    private static Validator jakartaValidator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            jakartaValidator = factory.getValidator();
        }
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

    private static Coding securityLabel() {
        Coding coding = new Coding();
        coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality");
        coding.setCode("R");
        coding.setDisplay("restricted");
        return coding;
    }

    // Integration tests

    @Test
    void constraintViolationReportedViaValidatorFactory() {
        Organization contained = organization("General Hospital");
        contained.setMeta(meta("1", null));
        TestDomainResource domainResource = new TestDomainResource();
        domainResource.setContained(List.of(contained));

        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                domainResource);
        assertFalse(violations.isEmpty());
        assertEquals(MESSAGE +
                " (contained[0]: dom-4 contained resource has meta.versionId/meta.lastUpdated)",
                violations.iterator().next().getMessage());
    }

    @Test
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

    @Test
    void severalViolationsAreReportedForTheSameContainedResource() {
        TestDomainResource contained = new TestDomainResource();
        contained.setContained(List.of(new TestDomainResource()));

        Meta meta = new Meta();
        meta.setVersionId("1"); // dom-4
        meta.setSecurity(List.of(securityLabel())); // dom-5
        contained.setMeta(meta);

        TestDomainResource domainResource = new TestDomainResource();
        domainResource.setContained(List.of(contained));

        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                domainResource);
        assertEquals(3, violations.size());
    }

    @Test
    void violationMessageContainsIndexOfTheContainedResource() {
        TestDomainResource valid = new TestDomainResource();
        TestDomainResource invalid = new TestDomainResource();

        TestDomainResource domainResource = new TestDomainResource();
        invalid.setContained(List.of(new TestDomainResource())); // dom-2
        domainResource.setContained(List.of(valid, invalid));

        Set<ConstraintViolation<TestDomainResource>> violations = jakartaValidator.validate(
                domainResource);
        assertEquals(MESSAGE + " (contained[1]: dom-2 nested contained resources)",
                violations.iterator().next().getMessage());
    }

    @Test
    void validContainedResourceViaValidatorFactory() {
        TestDomainResource domainResource = new TestDomainResource();
        domainResource.setContained(List.of(organization("General Hospital")));
        assertTrue(jakartaValidator.validate(domainResource).isEmpty());
    }
}
