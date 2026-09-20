package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateOrganization;
import com.pm.patient_service.model.backboneElement.OrganizationContact;

import jakarta.validation.Valid;

// TODO: org-2, org-3
@ValidateOrganization
public class Organization {

    @Valid
    private List<Identifier> identifier = new ArrayList<>();

    private Boolean active;

    @Valid
    private List<CodeableConcept> type = new ArrayList<>();

    private String name;

    private List<String> alias = new ArrayList<>();

    @Valid
    private List<ContactPoint> telecom = new ArrayList<>();

    @Valid
    private List<Address> address = new ArrayList<>();

    @Valid
    private Reference partOf;

    @Valid
    private List<OrganizationContact> contact = new ArrayList<>();

    @Valid
    private List<Endpoint> endpoint = new ArrayList<>();

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

    public List<CodeableConcept> getType() {
        return type;
    }

    public void setType(List<CodeableConcept> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
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

    public Reference getPartOf() {
        return partOf;
    }

    public void setPartOf(Reference partOf) {
        this.partOf = partOf;
    }

    public List<OrganizationContact> getContact() {
        return contact;
    }

    public void setContact(List<OrganizationContact> contact) {
        this.contact = contact;
    }

    public List<Endpoint> getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(List<Endpoint> endpoint) {
        this.endpoint = endpoint;
    }

}
