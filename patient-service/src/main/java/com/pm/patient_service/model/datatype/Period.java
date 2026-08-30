package com.pm.patient_service.model.datatype;

import com.pm.patient_service.annotation.ValidateFHIRDateTime;
import com.pm.patient_service.annotation.ValidatePeriod;

@ValidatePeriod
public class Period {

    @ValidateFHIRDateTime(message = "Period start date time format must be valid")
    private String start;

    @ValidateFHIRDateTime(message = "Period end date time format must be valid")
    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
