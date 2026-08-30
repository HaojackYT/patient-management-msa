package com.pm.patient_service.model.datatype;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

enum HumanNameUse {
    usual, official, temp, nickname, anonymous, old, maiden
}

public class HumanName {

    @Valid
    private List<Extension> extension = new ArrayList<>();

    private HumanNameUse use;

    private String text;

    private String family;

    private List<String> given = new ArrayList<>();

    private List<String> prefix = new ArrayList<>();

    private List<String> suffix = new ArrayList<>();

    @Valid
    private Period period;

    public List<Extension> getExtension() {
        return extension;
    }

    public void setExtension(List<Extension> extension) {
        this.extension = extension;
    }

    public HumanNameUse getUse() {
        return use;
    }

    public void setUse(HumanNameUse use) {
        this.use = use;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    public List<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(List<String> prefix) {
        this.prefix = prefix;
    }

    public List<String> getSuffix() {
        return suffix;
    }

    public void setSuffix(List<String> suffix) {
        this.suffix = suffix;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

}