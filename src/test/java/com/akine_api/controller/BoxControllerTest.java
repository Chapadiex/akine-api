package com.akine_api.controller;

import com.akine_api.application.dto.result.BoxResult;
import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.application.service.BoxService;
import com.akine_api.domain.model.BoxTipo;
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
class BoxControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BoxService boxService;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID BOX_ID = UUID.randomUUID();

    private BoxResult sampleResult() {
        return new BoxResult(BOX_ID, CONSULTORIO_ID, "Box 1", "B01",
                BoxTipo.BOX, BoxCapacidadTipo.UNLIMITED, null, true, Instant.now(), Instant.now());
    }

    private String baseUrl() {
        return "/api/v1/consultorios/" + CONSULTORIO_ID + "/boxes";
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
        when(boxService.list(any(), any(), any())).thenReturn(List.of(sampleResult()));

        mvc.perform(get(baseUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Box 1"))
                .andExpect(jsonPath("$[0].tipo").value("BOX"));
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Box 2\",\"tipo\":\"BOX\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(boxService.create(any(), any(), any())).thenReturn(sampleResult());

        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Box 1\",\"codigo\":\"B01\",\"tipo\":\"BOX\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Box 1"));
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFESIONAL")
    void create_asProfesional_returns403() throws Exception {
        when(boxService.create(any(), any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(post(baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Box X\",\"tipo\":\"BOX\"}"))
                .andExpect(status().isForbidden());
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void inactivate_withoutAuth_returns401() throws Exception {
        mvc.perform(delete(baseUrl() + "/" + BOX_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void inactivate_asAdmin_returns204() throws Exception {
        doNothing().when(boxService).inactivate(any(), any(), any(), any());

        mvc.perform(delete(baseUrl() + "/" + BOX_ID))
                .andExpect(status().isNoContent());
    }
}
