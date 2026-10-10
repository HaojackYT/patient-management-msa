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

/**
 * FHIR R4 {@code PractitionerRole} — a specific set of
 * Roles/Locations/specialties/services that a practitioner may perform at an
 * organization for a period of time.
 *
 * <p>
 * PractitionerRole covers the recording of the location and types of services
 * that Practitioners are able to provide for an organization. Where
 * availability, telecom, or other details are not the same across all
 * healthcare services or locations, a separate PractitionerRole instance should
 * be created.
 * </p>
 *
 * <p>
 * Elements:
 * </p>
 *
 * <ul>
 * <li>{@code identifier} [0..*] {@code Identifier} — business identifiers that
 * are specific to a role/location</li>
 * <li>{@code active} [0..1] {@code boolean} — whether this practitioner role
 * record is in active use</li>
 * <li>{@code period} [0..1] {@code Period} — the period during which the person
 * is authorized to act as a practitioner in these role(s)</li>
 * <li>{@code practitioner} [0..1] {@code Reference(Practitioner)} —
 * practitioner that is able to provide the defined services for the
 * organization</li>
 * <li>{@code organization} [0..1] {@code Reference(Organization)} — the
 * organization where the Practitioner performs the roles associated</li>
 * <li>{@code code} [0..*] {@code CodeableConcept} — roles which this
 * practitioner is authorized to perform for the organization (example
 * binding)</li>
 * <li>{@code specialty} [0..*] {@code CodeableConcept} — specific specialty of
 * the practitioner (preferred binding, Practice Setting Code Value Set)</li>
 * <li>{@code location} [0..*] {@code Reference(Location)} — the location(s) at
 * which this practitioner provides care</li>
 * <li>{@code healthcareService} [0..*] {@code Reference(HealthcareService)} —
 * the list of healthcare services that this worker provides for this role's
 * Organization/Location(s)</li>
 * <li>{@code telecom} [0..*] {@code ContactPoint} — contact details that are
 * specific to the role/location/service</li>
 * <li>{@code availableTime} [0..*] {@link PractitionerRoleAvailableTime} — a
 * collection of times the practitioner is available at the location and/or
 * healthcareservice</li>
 * <li>{@code notAvailable} [0..*] {@link PractitionerRoleNotAvailable} — the
 * practitioner is not available during this period of time due to the provided
 * reason</li>
 * <li>{@code availabilityExceptions} [0..1] {@code string} — a description of
 * site availability exceptions, e.g. public holiday availability</li>
 * <li>{@code endpoint} [0..*] {@code Reference(Endpoint)} — technical endpoints
 * providing access to services operated for the practitioner with this
 * role</li>
 * </ul>
 *
 * <p>
 * Note: R4 defines no {@code PractitionerRole} specific invariant (only the
 * generic {@code ele-1} rule that every element must have a value or children),
 * so no class level constraint is declared on this resource.
 * </p>
 *
 * <p>
 * Note: there is no address on the PractitionerRole as the location that is
 * defined here contains the address.
 * </p>
 *
 * @see <a href=
 *      "https://hl7.org/fhir/R4/practitionerrole.html">FHIR R4
 *      PractitionerRole</a>
 * @see <a href="https://hl7.org/fhir/R4/practitioner.html">FHIR R4
 *      Practitioner</a>
 */
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
