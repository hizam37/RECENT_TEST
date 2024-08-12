package com.example.task_management_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<AppError> registrationException(RegistrationException ex) {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<AppError> taskException(TaskException ex) {
        return new ResponseEntity<>(new AppError(HttpStatus.FORBIDDEN.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }




}
