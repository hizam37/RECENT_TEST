package com.example.task_management_service.exception;

public class AuthorizationDeniedException extends RuntimeException{
    public AuthorizationDeniedException(String message) {
        super(message);
    }
}
