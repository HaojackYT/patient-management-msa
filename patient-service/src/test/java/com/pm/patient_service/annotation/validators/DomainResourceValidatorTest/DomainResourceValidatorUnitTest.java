package com.pm.patient_service.annotation.validators.DomainResourceValidatorTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pm.patient_service.annotation.ValidateDomainResource;
import com.pm.patient_service.annotation.validators.DomainResourceValidator;
import com.pm.patient_service.model.datatype.Coding;
import com.pm.patient_service.model.datatype.Meta;
import com.pm.patient_service.model.datatype.resource.AbstractResource;
import com.pm.patient_service.model.datatype.resource.domainResource.AbstractDomainResource;

/**
 * Unit tests for {@link DomainResourceValidator}
 */
class DomainResourceValidatorUnitTest {

    static class TestDomainResource extends AbstractDomainResource {
    }

    static class TestResource extends AbstractResource {
    }

    private DomainResourceValidator validator;
    private TestDomainResource testDomainResource;

    @BeforeEach
    void setUp() {
        validator = new DomainResourceValidator();
        validator.initialize(AbstractDomainResource.class.getAnnotation(ValidateDomainResource.class));
        testDomainResource = new TestDomainResource();
    }

    // Helper methods

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

    // Unit tests

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
        List<AbstractResource> resources = new ArrayList<>();

        resources.add(new TestDomainResource());
        resources.add(new TestResource());
        resources.add(null);
        testDomainResource.setContained(resources);

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

        contained.setContained(List.of(new TestDomainResource())); // dom-2
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
        TestDomainResource domainResource = new TestDomainResource();

        domainResource.setMeta(meta("1", null));
        testDomainResource.setContained(List.of(domainResource));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourceWithMetaLastUpdatedIsInvalid() {
        TestDomainResource domainResource = new TestDomainResource();

        domainResource.setMeta(meta(null, Instant.parse("2026-08-15T00:00:00Z")));
        testDomainResource.setContained(List.of(domainResource));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedNonDomainResourceWithMetaVersionIdIsInvalid() {
        TestResource resource = new TestResource();

        resource.setMeta(meta("1", null));
        testDomainResource.setContained(List.of(resource));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    // dom-5: contained.meta.security.empty()

    @Test
    void containedResourceWithSecurityLabelIsInvalid() {
        TestDomainResource domainResource = new TestDomainResource();

        domainResource.setMeta(metaWithSecurityLabel());
        testDomainResource.setContained(List.of(domainResource));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedNonDomainResourceWithSecurityLabelIsInvalid() {
        TestResource resource = new TestResource();

        resource.setMeta(metaWithSecurityLabel());
        testDomainResource.setContained(List.of(resource));

        assertFalse(validator.isValid(testDomainResource, null));
    }

    @Test
    void containedResourceWithUnrestrictedMetaIsValid() {
        Meta meta = new Meta();

        meta.setSource("http://example.org/fhir");
        meta.setProfile(List.of("http://example.org/fhir/StructureDefinition/organization"));
        meta.setTag(new ArrayList<>());

        TestDomainResource contained = new TestDomainResource();

        contained.setMeta(meta);
        testDomainResource.setContained(List.of(contained));

        assertTrue(validator.isValid(testDomainResource, null));
    }
}
