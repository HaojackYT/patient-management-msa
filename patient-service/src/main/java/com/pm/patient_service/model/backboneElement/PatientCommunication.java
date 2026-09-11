package com.pm.patient_service.model.backboneElement;

import com.pm.patient_service.annotation.ValidateFHIRLanguageBinding;
import com.pm.patient_service.model.datatype.BackboneElement;
import com.pm.patient_service.model.datatype.CodeableConcept;

import jakarta.validation.constraints.NotNull;

public class PatientCommunication extends BackboneElement {

    @NotNull(message = "Patient communication language is mandatory")
    @ValidateFHIRLanguageBinding
    private CodeableConcept language;

    private boolean preferred;

    public CodeableConcept getLanguage() {
        return language;
    }

    public void setLanguage(CodeableConcept language) {
        this.language = language;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

}
