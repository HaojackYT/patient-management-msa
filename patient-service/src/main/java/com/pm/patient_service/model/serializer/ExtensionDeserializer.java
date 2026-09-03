package com.pm.patient_service.model.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pm.patient_service.model.datatype.Extension;

/**
 * Custom jackson deserializer for {@link Extension}
 * 
 * <p>
 * Reads FHIR R4 JSON structure: {@code url}, {@code extension} (recursive) and
 * dynamic {@code value[x]} elements.
 * </p>
 * 
 * <p>
 * Note: primitive datatype remains as-is, object/array -> dynamic Map
 * </p>
 */
public class ExtensionDeserializer extends JsonDeserializer<Extension> {

    @Override
    public Extension deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {

        JsonNode node = parser.readValueAsTree();

        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.isObject()) {
            throw new JsonMappingException(parser,
                    "Extension must be a JSON object, got: " + node.getNodeType());
        }

        ObjectNode object = (ObjectNode) node;
        Extension extension = new Extension();

        JsonNode urlNode = object.get("url"); // TextNode
        if (urlNode != null && !urlNode.isNull()) {
            extension.setUrl(urlNode.asText());
        }

        JsonNode extensionNode = object.get("extension"); // ArrayNode
        if (extensionNode != null && !extensionNode.isNull()) {
            if (!extensionNode.isArray()) {
                throw new JsonMappingException(parser,
                        "Extension extension must be a JSON array");
            }
            List<Extension> children = new ArrayList<>();
            for (JsonNode child : extensionNode) {
                children.add(parser.getCodec().treeToValue(child, Extension.class));
            }
            extension.setExtension(children);
        }

        for (Map.Entry<String, JsonNode> field : object.properties()) {
            String name = field.getKey();

            if ("url".equals(name) || "extension".equals(name)) {
                continue;
            }

            if (!name.startsWith(Extension.KEY_PREFIX) ||
                    name.length() <= Extension.KEY_PREFIX.length()) {
                throw new JsonMappingException(parser,
                        "Unknown field '" + name + "' in Extension");
            }

            if (field.getValue().isNull()) {
                continue;
            }

            // primitive datatype remains as-is, object/array -> dynamic Map
            extension.putValue(name, parser.getCodec().treeToValue(
                    field.getValue(),
                    Object.class));
        }

        return extension;
    }

    @Override
    public Class<Extension> handledType() {
        return Extension.class;
    }

}
