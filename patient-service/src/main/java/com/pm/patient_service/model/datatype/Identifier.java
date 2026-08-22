package com.pm.patient_service.model.datatype;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

enum IdentifierUse {
    usual, official, temp, secondary, old
}

@Embeddable
public class Identifier {

    // Schema:[//authority]path[?query][#fragment]
    // Scheme: ^([^:/?#\s]+)
    // authority: (//([^/?#\s]*))? (optional with urn)
    // path: ([^?#\s]*)
    // query: (\?([^#\s]*))? (optional)
    // fragment: (#([^\s]*))? (optional)
    private static final String URI_REGEX = "^([^:/?#\\s]+):(//([^/?#\\s]*))?([^?#\\s]*)(\\?([^#\\s]*))?(#([^\\s]*))?$";

    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_use")
    private IdentifierUse use;

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "identifier_type", columnDefinition = "jsonb")
    private CodeableConcept type;

    @NotNull(message = "Identifier system is mandatory")
    @Column(name = "identifier_system", nullable = false)
    @Pattern(regexp = URI_REGEX, message = "Identifier system must be a valid URI")
    private String system;

    @NotNull(message = "Identifier value is mandatory")
    @Column(name = "identifier_value", nullable = false)
    private String value;

    @Valid
    @Embedded
    private Period period;

    // Avoid infinite loop due to
    // Identifier has Reference and Reference has Identifier
    @Transient
    private Reference assigner;

    @Column(name = "identifier_assigner_reference")
    private String assignerReference; // contain reference string of Organization

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

    public String getAssignerReference() {
        return assignerReference;
    }

    public void setAssignerReference(String assignerReference) {
        this.assignerReference = assignerReference;
    }

}