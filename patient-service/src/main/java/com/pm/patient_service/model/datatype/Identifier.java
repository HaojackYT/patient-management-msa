package com.pm.patient_service.model.datatype;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

enum IdentifierUse {
    usual, official, temp, secondary, old
}

public class Identifier {

    // Schema:[//authority]path[?query][#fragment]
    // Scheme: ^([^:/?#\s]+)
    // authority: (//([^/?#\s]*))? (optional with urn)
    // path: ([^?#\s]*)
    // query: (\?([^#\s]*))? (optional)
    // fragment: (#([^\s]*))? (optional)
    private static final String URI_REGEX = "^([^:/?#\\s]+):(//([^/?#\\s]*))?([^?#\\s]*)(\\?([^#\\s]*))?(#([^\\s]*))?$";

    private IdentifierUse use;

    @Valid
    private CodeableConcept type;

    @NotNull(message = "Identifier system is mandatory")
    @Pattern(regexp = URI_REGEX, message = "Identifier system must be a valid URI")
    private String system;

    @NotNull(message = "Identifier value is mandatory")
    private String value;

    @Valid
    private Period period;

    @Valid
    private Reference assigner;

    public IdentifierUse getUse() {
        return use;
    }

    public void setUse(IdentifierUse use) {
        this.use = use;
    }

    public CodeableConcept getType() {
        return type;
    }

    public void setType(CodeableConcept type) {
        this.type = type;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Reference getAssigner() {
        return assigner;
    }

    public void setAssigner(Reference assigner) {
        this.assigner = assigner;
    }

}