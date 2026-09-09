package com.pm.patient_service.model.backboneElement;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.model.datatype.Address;
import com.pm.patient_service.model.datatype.BackboneElement;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.ContactPoint;
import com.pm.patient_service.model.datatype.HumanName;
import com.pm.patient_service.model.datatype.Period;
import com.pm.patient_service.model.datatype.Reference;
import com.pm.patient_service.model.enums.AdministrativeGender;

import jakarta.validation.Valid;

public class PatientContact extends BackboneElement {

    @Valid
    private List<CodeableConcept> relationship = new ArrayList<>();

    @Valid
    private HumanName name;

    @Valid
    private List<ContactPoint> telecom = new ArrayList<>();

    @Valid
    private Address address;

    private AdministrativeGender gender;

    @Valid
    private Reference organization;

    @Valid
    private Period period;

    public List<CodeableConcept> getRelationship() {
        return relationship;
    }

    public void setRelationship(List<CodeableConcept> relationship) {
        this.relationship = relationship;
    }

    public HumanName getName() {
        return name;
    }

    public void setName(HumanName name) {
        this.name = name;
    }

    public List<ContactPoint> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<ContactPoint> telecom) {
        this.telecom = telecom;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public AdministrativeGender getGender() {
        return gender;
    }

    public void setGender(AdministrativeGender gender) {
        this.gender = gender;
    }

    public Reference getOrganization() {
        return organization;
    }

    public void setOrganization(Reference organization) {
        this.organization = organization;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

}
