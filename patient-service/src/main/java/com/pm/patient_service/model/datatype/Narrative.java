package com.pm.patient_service.model.datatype;

import com.pm.patient_service.annotation.ValidateNarrative;
import com.pm.patient_service.model.enums.NarrativeStatus;

import jakarta.validation.constraints.NotNull;

@ValidateNarrative
public class Narrative {

    @NotNull(message = "Narrative status is mandatory")
    private NarrativeStatus status;

    @NotNull(message = "Narrative div is mandatory")
    private String div;

    public NarrativeStatus getStatus() {
        return status;
    }

    public void setStatus(NarrativeStatus status) {
        this.status = status;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

}
