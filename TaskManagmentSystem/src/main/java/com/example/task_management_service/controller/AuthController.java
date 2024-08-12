package com.example.task_management_service.controller;

import com.example.task_management_service.dto.JwtAuthenticationResponse;
import com.example.task_management_service.dto.SignInRequest;
import com.example.task_management_service.dto.SignUpRequest;
import com.example.task_management_service.service.AuthenticationService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;



    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody @Valid SignUpRequest signOut)
    {
        authenticationService.signUp(signOut);
        return ResponseEntity.ok("Registration Succeeded");
    }



    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest signIn)
    {
        return authenticationService.signIn(signIn);
    }



}
