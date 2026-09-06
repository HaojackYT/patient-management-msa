package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.model.enums.AddressType;
import com.pm.patient_service.model.enums.AddressUse;

import jakarta.validation.Valid;

public class Address {

    private AddressUse use;

    private AddressType type;

    private String text;

    private List<String> line = new ArrayList<>();

    private String city;

    private String district;

    private String state;

    private String postalCode;

    private String country;

    @Valid
    private Period period;

    public AddressUse getUse() {
        return use;
    }

    public void setUse(AddressUse use) {
        this.use = use;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getLine() {
        return line;
    }

    public void setLine(List<String> line) {
        this.line = line;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

}