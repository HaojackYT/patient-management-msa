package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.validation.Valid;

@Embeddable
public class CodeableConcept {

    @Valid
    @ElementCollection // force JPA create a separate table
    @CollectionTable(name = "codeable_concept_coding", joinColumns = @JoinColumn(name = "codeable_concept_id"))
    @Embedded
    private List<Coding> coding = new ArrayList<>();

    @Column(name = "codeable_concept_text")
    private String text;

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