package com.pm.patient_service.model.datatype;

import com.pm.patient_service.annotation.ValidatePeriod;

import jakarta.validation.constraints.Pattern;

@ValidatePeriod
public class Period {
    
    // YYYY: 0001 -> 9999 ([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)
    // MM: 01 -> 12 (0[1-9]|1[0-2])
    // DD: 01 -> 31 (0[1-9]|[1-2][0-9]|3[0-1])
    // HH: 00 -> 23 ([01][0-9]|2[0-3])
    // MM: 00 -> 59 [0-5][0-9]
    // SS: 00 -> 59 ([0-5][0-9]|60)
    // MS: 1 to infinite decimal places (\\.[0-9]+)? (Optional)
    // TZ: 00:00 -> 14:00 Z or +HH:MM or -HH:MM
    // (Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))
    private static final String DATE_TIME_REGEX =
        "\\A" +
        "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)" +
        "(-(0[1-9]|1[0-2])" +
        "(-(0[1-9]|[1-2][0-9]|3[0-1])" +
        "(T([01][0-9]|2[0-3]):[0-5][0-9]:" +
        "([0-5][0-9]|60)(\\.[0-9]+)?" +
        "(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00)))?" +
        ")?)?)?" +
        "\\z";

    @Pattern(regexp = DATE_TIME_REGEX, message = "Period start date time format must be valid")
    private String start;

    @Pattern(regexp = DATE_TIME_REGEX, message = "Period end date time format must be valid")
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
