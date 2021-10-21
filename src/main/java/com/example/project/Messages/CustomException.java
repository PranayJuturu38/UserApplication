package com.example.project.Messages;

import org.springframework.lang.NonNull;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomException(@NonNull final String message) {
        super(message);
    }
    public CustomException(String message,Throwable cause){
        super(message,cause);
    }
}
