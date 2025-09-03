package com.pdfgenerator.exception;

public class TemplateNotFoundException extends RuntimeException {
    
    public TemplateNotFoundException(String templateId) {
        super("Template not found with id: " + templateId);
    }
    
    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}