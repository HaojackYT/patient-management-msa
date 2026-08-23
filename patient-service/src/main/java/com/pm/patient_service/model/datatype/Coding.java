package com.pm.patient_service.model.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Pattern;

// avoid crashing when receiving unknown properties from the client
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coding {

    // \S*: does not contain whitespace (can be empty)
    @Pattern(regexp = "\\S*", message = "Coding system must be a valid URI")
    private String system;

    private String version;

    private String code;

    private String display;

    private Boolean userSelected;

    public Coding() {
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Boolean getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(Boolean userSelected) {
        this.userSelected = userSelected;
    }

}