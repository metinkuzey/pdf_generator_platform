package com.pdfgenerator.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    
    private final List<String> errors;
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public ValidationException(String message, List<String> errors, Throwable cause) {
        super(message, cause);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}