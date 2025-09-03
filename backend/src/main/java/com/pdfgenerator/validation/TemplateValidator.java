package com.pdfgenerator.validation;

import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TemplateValidator {
    
    public void validateTemplateRequest(TemplateRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Basic field validation
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Template name is required");
        } else if (request.getName().length() < 3 || request.getName().length() > 255) {
            errors.add("Template name must be between 3 and 255 characters");
        }
        
        if (request.getCategory() == null) {
            errors.add("Template category is required");
        }
        
        if (request.getSchema() == null || request.getSchema().isEmpty()) {
            errors.add("Template schema is required");
        } else {
            validateSchema(request.getSchema(), errors);
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Template validation failed", errors);
        }
    }
    
    private void validateSchema(Map<String, Object> schema, List<String> errors) {
        // Validate required schema fields
        if (!schema.containsKey("layout")) {
            errors.add("Schema must contain 'layout' configuration");
        }
        
        if (!schema.containsKey("elements")) {
            errors.add("Schema must contain 'elements' array");
        } else {
            Object elements = schema.get("elements");
            if (!(elements instanceof List)) {
                errors.add("Schema 'elements' must be an array");
            } else {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> elementList = (List<Map<String, Object>>) elements;
                validateElements(elementList, errors);
            }
        }
        
        // Validate layout configuration
        if (schema.containsKey("layout")) {
            Object layout = schema.get("layout");
            if (layout instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> layoutMap = (Map<String, Object>) layout;
                validateLayout(layoutMap, errors);
            } else {
                errors.add("Schema 'layout' must be an object");
            }
        }
    }
    
    private void validateElements(List<Map<String, Object>> elements, List<String> errors) {
        if (elements.isEmpty()) {
            errors.add("Template must contain at least one element");
            return;
        }
        
        for (int i = 0; i < elements.size(); i++) {
            Map<String, Object> element = elements.get(i);
            validateElement(element, i, errors);
        }
    }
    
    private void validateElement(Map<String, Object> element, int index, List<String> errors) {
        String prefix = "Element[" + index + "]: ";
        
        // Required fields for all elements
        if (!element.containsKey("id") || element.get("id") == null) {
            errors.add(prefix + "Element must have an 'id' field");
        }
        
        if (!element.containsKey("type") || element.get("type") == null) {
            errors.add(prefix + "Element must have a 'type' field");
        } else {
            String type = element.get("type").toString();
            if (!isValidElementType(type)) {
                errors.add(prefix + "Invalid element type: " + type);
            }
        }
        
        if (!element.containsKey("position") || element.get("position") == null) {
            errors.add(prefix + "Element must have a 'position' field");
        } else {
            Object position = element.get("position");
            if (position instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> positionMap = (Map<String, Object>) position;
                validatePosition(positionMap, prefix, errors);
            } else {
                errors.add(prefix + "Position must be an object");
            }
        }
    }
    
    private void validatePosition(Map<String, Object> position, String prefix, List<String> errors) {
        String[] requiredFields = {"x", "y", "width", "height"};
        
        for (String field : requiredFields) {
            if (!position.containsKey(field)) {
                errors.add(prefix + "Position must contain '" + field + "' field");
            } else {
                Object value = position.get(field);
                if (!(value instanceof Number)) {
                    errors.add(prefix + "Position '" + field + "' must be a number");
                } else {
                    double numValue = ((Number) value).doubleValue();
                    if (numValue < 0) {
                        errors.add(prefix + "Position '" + field + "' must be non-negative");
                    }
                }
            }
        }
    }
    
    private void validateLayout(Map<String, Object> layout, List<String> errors) {
        // Validate page size
        if (layout.containsKey("pageSize")) {
            String pageSize = layout.get("pageSize").toString();
            if (!isValidPageSize(pageSize)) {
                errors.add("Invalid page size: " + pageSize + ". Supported: A4, A3, LETTER, LEGAL");
            }
        }
        
        // Validate orientation
        if (layout.containsKey("orientation")) {
            String orientation = layout.get("orientation").toString();
            if (!orientation.equals("PORTRAIT") && !orientation.equals("LANDSCAPE")) {
                errors.add("Invalid orientation: " + orientation + ". Supported: PORTRAIT, LANDSCAPE");
            }
        }
        
        // Validate margins
        if (layout.containsKey("margins")) {
            Object margins = layout.get("margins");
            if (margins instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> marginsMap = (Map<String, Object>) margins;
                validateMargins(marginsMap, errors);
            } else {
                errors.add("Layout margins must be an object");
            }
        }
    }
    
    private void validateMargins(Map<String, Object> margins, List<String> errors) {
        String[] marginFields = {"top", "right", "bottom", "left"};
        
        for (String field : marginFields) {
            if (margins.containsKey(field)) {
                Object value = margins.get(field);
                if (!(value instanceof Number)) {
                    errors.add("Margin '" + field + "' must be a number");
                } else {
                    double numValue = ((Number) value).doubleValue();
                    if (numValue < 0) {
                        errors.add("Margin '" + field + "' must be non-negative");
                    }
                }
            }
        }
    }
    
    private boolean isValidElementType(String type) {
        return type.equals("TEXT") || type.equals("TABLE") || type.equals("IMAGE") || 
               type.equals("SHAPE") || type.equals("CONTAINER") || type.equals("DATA_FIELD");
    }
    
    private boolean isValidPageSize(String pageSize) {
        return pageSize.equals("A4") || pageSize.equals("A3") || 
               pageSize.equals("LETTER") || pageSize.equals("LEGAL");
    }
}