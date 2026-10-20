package com.pm.patient_service.model.datatype.backboneElement;

import com.pm.patient_service.model.datatype.Period;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class PractitionerRoleNotAvailable extends AbstractBackboneElement {

    @NotBlank(message = "PractitionerRole notAvailable description is mandatory")
    private String description;

    @Valid
    private Period during;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Period getDuring() {
        return during;
    }

    public void setDuring(Period during) {
        this.during = during;
    }

}
