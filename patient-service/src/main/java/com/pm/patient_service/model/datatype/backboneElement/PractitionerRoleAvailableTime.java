package com.pm.patient_service.model.datatype.backboneElement;

import java.util.ArrayList;
import java.util.List;

import com.pm.patient_service.annotation.ValidateFHIRTime;
import com.pm.patient_service.model.enums.DaysOfWeek;

public class PractitionerRoleAvailableTime extends AbstractBackboneElement {

    private List<DaysOfWeek> daysOfWeek = new ArrayList<>();

    private Boolean allDay;

    @ValidateFHIRTime(message = "PractitionerRole availableStartTime format must be valid")
    private String availableStartTime;

    @ValidateFHIRTime(message = "PractitionerRole availableEndTime format must be valid")
    private String availableEndTime;

    public List<DaysOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<DaysOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public String getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

}
