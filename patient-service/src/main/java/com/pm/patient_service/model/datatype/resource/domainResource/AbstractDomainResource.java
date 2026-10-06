package com.pm.patient_service.model.datatype.resource.domainResource;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateDomainResource;
import com.pm.patient_service.model.datatype.Extension;
import com.pm.patient_service.model.datatype.Narrative;
import com.pm.patient_service.model.datatype.resource.AbstractResource;

import jakarta.validation.Valid;

@ValidateDomainResource
public abstract class AbstractDomainResource extends AbstractResource {

    @Valid
    private Narrative text;

    @Valid
    private List<AbstractResource> contained = new ArrayList<>();

    @Valid
    private List<Extension> extension = new ArrayList<>();

    @Valid
    private List<Extension> modifierExtension = new ArrayList<>();

    public Narrative getText() {
        return text;
    }

    public void setText(Narrative text) {
        this.text = text;
    }

    public List<AbstractResource> getContained() {
        return contained;
    }

    public void setContained(List<AbstractResource> contained) {
        this.contained = contained;
    }

    public List<Extension> getExtension() {
        return extension;
    }

    public void setExtension(List<Extension> extension) {
        this.extension = extension;
    }

    public List<Extension> getModifierExtension() {
        return modifierExtension;
    }

    public void setModifierExtension(List<Extension> modifierExtension) {
        this.modifierExtension = modifierExtension;
    }

}
