package com.pm.patient_service.model.datatype;

import org.hibernate.validator.constraints.ScriptAssert;

import jakarta.validation.constraints.Pattern;

@ScriptAssert(lang = "javascript", script = "_this.start <= _this.end", message = "Start date time <= end date")
public class Period {

    // YYYY: 0001 -> 9999 ([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)
    // MM: 01 -> 12 (0[1-9]|1[0-2])
    // DD: 01 -> 31 (0[1-9]|[1-2][0-9]|3[0-1])
    // HH: 00 -> 23 ([01][0-9]|2[0-3])
    // MM: 00 -> 59 [0-5][0-9]
    // SS: 00 -> 59 ([0-5][0-9]|60)
    // MS: .0 -> .9 (\\.[0-9]+)? (Optional)
    // TZ: 00:00 -> 14:00 Z or +HH:MM or -HH:MM
    // (Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))
    private static final String DATE_TIME_REGEX = "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)(-(0[1-9]|1[0-2])(-(0[1-9]|[1-2][0-9]|3[0-1])(T([01][0-9]|2[0-3]):[0-5][0-9]:([0-5][0-9]|60)(\\.[0-9]+)?(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00)))?)?)?";

    @Pattern(regexp = DATE_TIME_REGEX, message = "Invalid date time format")
    private String start;

    @Pattern(regexp = DATE_TIME_REGEX, message = "Invalid date time format")
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
