package com.hizam.task_management_service;

import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.dto.SignInRequest;
import com.hizam.task_management_service.dto.SignUpRequest;
import com.hizam.task_management_service.model.ReferenceToken;
import com.hizam.task_management_service.model.Role;
import com.hizam.task_management_service.model.User;
import com.hizam.task_management_service.repository.ReferenceTokenRepository;
import com.hizam.task_management_service.repository.UserRepository;
import com.hizam.task_management_service.security.config.SecurityConfiguration;
import com.hizam.task_management_service.security.service.JwtService;
import com.hizam.task_management_service.service.AuthenticationService;
import com.hizam.task_management_service.service.ReferenceTokenService;
import com.hizam.task_management_service.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
public class AuthenticationServiceTest {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ReferenceTokenRepository referenceTokenRepository;


    @Test
    @Description("Testing signUp")
    public void signUpTest()
    {

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("dan@gmail.com");
        signUpRequest.setPassword(passwordEncoder.encode("123"));
        signUpRequest.setRole(String.valueOf(Role.ROLE_ADMIN));
        var user = User.builder()
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .role(Role.valueOf(signUpRequest.getRole()))
                .build();
        when(userRepository.save(user)).thenReturn(user);
        authenticationService.signUp(signUpRequest);
        assertEquals(user.getEmail(),signUpRequest.getEmail());

    }

    @Test
    @Description("Testing signIn")
    public void signInTest(){
        HttpServletResponse response = mock(HttpServletResponse.class);
        var user = User.builder()
                .id(1L)
                .email("dan@gmail.com")
                .password(passwordEncoder.encode("123"))
                .role(Role.ROLE_ADMIN)
                .build();
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("dan@gmail.com");
        signInRequest.setPassword("123");
        when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(Optional.of(user));
        var jwt = jwtService.generateToken(user);
        ReferenceToken referenceToken = new ReferenceToken();
        referenceToken.setPerformerId(user.getId());
        referenceToken.setReferenceToken("1231242-3DSAFGWT-GFDGASDFGS-HWGRWER3RRF");
        when(referenceTokenRepository.findByReferenceToken(referenceToken.getReferenceToken())).thenReturn(referenceToken);
        Cookie cookie1 = new Cookie("Access_token",jwt);
        Cookie cookie2 = new Cookie("Reference_Token",referenceToken.getReferenceToken());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signIn(signInRequest,response);
        assertThat(jwtAuthenticationResponse).isNotNull();

    }



}
