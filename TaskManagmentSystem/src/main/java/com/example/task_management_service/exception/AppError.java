package com.example.task_management_service.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AppError {
    private int httpStatus;
    private String message;

    public AppError(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
