package com.pm.patient_service.model.datatype;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class Reference {

    @Column(name = "reference_reference")
    private String reference;

    @Pattern(regexp = "\\S*", message = "Reference type must be a valid URI")
    @Column(name = "reference_type")
    private String type; // uri

    @Valid
    @Embedded
    private Identifier identifier;

    @Column(name = "reference_display")
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