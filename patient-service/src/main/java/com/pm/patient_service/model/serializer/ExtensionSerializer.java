package com.pm.patient_service.model.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pm.patient_service.model.datatype.Extension;

/**
 * Custom jackson serializer for {@link Extension}
 * 
 * <p>
 * Writes FHIR R4 JSON structure:
 * {@code url} + dynamic {@code value[x]} elements + nested {@code extension}s.
 * </p>
 */
public class ExtensionSerializer extends JsonSerializer<Extension> {

    @Override
    public void serialize(Extension value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeStartObject(); // {

        if (value.getUrl() != null) {
            gen.writeStringField("url", value.getUrl());
        }

        // Flatten valueX into JSON fields
        Map<String, Object> valueX = value.getValueX();
        if (valueX != null && !valueX.isEmpty()) {
            for (Map.Entry<String, Object> entry : valueX.entrySet()) {
                // Avoid serialize null value of a key in valueX
                if (entry.getValue() == null) {
                    continue;
                }
                gen.writeFieldName(entry.getKey());
                serializers.defaultSerializeValue(entry.getValue(), gen);
            }
        }

        if (value.getExtension() != null && !value.getExtension().isEmpty()) {
            gen.writeFieldName("extension");
            serializers.defaultSerializeValue(value.getExtension(), gen);
        }

        gen.writeEndObject(); // }
    }

    @Override
    public Class<Extension> handledType() {
        return Extension.class;
    }

}
