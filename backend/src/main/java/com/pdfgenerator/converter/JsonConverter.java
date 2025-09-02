package com.pdfgenerator.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting Map to JSON string", e);
            throw new RuntimeException("Error converting Map to JSON string", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            logger.error("Error converting JSON string to Map", e);
            throw new RuntimeException("Error converting JSON string to Map", e);
        }
    }
}