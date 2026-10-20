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
