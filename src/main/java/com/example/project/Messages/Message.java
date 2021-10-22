package com.example.project.Messages;

import org.springframework.http.ResponseEntity;

public class Message {

    private Exception exception;
    private String message;
    private Exception e;
    private ResponseEntity messageResponseEntity;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String message) {
        this.message = message;
    }
    public Message(Exception exception ){
        this.exception = exception;
    }

    public Message(ResponseEntity<Message> messageResponseEntity){
        this.messageResponseEntity = messageResponseEntity;
    }

}
