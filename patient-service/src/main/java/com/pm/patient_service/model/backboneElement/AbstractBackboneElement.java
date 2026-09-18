package com.pm.patient_service.model.backboneElement;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.model.datatype.Extension;

import jakarta.validation.Valid;

public abstract class AbstractBackboneElement {

    @Valid
    private List<Extension> modifierExtension = new ArrayList<>();

    public List<Extension> getModifierExtension() {
        return modifierExtension;
    }

    public void setModifierExtension(List<Extension> modifierExtension) {
        this.modifierExtension = modifierExtension;
    }

}
