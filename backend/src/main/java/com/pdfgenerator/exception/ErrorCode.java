package com.pdfgenerator.exception;

/**
 * Error codes for different types of exceptions
 */
public enum ErrorCode {
    // Template validation errors
    TEMPLATE_NAME_REQUIRED("TMPL_001", "Template name is required"),
    TEMPLATE_CATEGORY_REQUIRED("TMPL_002", "Template category is required"),
    TEMPLATE_SCHEMA_REQUIRED("TMPL_003", "Template schema is required"),
    TEMPLATE_SCHEMA_INVALID("TMPL_004", "Template schema is invalid"),
    TEMPLATE_NOT_FOUND("TMPL_005", "Template not found"),
    
    // PDF generation errors
    PDF_GENERATION_FAILED("PDF_001", "PDF generation failed"),
    INVALID_DATA_FORMAT("PDF_002", "Invalid data format for PDF generation"),
    TEMPLATE_RENDERING_FAILED("PDF_003", "Template rendering failed"),
    
    // General validation errors
    VALIDATION_FAILED("VAL_001", "Validation failed"),
    INVALID_INPUT("VAL_002", "Invalid input provided");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}