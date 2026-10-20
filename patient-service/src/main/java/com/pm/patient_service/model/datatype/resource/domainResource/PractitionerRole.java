package com.pm.patient_service.model.datatype.resource.domainResource;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateFHIRReferenceTarget;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.ContactPoint;
import com.pm.patient_service.model.datatype.Identifier;
import com.pm.patient_service.model.datatype.Period;
import com.pm.patient_service.model.datatype.Reference;
import com.pm.patient_service.model.datatype.backboneElement.PractitionerRoleAvailableTime;
import com.pm.patient_service.model.datatype.backboneElement.PractitionerRoleNotAvailable;

import jakarta.validation.Valid;

public class PractitionerRole extends AbstractDomainResource {

    @Valid
    private List<Identifier> identifier = new ArrayList<>();

    private Boolean active;

    @Valid
    private Period period;

    @ValidateFHIRReferenceTarget({ "Practitioner" })
    @Valid
    private Reference practitioner;

    @ValidateFHIRReferenceTarget({ "Organization" })
    @Valid
    private Reference organization;

    @Valid
    private List<CodeableConcept> code = new ArrayList<>();

    @Valid
    private List<CodeableConcept> specialty = new ArrayList<>();

    @ValidateFHIRReferenceTarget({ "Location" })
    @Valid
    private List<Reference> location = new ArrayList<>();

    @ValidateFHIRReferenceTarget({ "HealthcareService" })
    @Valid
    private List<Reference> healthcareService = new ArrayList<>();

    @Valid
    private List<ContactPoint> telecom = new ArrayList<>();

    @Valid
    private List<PractitionerRoleAvailableTime> availableTime = new ArrayList<>();

    @Valid
    private List<PractitionerRoleNotAvailable> notAvailable = new ArrayList<>();

    private String availabilityExceptions;

    @ValidateFHIRReferenceTarget({ "Endpoint" })
    @Valid
    private List<Reference> endpoint = new ArrayList<>();

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Reference getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Reference practitioner) {
        this.practitioner = practitioner;
    }

    public Reference getOrganization() {
        return organization;
    }

    public void setOrganization(Reference organization) {
        this.organization = organization;
    }

    public List<CodeableConcept> getCode() {
        return code;
    }

    public void setCode(List<CodeableConcept> code) {
        this.code = code;
    }

    public List<CodeableConcept> getSpecialty() {
        return specialty;
    }

    public void setSpecialty(List<CodeableConcept> specialty) {
        this.specialty = specialty;
    }

    public List<Reference> getLocation() {
        return location;
    }

    public void setLocation(List<Reference> location) {
        this.location = location;
    }

    public List<Reference> getHealthcareService() {
        return healthcareService;
    }

    public void setHealthcareService(List<Reference> healthcareService) {
        this.healthcareService = healthcareService;
    }

    public List<ContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<ContactPoint> telecom) {
        this.telecom = telecom;
    }

    public List<PractitionerRoleAvailableTime> getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(List<PractitionerRoleAvailableTime> availableTime) {
        this.availableTime = availableTime;
    }

    public List<PractitionerRoleNotAvailable> getNotAvailable() {
        return notAvailable;
    }

    public void setNotAvailable(List<PractitionerRoleNotAvailable> notAvailable) {
        this.notAvailable = notAvailable;
    }

    public String getAvailabilityExceptions() {
        return availabilityExceptions;
    }

    public void setAvailabilityExceptions(String availabilityExceptions) {
        this.availabilityExceptions = availabilityExceptions;
    }

    public List<Reference> getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(List<Reference> endpoint) {
        this.endpoint = endpoint;
    }

}
