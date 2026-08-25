package com.pm.patient_service.model.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.pm.patient_service.model.datatype.CodeableConcept;
import com.pm.patient_service.model.datatype.Period;
import com.pm.patient_service.model.datatype.Reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "patient_identifier",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_patient_identifier_system_value",
            columnNames = {"system", "value"}
        )
    },
    indexes = {
        @Index(
            name = "idx_patient_identifier_system_value",
            columnList = "system,value"
        )
    }
)
public class PatientIdentifier {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Patient patient;

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "type", columnDefinition = "jsonb")
    private CodeableConcept type;

    @NotNull(message = "PatientIdentifier system is mandatory")
    @Column(name = "system", nullable = false)
    private String system;

    @NotNull(message = "PatientIdentifier value is mandatory")
    @Column(name = "value", nullable = false)
    private String value;

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "period", columnDefinition = "jsonb")
    private Period period;

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "assigner", columnDefinition = "jsonb")
    private Reference assigner;

    public UUID getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
