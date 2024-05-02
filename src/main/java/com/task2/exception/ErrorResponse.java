package com.task2.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}