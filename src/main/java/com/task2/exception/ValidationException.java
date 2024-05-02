package com.task2.exception;

import org.springframework.validation.FieldError;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<FieldError> fieldErrors;

    public ValidationException(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}