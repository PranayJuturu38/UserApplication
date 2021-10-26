package com.example.project.Messages;

import org.springframework.lang.NonNull;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;
    private String details;

    public CustomException(@NonNull final String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
