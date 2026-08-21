package com.pm.patient_service.model.datatype;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class Coding {

    // \S*: does not contain whitespace (can be empty)
    @Pattern(regexp = "\\S*", message = "Coding system must be a valid URI")
    @Column(name = "coding_system")
    private String system;

    @Column(name = "coding_version")
    private String version;

    @Column(name = "coding_code")
    private String code;

    @Column(name = "coding_display")
    private String display;

    @Column(name = "coding_user_selected")
    private Boolean userSelected;

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