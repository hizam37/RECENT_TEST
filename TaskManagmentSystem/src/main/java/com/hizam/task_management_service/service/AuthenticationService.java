package com.hizam.task_management_service.service;

import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.dto.SignInRequest;
import com.hizam.task_management_service.dto.SignUpRequest;
import com.hizam.task_management_service.model.Role;
import com.hizam.task_management_service.security.service.JwtService;
import com.hizam.task_management_service.service.implementations.AuthenticationServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.hizam.task_management_service.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceImpl {

    private final ReferenceTokenService referenceTokenService;

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    @Override
    public void signUp(SignUpRequest signUpRequest) {

        var user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.valueOf(signUpRequest.getRole())).build();

        userService.createUser(user);
    }

    @Override
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest, HttpServletResponse response)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));

        var user = userService.getByEmail(signInRequest.getEmail());

        var jwt = jwtService.generateToken(user);

        var refreshToken = referenceTokenService.generateReferenceTokenById(user.getId());

        Cookie cookie = new Cookie("accessToken", jwt);

        Cookie cookie2 = new Cookie("ReferenceToken", refreshToken.getReferenceToken());


        cookie.setHttpOnly(true);

        cookie.setSecure(true);

        response.addCookie(cookie);
        response.addCookie(cookie2);


        return new JwtAuthenticationResponse(jwt);
    }

}
