package com.akine_api.controller;

import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.service.ConsultorioService;
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
class ConsultorioControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ConsultorioService consultorioService;

    private static final UUID ID = UUID.randomUUID();

    private ConsultorioResult sampleResult() {
        return new ConsultorioResult(ID, "Test Consultorio", null,
                "Av. 123", "1155550000", "test@mail.com", null, null, null, "ACTIVE",
                Instant.now(), Instant.now());
    }

    // ─── GET /consultorios ────────────────────────────────────────────────────

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_asAdmin_returns200() throws Exception {
        when(consultorioService.list(any(), any())).thenReturn(List.of(sampleResult()));

        mvc.perform(get("/api/v1/consultorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Consultorio"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // ─── POST /consultorios ───────────────────────────────────────────────────

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nuevo\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(consultorioService.create(any(), any())).thenReturn(sampleResult());

        mvc.perform(post("/api/v1/consultorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Consultorio\",\"address\":\"Av. 123\",\"phone\":\"1155550000\",\"email\":\"test@mail.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Consultorio"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidLatitude_returns400() throws Exception {
        mvc.perform(post("/api/v1/consultorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Consultorio","mapLatitude":120}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidLongitude_returns400() throws Exception {
        mvc.perform(post("/api/v1/consultorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Consultorio","mapLongitude":-190}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL_ADMIN")
    void create_asProfAdmin_returns403() throws Exception {
        when(consultorioService.create(any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(post("/api/v1/consultorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nuevo\",\"address\":\"Av. 1\",\"phone\":\"1155550000\",\"email\":\"x@x.com\"}"))
                .andExpect(status().isForbidden());
    }

    // ─── GET /consultorios/{id} ───────────────────────────────────────────────

    @Test
    void getById_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{id}", ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_asAdmin_returns200() throws Exception {
        when(consultorioService.getById(any(), any(), any())).thenReturn(sampleResult());

        mvc.perform(get("/api/v1/consultorios/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()));
    }

    // ─── DELETE /consultorios/{id} ────────────────────────────────────────────

    @Test
    void inactivate_withoutAuth_returns401() throws Exception {
        mvc.perform(delete("/api/v1/consultorios/{id}", ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void inactivate_asAdmin_returns204() throws Exception {
        doNothing().when(consultorioService).inactivate(any(), any(), any());

        mvc.perform(delete("/api/v1/consultorios/{id}", ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void activate_withoutAuth_returns401() throws Exception {
        mvc.perform(patch("/api/v1/consultorios/{id}/activar", ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activate_asAdmin_returns200() throws Exception {
        when(consultorioService.activate(any(), any())).thenReturn(sampleResult());

        mvc.perform(patch("/api/v1/consultorios/{id}/activar", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()));
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL_ADMIN")
    void activate_asProfAdmin_returns403() throws Exception {
        when(consultorioService.activate(any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(patch("/api/v1/consultorios/{id}/activar", ID))
                .andExpect(status().isForbidden());
    }
}
