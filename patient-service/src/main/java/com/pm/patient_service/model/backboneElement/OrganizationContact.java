package com.pm.patient_service.model.backboneElement;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.model.datatype.Address;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.ContactPoint;
import com.pm.patient_service.model.datatype.HumanName;

import jakarta.validation.Valid;

public class OrganizationContact extends AbstractBackboneElement {

    @Valid
    private CodeableConcept purpose;

    @Valid
    private HumanName name;

    @Valid
    private List<ContactPoint> telecom = new ArrayList<>();

    @Valid
    private Address address;

    public CodeableConcept getPurpose() {
        return purpose;
    }

    public void setPurpose(CodeableConcept purpose) {
        this.purpose = purpose;
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

}
