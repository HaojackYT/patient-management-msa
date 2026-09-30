package com.pm.patient_service.model.datatype;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.pm.patient_service.annotation.ValidateFHIRId;

import jakarta.validation.Valid;

public class Meta {

    @ValidateFHIRId
    private String versionId; // id

    private Instant lastUpdated;

    private String source; // uri

    // TODO: canonical(StructureDefinition)
    private List<String> profile = new ArrayList<>();

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Coding> security = new ArrayList<>();

    @Valid
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Coding> tag = new ArrayList<>();

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getProfile() {
        return profile;
    }

    public void setProfile(List<String> profile) {
        this.profile = profile;
    }

    public List<Coding> getSecurity() {
        return security;
    }

    public void setSecurity(List<Coding> security) {
        this.security = security;
    }

    public List<Coding> getTag() {
        return tag;
    }

    public void setTag(List<Coding> tag) {
        this.tag = tag;
    }

}