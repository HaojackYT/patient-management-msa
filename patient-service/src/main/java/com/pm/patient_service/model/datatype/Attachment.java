package com.pm.patient_service.model.datatype;

import com.pm.patient_service.annotation.ValidateFHIRDateTime;
import com.pm.patient_service.annotation.ValidateFHIRAttachment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@ValidateFHIRAttachment
public class Attachment {

    // ^...$: ensure the entire string matches the regex
    // ^: negation
    // \\s: whitespace
    // +: one or more
    // [^/\\s]: require continuous string without whitespace or "/" separator
    private static final String MIME_TYPE_REGEX = "^[^/\\s]+/[^/\\s]+$";
    private static final String MIME_TYPE_MESSAGE = "Attachment contentType must be a valid FHIR R4 MimeType";

    // [A-Za-z]{2,8}: 2 to 8 capital or lowercase characters
    // (...)*: zero or more occurrences of the following
    private static final String BCP_47_REGEX = "^[A-Za-z]{2,8}(-[A-Za-z0-9]{1,8})*$";
    private static final String BCP_47_MESSAGE = "Attachment language must be a valid FHIR R4 Language Code";

    @Pattern(regexp = MIME_TYPE_REGEX, message = MIME_TYPE_MESSAGE)
    private String contentType;

    @Pattern(regexp = BCP_47_REGEX, message = BCP_47_MESSAGE)
    private String language;

    private byte[] data;

    private String url;

    @Min(value = 0, message = "Attachment size must be non-negative")
    private int size;

    private String hash;

    private String title;

    @ValidateFHIRDateTime(message = "Creation date time format must be valid")
    private String creation;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

}