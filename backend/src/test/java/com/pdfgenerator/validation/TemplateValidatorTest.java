package com.pdfgenerator.validation;

import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateValidatorTest {
    
    private TemplateValidator templateValidator;
    private TemplateRequest validRequest;
    private Map<String, Object> validSchema;
    
    @BeforeEach
    void setUp() {
        templateValidator = new TemplateValidator();
        
        // Valid schema
        validSchema = new HashMap<>();
        validSchema.put("layout", Map.of(
            "pageSize", "A4",
            "orientation", "PORTRAIT",
            "margins", Map.of("top", 20, "right", 20, "bottom", 20, "left", 20)
        ));
        validSchema.put("elements", List.of(
            Map.of(
                "id", "title",
                "type", "TEXT",
                "position", Map.of("x", 0, "y", 0, "width", 100, "height", 10)
            )
        ));
        
        // Valid request
        validRequest = new TemplateRequest();
        validRequest.setName("Valid Template");
        validRequest.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        validRequest.setSchema(validSchema);
    }
    
    @Test
    void validateTemplateRequest_WithValidRequest_ShouldNotThrowException() {
        // When & Then
        assertDoesNotThrow(() -> templateValidator.validateTemplateRequest(validRequest));
    }
    
    @Test
    void validateTemplateRequest_WithNullName_ShouldThrowValidationException() {
        // Given
        validRequest.setName(null);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template name is required"));
    }
    
    @Test
    void validateTemplateRequest_WithEmptyName_ShouldThrowValidationException() {
        // Given
        validRequest.setName("  ");
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template name is required"));
    }
    
    @Test
    void validateTemplateRequest_WithShortName_ShouldThrowValidationException() {
        // Given
        validRequest.setName("AB");
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template name must be between 3 and 255 characters"));
    }
    
    @Test
    void validateTemplateRequest_WithLongName_ShouldThrowValidationException() {
        // Given
        validRequest.setName("A".repeat(256));
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template name must be between 3 and 255 characters"));
    }
    
    @Test
    void validateTemplateRequest_WithNullCategory_ShouldThrowValidationException() {
        // Given
        validRequest.setCategory(null);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template category is required"));
    }
    
    @Test
    void validateTemplateRequest_WithNullSchema_ShouldThrowValidationException() {
        // Given
        validRequest.setSchema(null);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template schema is required"));
    }
    
    @Test
    void validateTemplateRequest_WithEmptySchema_ShouldThrowValidationException() {
        // Given
        validRequest.setSchema(new HashMap<>());
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template schema is required"));
    }
    
    @Test
    void validateSchema_WithoutLayout_ShouldThrowValidationException() {
        // Given
        Map<String, Object> schemaWithoutLayout = new HashMap<>(validSchema);
        schemaWithoutLayout.remove("layout");
        validRequest.setSchema(schemaWithoutLayout);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Schema must contain 'layout' configuration"));
    }
    
    @Test
    void validateSchema_WithoutElements_ShouldThrowValidationException() {
        // Given
        Map<String, Object> schemaWithoutElements = new HashMap<>(validSchema);
        schemaWithoutElements.remove("elements");
        validRequest.setSchema(schemaWithoutElements);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Schema must contain 'elements' array"));
    }
    
    @Test
    void validateSchema_WithEmptyElements_ShouldThrowValidationException() {
        // Given
        Map<String, Object> schemaWithEmptyElements = new HashMap<>(validSchema);
        schemaWithEmptyElements.put("elements", List.of());
        validRequest.setSchema(schemaWithEmptyElements);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().contains("Template must contain at least one element"));
    }
    
    @Test
    void validateElement_WithoutId_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithoutId = Map.of(
            "type", "TEXT",
            "position", Map.of("x", 0, "y", 0, "width", 100, "height", 10)
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithoutId));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Element must have an 'id' field")));
    }
    
    @Test
    void validateElement_WithoutType_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithoutType = Map.of(
            "id", "test-element",
            "position", Map.of("x", 0, "y", 0, "width", 100, "height", 10)
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithoutType));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Element must have a 'type' field")));
    }
    
    @Test
    void validateElement_WithInvalidType_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithInvalidType = Map.of(
            "id", "test-element",
            "type", "INVALID_TYPE",
            "position", Map.of("x", 0, "y", 0, "width", 100, "height", 10)
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithInvalidType));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Invalid element type: INVALID_TYPE")));
    }
    
    @Test
    void validateElement_WithoutPosition_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithoutPosition = Map.of(
            "id", "test-element",
            "type", "TEXT"
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithoutPosition));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Element must have a 'position' field")));
    }
    
    @Test
    void validatePosition_WithMissingCoordinates_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithIncompletePosition = Map.of(
            "id", "test-element",
            "type", "TEXT",
            "position", Map.of("x", 0, "y", 0) // missing width and height
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithIncompletePosition));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Position must contain 'width' field")));
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Position must contain 'height' field")));
    }
    
    @Test
    void validatePosition_WithNegativeValues_ShouldThrowValidationException() {
        // Given
        Map<String, Object> elementWithNegativePosition = Map.of(
            "id", "test-element",
            "type", "TEXT",
            "position", Map.of("x", -10, "y", 0, "width", 100, "height", 10)
        );
        
        Map<String, Object> schemaWithInvalidElement = new HashMap<>(validSchema);
        schemaWithInvalidElement.put("elements", List.of(elementWithNegativePosition));
        validRequest.setSchema(schemaWithInvalidElement);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Position 'x' must be non-negative")));
    }
    
    @Test
    void validateLayout_WithInvalidPageSize_ShouldThrowValidationException() {
        // Given
        Map<String, Object> layoutWithInvalidPageSize = Map.of(
            "pageSize", "INVALID_SIZE",
            "orientation", "PORTRAIT"
        );
        
        Map<String, Object> schemaWithInvalidLayout = new HashMap<>(validSchema);
        schemaWithInvalidLayout.put("layout", layoutWithInvalidPageSize);
        validRequest.setSchema(schemaWithInvalidLayout);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Invalid page size: INVALID_SIZE")));
    }
    
    @Test
    void validateLayout_WithInvalidOrientation_ShouldThrowValidationException() {
        // Given
        Map<String, Object> layoutWithInvalidOrientation = Map.of(
            "pageSize", "A4",
            "orientation", "INVALID_ORIENTATION"
        );
        
        Map<String, Object> schemaWithInvalidLayout = new HashMap<>(validSchema);
        schemaWithInvalidLayout.put("layout", layoutWithInvalidOrientation);
        validRequest.setSchema(schemaWithInvalidLayout);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Invalid orientation: INVALID_ORIENTATION")));
    }
    
    @Test
    void validateMargins_WithNegativeValues_ShouldThrowValidationException() {
        // Given
        Map<String, Object> layoutWithNegativeMargins = Map.of(
            "pageSize", "A4",
            "orientation", "PORTRAIT",
            "margins", Map.of("top", -10, "right", 20, "bottom", 20, "left", 20)
        );
        
        Map<String, Object> schemaWithInvalidLayout = new HashMap<>(validSchema);
        schemaWithInvalidLayout.put("layout", layoutWithNegativeMargins);
        validRequest.setSchema(schemaWithInvalidLayout);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> templateValidator.validateTemplateRequest(validRequest));
        
        assertTrue(exception.getErrors().stream()
            .anyMatch(error -> error.contains("Margin 'top' must be non-negative")));
    }
}