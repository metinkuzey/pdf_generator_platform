package com.pdfgenerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.util.UUIDGenerator;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "templates")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Template {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private TemplateCategory category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> schema;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 36)
    private String createdBy;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    // Default constructor
    public Template() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUIDGenerator.generateUUID();
        }
    }

    // Constructor with required fields
    public Template(String id, String name, TemplateCategory category, Map<String, Object> schema) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.schema = schema;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", active=" + active +
                ", version=" + version +
                ", createdAt=" + createdAt +
                '}';
    }
}