package com.pm.patient_service.model.datatype.resource.domainResource;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateFHIRDate;
import com.pm.patient_service.model.datatype.Address;
import com.pm.patient_service.model.datatype.Attachment;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.ContactPoint;
import com.pm.patient_service.model.datatype.HumanName;
import com.pm.patient_service.model.datatype.Identifier;
import com.pm.patient_service.model.datatype.backboneElement.PractitionerQualification;
import com.pm.patient_service.model.enums.AdministrativeGender;

import jakarta.validation.Valid;

/**
 * FHIR R4 {@code Practitioner} — a person who is directly or indirectly
 * involved in the provisioning of healthcare.
 *
 * <p>
 * Practitioner covers all individuals who are engaged in the healthcare process
 * and healthcare-related services as part of their formal responsibilities and
 * this Resource is used for attribution of activities and responsibilities to
 * these individuals.
 * </p>
 *
 * <p>
 * The Resource SHALL NOT be used for persons involved without a formal
 * responsibility (e.g. individuals taking care of friends, relatives or
 * neighbors — these can be registered as a Patient's contact, or use
 * {@code RelatedPerson}). The roles that a practitioner is approved to perform
 * for which organizations (and at which locations) are represented by
 * {@link PractitionerRole}.
 * </p>
 *
 * <p>
 * Elements:
 * </p>
 *
 * <ul>
 * <li>{@code identifier} [0..*] {@code Identifier} — an identifier that applies
 * to this person in this role (business identifier, not resource id)</li>
 * <li>{@code active} [0..1] {@code boolean} — whether this practitioner's
 * record
 * is in active use (assumed active if missing)</li>
 * <li>{@code name} [0..*] {@code HumanName} — the name(s) associated with the
 * practitioner</li>
 * <li>{@code telecom} [0..*] {@code ContactPoint} — a contact detail for the
 * practitioner, e.g. a telephone number or an email address</li>
 * <li>{@code address} [0..*] {@code Address} — address(es) of the practitioner
 * that are not role specific (typically home address)</li>
 * <li>{@code gender} [0..1] {@code code} — administrative gender (required
 * binding to {@link AdministrativeGender})</li>
 * <li>{@code birthDate} [0..1] {@code date} — the date of birth for the
 * practitioner</li>
 * <li>{@code photo} [0..*] {@code Attachment} — image of the person</li>
 * <li>{@code qualification} [0..*] {@link PractitionerQualification} — the
 * official certifications, training, and licenses that authorize the provision
 * of care by the practitioner</li>
 * <li>{@code communication} [0..*] {@code CodeableConcept} — a language the
 * practitioner can use in patient communication (preferred binding, Common
 * Languages)</li>
 * </ul>
 *
 * <p>
 * Note: R4 defines no {@code Practitioner} specific invariant (only the generic
 * {@code ele-1} rule that every element must have a value or children), so no
 * class level constraint is declared on this resource.
 * </p>
 *
 * <p>
 * Limitation: the preferred binding of {@code communication} (Common Languages)
 * is not enforced; the syntactic BCP-47 check of
 * {@code @ValidateFHIRLanguageBinding} targets a single {@code CodeableConcept}
 * element while this element is a list.
 * </p>
 *
 * @see <a href="https://hl7.org/fhir/R4/practitioner.html">FHIR R4
 *      Practitioner</a>
 * @see <a href=
 *      "https://hl7.org/fhir/R4/practitionerrole.html">FHIR R4
 *      PractitionerRole</a>
 */
public class Practitioner extends AbstractDomainResource {

    @Valid
    private List<Identifier> identifier = new ArrayList<>();

    private Boolean active;

    @Valid
    private List<HumanName> name = new ArrayList<>();

    @Valid
    private List<ContactPoint> telecom = new ArrayList<>();

    @Valid
    private List<Address> address = new ArrayList<>();

    private AdministrativeGender gender;

    @ValidateFHIRDate(message = "Practitioner birthDate format must be valid")
    private String birthDate;

    @Valid
    private List<Attachment> photo = new ArrayList<>();

    @Valid
    private List<PractitionerQualification> qualification = new ArrayList<>();

    @Valid
    private List<CodeableConcept> communication = new ArrayList<>();

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

    public List<HumanName> getName() {
        return name;
    }

    public void setName(List<HumanName> name) {
        this.name = name;
    }

    public List<ContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<ContactPoint> telecom) {
        this.telecom = telecom;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public AdministrativeGender getGender() {
        return gender;
    }

    public void setGender(AdministrativeGender gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Attachment> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Attachment> photo) {
        this.photo = photo;
    }

    public List<PractitionerQualification> getQualification() {
        return qualification;
    }

    public void setQualification(List<PractitionerQualification> qualification) {
        this.qualification = qualification;
    }

    public List<CodeableConcept> getCommunication() {
        return communication;
    }

    public void setCommunication(List<CodeableConcept> communication) {
        this.communication = communication;
    }

}
