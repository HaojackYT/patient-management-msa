package com.pm.patient_service.model.datatype;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public class Reference {

    private String reference;

    @Pattern(regexp = "\\S*", message = "Reference type must be a valid URI")
    private String type; // uri

    @Valid
    private Identifier identifier;

    private String display;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

}