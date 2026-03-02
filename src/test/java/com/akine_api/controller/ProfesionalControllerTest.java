package com.akine_api.controller;

import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.service.ProfesionalService;
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

import org.springframework.security.access.AccessDeniedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ProfesionalControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ProfesionalService profesionalService;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROF_ID = UUID.randomUUID();

    private ProfesionalResult sampleResult() {
        return new ProfesionalResult(PROF_ID, CONSULTORIO_ID, "Juan", "Pérez",
                "MP-1234", "Kinesiología", "juan@mail.com", "1155550000",
                true, Instant.now(), Instant.now());
    }

    private String baseUrl() {
        return "/api/v1/consultorios/" + CONSULTORIO_ID + "/profesionales";
    }

    // ─── GET ─────────────────────────────────────────────────────────────────

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get(baseUrl()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_asAdmin_returns200() throws Exception {
        when(profesionalService.list(any(), any(), any())).thenReturn(List.of(sampleResult()));

        mvc.perform(get(baseUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("MP-1234"))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"apellido\":\"García\",\"matricula\":\"MP-9999\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(profesionalService.create(any(), any(), any())).thenReturn(sampleResult());

        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"matricula\":\"MP-1234\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("MP-1234"));
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFESIONAL")
    void create_asProfesional_returns403() throws Exception {
        when(profesionalService.create(any(), any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"apellido\":\"Y\",\"matricula\":\"MP-0001\"}"))
                .andExpect(status().isForbidden());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void inactivate_withoutAuth_returns401() throws Exception {
        mvc.perform(delete(baseUrl() + "/" + PROF_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void inactivate_asAdmin_returns204() throws Exception {
        doNothing().when(profesionalService).inactivate(any(), any(), any(), any());

        mvc.perform(delete(baseUrl() + "/" + PROF_ID))
                .andExpect(status().isNoContent());
    }
}
