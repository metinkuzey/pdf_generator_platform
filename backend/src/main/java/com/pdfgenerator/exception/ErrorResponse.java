package com.pdfgenerator.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ErrorResponse {
    
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;
    private Map<String, Object> context;
    private Map<String, Object> details;
    
    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public ErrorResponse(String code, String message, List<String> errors) {
        this(code, message);
        this.errors = errors;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ErrorResponse errorResponse = new ErrorResponse();
        
        public Builder code(String code) {
            errorResponse.code = code;
            return this;
        }
        
        public Builder message(String message) {
            errorResponse.message = message;
            return this;
        }
        
        public Builder errors(List<String> errors) {
            errorResponse.errors = errors;
            return this;
        }
        
        public Builder context(Map<String, Object> context) {
            errorResponse.context = context;
            return this;
        }
        
        public Builder details(Map<String, Object> details) {
            errorResponse.details = details;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            errorResponse.timestamp = timestamp;
            return this;
        }
        
        public ErrorResponse build() {
            return errorResponse;
        }
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public Map<String, Object> getContext() {
        return context;
    }
    
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}