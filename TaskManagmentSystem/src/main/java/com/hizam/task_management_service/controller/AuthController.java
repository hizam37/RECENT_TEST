package com.hizam.task_management_service.controller;

import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.dto.SignInRequest;
import com.hizam.task_management_service.dto.SignUpRequest;
import com.hizam.task_management_service.exception.RegistrationException;
import com.hizam.task_management_service.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * This class provides endpoints used for registering and authenticating the user
 *
 * @author Daniil Hizam
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication api", description = "This class provides endpoints used for registering and authenticating the user")
public class AuthController {

    private final AuthenticationService authenticationService;


    /**
     * Registers the user
     *
     * @param signOut carries the user's details
     * @throws RegistrationException if email already exists
     */


    @Operation(summary = "Registers the user", parameters = {@Parameter(name = "signOut", description = "carries the user's details")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JwtAuthenticationResponse.class))),@ApiResponse(responseCode = "400",description = "if email already exists")
    })
    @PostMapping("/register")
    public void Register(@RequestBody @Valid SignUpRequest signOut) {
        authenticationService.signUp(signOut);
    }

    /**
     * Logins the user
     *
     * @param signIn   uses the registered data of the registered user
     * @param response saves the accessToken and referenceToken inside the cookies automatically
     * @return accessToken
     */

    @Operation(summary = "Logins the user", parameters = {@Parameter(name = "signIn", description = "uses the registered data of the registered user"),
            @Parameter(name = "response", description = "saves the accessToken and referenceToken inside the cookies automatically")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JwtAuthenticationResponse.class)))})
    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest signIn, HttpServletResponse response) {
        return authenticationService.signIn(signIn, response);
    }


}
