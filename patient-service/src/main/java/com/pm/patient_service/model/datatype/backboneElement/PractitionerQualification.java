package com.pm.patient_service.model.datatype.backboneElement;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateFHIRReferenceTarget;
import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.Identifier;
import com.pm.patient_service.model.datatype.Period;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class PractitionerQualification extends AbstractBackboneElement {

    @Valid
    private List<Identifier> identifier = new ArrayList<>();

    @NotNull(message = "Practitioner qualification code is mandatory")
    @Valid
    private CodeableConcept code;

    @Valid
    private Period period;

    @ValidateFHIRReferenceTarget({ "Organization" })
    @Valid
    private Reference issuer;

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public CodeableConcept getCode() {
        return code;
    }

    public void setCode(CodeableConcept code) {
        this.code = code;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Reference getIssuer() {
        return issuer;
    }

    public void setIssuer(Reference issuer) {
        this.issuer = issuer;
    }

}
