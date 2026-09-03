package com.pm.patient_service.model.datatype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pm.patient_service.annotation.ValidateFHIRExtension;
import com.pm.patient_service.model.serializer.ExtensionDeserializer;
import com.pm.patient_service.model.serializer.ExtensionSerializer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * FHIR R4 {@code Extension} datatype — dynamic storage as JSON (jsonb).
 *
 * <p>
 * {@code value[x]} is kept in a dynamic {@link Map}:
 * </p>
 * 
 * <ul>
 * <li>key: FHIR element name ({@code valueString},
 * {@code valueCodeableConcept}, ...)</li>
 * <li>value: primitive datatype or Map<String, Object> for complex
 * datatype</li>
 * </ul>
 * FHIR JSON <-> object conversion is handled by {@link ExtensionSerializer} /
 * {@link ExtensionDeserializer}
 * </p>
 *
 * <p>
 * Rule ext-1 — enforced by {@link ValidateFHIRExtension}:
 * "Must have either extensions or value[x], not both".
 * </p>
 */
@ValidateFHIRExtension
@JsonSerialize(using = ExtensionSerializer.class)
@JsonDeserialize(using = ExtensionDeserializer.class)
public class Extension implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String KEY_PREFIX = "value";

    @NotNull(message = "Extension url is mandatory")
    @NotBlank(message = "Extension url cannot be blank")
    private String url;

    // key: value + {data type}
    // value: primitive datatype or Map<String, Object> for complex datatype
    private Map<String, Object> valueX = new LinkedHashMap<>();

    @Valid
    private List<Extension> extension = new ArrayList<>();

    public Extension() {
    }

    public Extension(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Extension> getExtension() {
        return extension;
    }

    public void setExtension(List<Extension> extension) {
        this.extension = extension;
    }

    public Map<String, Object> getValueX() {
        return valueX;
    }

    // Custom setter for complex datatypes

    public void putValue(String key, Object value) {
        if (key == null || !key.startsWith(KEY_PREFIX) || key.length() <= KEY_PREFIX.length()) {
            throw new IllegalArgumentException(
                    "Extension value[x] key must start with '" + KEY_PREFIX +
                            "' (e.g. valueString), got: " + key);
        }
        valueX.put(key, value);
    }

    public Object getValue(String key) {
        return valueX.get(key);
    }

    public Object removeValue(String key) {
        return valueX.remove(key);
    }

    // Custom getters and setters for values in valueX

    public String getValueString() {
        return asString(getValue("valueString"));
    }

    public void setValueString(String valueString) {
        putValue("valueString", valueString);
    }

    public String getValueCode() {
        return asString(getValue("valueCode"));
    }

    public void setValueCode(String valueCode) {
        putValue("valueCode", valueCode);
    }

    public String getValueUri() {
        return asString(getValue("valueUri"));
    }

    public void setValueUri(String valueUri) {
        putValue("valueUri", valueUri);
    }

    public String getValueBase64Binary() {
        return asString(getValue("valueBase64Binary"));
    }

    public void setValueBase64Binary(String valueBase64Binary) {
        putValue("valueBase64Binary", valueBase64Binary);
    }

    public String getValueOid() {
        return asString(getValue("valueOid"));
    }

    public void setValueOid(String valueOid) {
        putValue("valueOid", valueOid);
    }

    public String getValueId() {
        return asString(getValue("valueId"));
    }

    public void setValueId(String valueId) {
        putValue("valueId", valueId);
    }

    public String getValueMarkdown() {
        return asString(getValue("valueMarkdown"));
    }

    public void setValueMarkdown(String valueMarkdown) {
        putValue("valueMarkdown", valueMarkdown);
    }

    public String getValueUrl() {
        return asString(getValue("valueUrl"));
    }

    public void setValueUrl(String valueUrl) {
        putValue("valueUrl", valueUrl);
    }

    public String getValueCanonical() {
        return asString(getValue("valueCanonical"));
    }

    public void setValueCanonical(String valueCanonical) {
        putValue("valueCanonical", valueCanonical);
    }

    public String getValueUuid() {
        return asString(getValue("valueUuid"));
    }

    public void setValueUuid(String valueUuid) {
        putValue("valueUuid", valueUuid);
    }

    public String getValueDateTime() {
        return asString(getValue("valueDateTime"));
    }

    public void setValueDateTime(String valueDateTime) {
        putValue("valueDateTime", valueDateTime);
    }

    public String getValueDate() {
        return asString(getValue("valueDate"));
    }

    public void setValueDate(String valueDate) {
        putValue("valueDate", valueDate);
    }

    public String getValueTime() {
        return asString(getValue("valueTime"));
    }

    public void setValueTime(String valueTime) {
        putValue("valueTime", valueTime);
    }

    public String getValueInstant() {
        return asString(getValue("valueInstant"));
    }

    public void setValueInstant(String valueInstant) {
        putValue("valueInstant", valueInstant);
    }

    public Boolean getValueBoolean() {
        return asBoolean(getValue("valueBoolean"));
    }

    public void setValueBoolean(Boolean valueBoolean) {
        putValue("valueBoolean", valueBoolean);
    }

    public Integer getValueInteger() {
        return asInteger(getValue("valueInteger"));
    }

    public void setValueInteger(Integer valueInteger) {
        putValue("valueInteger", valueInteger);
    }

    public Integer getValuePositiveInt() {
        return asInteger(getValue("valuePositiveInt"));
    }

    public void setValuePositiveInt(Integer valuePositiveInt) {
        putValue("valuePositiveInt", valuePositiveInt);
    }

    public Integer getValueUnsignedInt() {
        return asInteger(getValue("valueUnsignedInt"));
    }

    public void setValueUnsignedInt(Integer valueUnsignedInt) {
        putValue("valueUnsignedInt", valueUnsignedInt);
    }

    public BigDecimal getValueDecimal() {
        return asDecimal(getValue("valueDecimal"));
    }

    public void setValueDecimal(BigDecimal valueDecimal) {
        putValue("valueDecimal", valueDecimal);
    }

    // Helper methods

    private static String asString(Object value) {
        return value instanceof String ? (String) value : null;
    }

    private static Boolean asBoolean(Object value) {
        return value instanceof Boolean ? (Boolean) value : null;
    }

    private static Integer asInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    private static BigDecimal asDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return value instanceof Number ? new BigDecimal(value.toString()) : null;
    }

}
