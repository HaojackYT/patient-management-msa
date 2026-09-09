package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public abstract class BackboneElement {

    @Valid
    private List<Extension> modifierExtension = new ArrayList<>();

    public List<Extension> getModifierExtension() {
        return modifierExtension;
    }

    public void setModifierExtension(List<Extension> modifierExtension) {
        this.modifierExtension = modifierExtension;
    }

}
