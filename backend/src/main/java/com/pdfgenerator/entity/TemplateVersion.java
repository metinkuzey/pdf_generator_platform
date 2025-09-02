package com.pdfgenerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pdfgenerator.util.UUIDGenerator;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "template_versions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TemplateVersion {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "template_id", nullable = false, length = 36)
    private String templateId;

    @Column(name = "version", nullable = false)
    private Integer version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schema", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> schema;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 36)
    private String createdBy;

    // Many-to-One relationship with Template
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private Template template;

    // Default constructor
    public TemplateVersion() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUIDGenerator.generateUUID();
        }
    }

    // Constructor with required fields
    public TemplateVersion(String id, String templateId, Integer version, Map<String, Object> schema) {
        this.id = id;
        this.templateId = templateId;
        this.version = version;
        this.schema = schema;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "TemplateVersion{" +
                "id='" + id + '\'' +
                ", templateId='" + templateId + '\'' +
                ", version=" + version +
                ", createdAt=" + createdAt +
                '}';
    }
}