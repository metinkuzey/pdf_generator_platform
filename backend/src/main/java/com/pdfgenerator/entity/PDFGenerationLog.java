package com.pdfgenerator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pdfgenerator.enums.PDFGenerationStatus;
import com.pdfgenerator.util.UUIDGenerator;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "pdf_generation_logs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PDFGenerationLog {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "template_id", nullable = false, length = 36)
    private String templateId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_data", columnDefinition = "jsonb")
    private Map<String, Object> requestData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PDFGenerationStatus status;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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
    public PDFGenerationLog() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUIDGenerator.generateUUID();
        }
    }

    // Constructor with required fields
    public PDFGenerationLog(String id, String templateId, PDFGenerationStatus status) {
        this.id = id;
        this.templateId = templateId;
        this.status = status;
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

    public Map<String, Object> getRequestData() {
        return requestData;
    }

    public void setRequestData(Map<String, Object> requestData) {
        this.requestData = requestData;
    }

    public PDFGenerationStatus getStatus() {
        return status;
    }

    public void setStatus(PDFGenerationStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
        return "PDFGenerationLog{" +
                "id='" + id + '\'' +
                ", templateId='" + templateId + '\'' +
                ", status=" + status +
                ", processingTimeMs=" + processingTimeMs +
                ", createdAt=" + createdAt +
                '}';
    }
}