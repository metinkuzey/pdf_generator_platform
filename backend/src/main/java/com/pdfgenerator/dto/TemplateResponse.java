package com.pdfgenerator.dto;

import com.pdfgenerator.enums.TemplateCategory;

import java.time.LocalDateTime;
import java.util.Map;

public class TemplateResponse {
    
    private String id;
    private String name;
    private TemplateCategory category;
    private Map<String, Object> schema;
    private Map<String, Object> metadata;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private boolean active;
    private Integer version;
    
    // Constructors
    public TemplateResponse() {}
    
    public TemplateResponse(String id, String name, TemplateCategory category, 
                           Map<String, Object> schema, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.schema = schema;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
}