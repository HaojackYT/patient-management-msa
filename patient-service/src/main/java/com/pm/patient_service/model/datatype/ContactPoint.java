package com.pm.patient_service.model.datatype;

import com.pm.patient_service.annotation.ValidateContactPoint;
import com.pm.patient_service.model.enums.ContactPointSystem;
import com.pm.patient_service.model.enums.ContactPointUse;

import jakarta.validation.Valid;

@ValidateContactPoint
public class ContactPoint {

    private ContactPointSystem system;

    private String value;

    private ContactPointUse use;

    private Integer rank;

    @Valid
    private Period period;

    public ContactPointSystem getSystem() {
        return system;
    }

    public void setSystem(ContactPointSystem system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ContactPointUse getUse() {
        return use;
    }

    public void setUse(ContactPointUse use) {
        this.use = use;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

}