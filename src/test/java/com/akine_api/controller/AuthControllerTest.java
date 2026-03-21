package com.akine_api.controller;

import com.akine_api.application.dto.result.AuthResult;
import java.util.Map;
import com.akine_api.application.service.AuthenticationService;
import com.akine_api.application.service.UserRegistrationService;
import com.akine_api.domain.exception.InvalidCredentialsException;
import com.akine_api.domain.exception.UserAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserRegistrationService registrationService;
    @MockBean AuthenticationService authService;

    @Test
    void registerPatient_validRequest_returns201() throws Exception {
        doNothing().when(registrationService).registerPatient(any());

        mvc.perform(post("/api/v1/auth/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"p@test.com","password":"Pass1234!",
                     "firstName":"Ana","lastName":"Lopez"}
                    """))
                .andExpect(status().isCreated());
    }

    @Test
    void registerPatient_duplicateEmail_returns409() throws Exception {
        doThrow(new UserAlreadyExistsException("p@test.com"))
                .when(registrationService).registerPatient(any());

        mvc.perform(post("/api/v1/auth/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"p@test.com","password":"Pass1234!",
                     "firstName":"Ana","lastName":"Lopez"}
                    """))
                .andExpect(status().isConflict());
    }

    @Test
    void registerPatient_invalidEmail_returns422() throws Exception {
        mvc.perform(post("/api/v1/auth/register/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"not-an-email","password":"Pass1234!",
                     "firstName":"Ana","lastName":"Lopez"}
                    """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void login_validCredentials_returns200WithTokens() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthResult mockResult = new AuthResult(
                "access-token", "refresh-token", 900000L,
                userId,
                "u@test.com",
                "Carlos",
                "G",
                List.of("PACIENTE"),
                "ACTIVE",
                "PACIENTE",
                List.of("PACIENTE"),
                List.of(),
                null,
                Map.of()
        );
        when(authService.login(any())).thenReturn(mockResult);

        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"u@test.com","password":"Pass1234!"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("u@test.com"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any())).thenThrow(new InvalidCredentialsException());

        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"u@test.com","password":"wrong"}
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingFields_returns422() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
