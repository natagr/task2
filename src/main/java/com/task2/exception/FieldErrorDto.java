package com.task2.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

@Getter
@Setter
public class FieldErrorDto {
    private final String field;
    private final String message;

    public FieldErrorDto(FieldError fieldError) {
        this.field = fieldError.getField();
        this.message = fieldError.getDefaultMessage();
    }
}