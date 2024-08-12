package com.example.task_management_service.dto;


import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
