package com.pdfgenerator.dto;

import com.pdfgenerator.enums.TemplateCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class TemplateRequest {
    
    @NotBlank(message = "Template name is required")
    @Size(min = 3, max = 255, message = "Template name must be between 3 and 255 characters")
    private String name;
    
    @NotNull(message = "Template category is required")
    private TemplateCategory category;
    
    @NotNull(message = "Template schema is required")
    private Map<String, Object> schema;
    
    private Map<String, Object> metadata;
    
    private String description;
    
    // Constructors
    public TemplateRequest() {}
    
    public TemplateRequest(String name, TemplateCategory category, Map<String, Object> schema) {
        this.name = name;
        this.category = category;
        this.schema = schema;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public TemplateCategory getCategory() {
        return category;
    }
    
    public void setCategory(TemplateCategory category) {
        this.category = category;
    }
    
    public Map<String, Object> getSchema() {
        return schema;
    }
    
    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}