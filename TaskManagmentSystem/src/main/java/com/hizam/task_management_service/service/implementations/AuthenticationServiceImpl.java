package com.hizam.task_management_service.service.implementations;

import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.dto.SignInRequest;
import com.hizam.task_management_service.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationServiceImpl {


    void signUp(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signIn(SignInRequest signInRequest, HttpServletResponse response);

}
