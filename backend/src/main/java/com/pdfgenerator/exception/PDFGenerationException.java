package com.pdfgenerator.exception;

import java.util.Map;

/**
 * Exception thrown during PDF generation process
 */
public class PDFGenerationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Map<String, Object> context;
    
    public PDFGenerationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = Map.of();
    }
    
    public PDFGenerationException(ErrorCode errorCode, String message, Map<String, Object> context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public PDFGenerationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = Map.of();
    }
    
    public PDFGenerationException(ErrorCode errorCode, String message, Throwable cause, Map<String, Object> context) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public Map<String, Object> getContext() {
        return context;
    }
}