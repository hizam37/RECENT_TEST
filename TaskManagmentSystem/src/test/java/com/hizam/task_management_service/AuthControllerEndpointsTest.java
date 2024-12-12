package com.hizam.task_management_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.dto.SignInRequest;
import com.hizam.task_management_service.dto.SignUpRequest;
import com.hizam.task_management_service.exception.RegistrationException;
import com.hizam.task_management_service.model.Role;
import com.hizam.task_management_service.model.User;
import com.hizam.task_management_service.security.config.SecurityConfiguration;
import com.hizam.task_management_service.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
public class AuthControllerEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Registering user test")
    public void registerTest() throws Exception {
        User user = new User();
        user.setEmail("dan@gmail.com");
        user.setPassword("123");
        user.setRole(Role.ROLE_ADMIN);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setPassword(user.getPassword());
        doNothing().when(authenticationService).signUp(signUpRequest);
        mockMvc.perform(post("/api/v1/auth/register")
                        .content(asJsonString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Registering user with similar email test")
    public void registerWithSimilarEmailShouldThrowException() throws Exception {
        User user = new User();
        user.setEmail("dan@gmail.com");
        user.setPassword("123");
        user.setRole(Role.ROLE_ADMIN);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setPassword(user.getPassword());
        doThrow(new RegistrationException("This email already exist")).when(authenticationService).signUp(any(SignUpRequest.class));
        mockMvc.perform(post("/api/v1/auth/register")
                        .content(asJsonString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Testing Login for a valid user")
    public void loginWithValidUser() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("dan@gmail.com");
        signInRequest.setPassword("123");
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sImlkIjoiZDc2MWU3YWItMGFiNy00MTM5LTk0ZjktOWJhYzUxZDY0MWFmIiwiZW1haWwiOiJHT09GWUBnbWFpbC5jb20iLCJzdWIiOiJHT09GWUBnbWFpbC5jb20iLCJpYXQiOjE3MjUwMTU5NjMsImV4cCI6MTcyNTE1OTk2M30.Hp0XgHrVTmHBLO9r42uMyHCQ-5mMPfEBvjyHejVpoGQ";
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setAccessToken(token);
        when(authenticationService.signIn(any(SignInRequest.class), any(HttpServletResponse.class))).thenReturn(jwtAuthenticationResponse);
        mockMvc.perform(post("/api/v1/auth/login")
                .content(asJsonString(signInRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(jwtAuthenticationResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print());

    }
        public static String asJsonString ( final Object object)
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                return mapper.writeValueAsString(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }
