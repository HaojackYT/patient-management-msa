package com.pm.patient_service.model.resource;

import com.pm.patient_service.annotation.ValidateFHIRId;
import com.pm.patient_service.model.datatype.Meta;

import jakarta.validation.Valid;

public abstract class AbstractResource {

    @ValidateFHIRId
    private String id;

    @Valid
    private Meta meta;

    private String implicitRules; // uri

    private String language; // code

    public String getId() {
        return id;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getImplicitRules() {
        return implicitRules;
    }

    public void setImplicitRules(String implicitRules) {
        this.implicitRules = implicitRules;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
