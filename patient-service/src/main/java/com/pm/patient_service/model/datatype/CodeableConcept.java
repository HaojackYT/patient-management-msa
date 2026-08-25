package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public class CodeableConcept {

    @Valid
    private List<Coding> coding = new ArrayList<>();

    private String text;

    public CodeableConcept() {
    }

    public List<Coding> getCoding() {
        return coding;
    }

    public void setCoding(List<Coding> coding) {
        this.coding = coding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}