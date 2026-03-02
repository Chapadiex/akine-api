package com.akine_api.controller;

import com.akine_api.application.dto.result.PacienteResult;
import com.akine_api.application.dto.result.PacienteSearchResult;
import com.akine_api.application.service.PacienteService;
import com.akine_api.domain.exception.PacienteDuplicadoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class PacienteControllerTest {

    @Autowired MockMvc mvc;
    @MockBean PacienteService service;

    private PacienteResult sampleResult() {
        return new PacienteResult(
                UUID.randomUUID(),
                "30111222",
                "Ana",
                "Perez",
                "1155554444",
                "ana@mail.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                UUID.randomUUID(),
                true,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createMe_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/pacientes/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"30111222","nombre":"Ana","apellido":"Perez","telefono":"1155554444"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void createMe_ok_returns201() throws Exception {
        when(service.createMe(any(), any(), anySet())).thenReturn(sampleResult());

        mvc.perform(post("/api/v1/pacientes/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"30111222","nombre":"Ana","apellido":"Perez","telefono":"1155554444","email":"ana@mail.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("30111222"));
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void createMe_invalid_returns422() throws Exception {
        mvc.perform(post("/api/v1/pacientes/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"ABC","nombre":"","apellido":"Perez","telefono":""}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void createMe_duplicate_returns409() throws Exception {
        when(service.createMe(any(), any(), anySet())).thenThrow(new PacienteDuplicadoException("Paciente ya registrado"));

        mvc.perform(post("/api/v1/pacientes/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"30111222","nombre":"Ana","apellido":"Perez","telefono":"1155554444"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVO")
    void search_ok_returns200() throws Exception {
        when(service.search(any(), any(), any(), any(), anySet()))
                .thenReturn(List.of(new PacienteSearchResult(
                        UUID.randomUUID(),
                        "30111222",
                        "Ana",
                        "Perez",
                        "1155554444",
                        "ana@mail.com",
                        true,
                        true
                )));

        mvc.perform(get("/api/v1/pacientes/search")
                        .param("consultorioId", UUID.randomUUID().toString())
                        .param("dni", "30111222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].linkedToConsultorio").value(true));
    }
}
