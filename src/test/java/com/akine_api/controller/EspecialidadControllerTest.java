package com.akine_api.controller;

import com.akine_api.application.dto.result.EspecialidadResult;
import com.akine_api.application.service.ConsultorioEspecialidadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class EspecialidadControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ConsultorioEspecialidadService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID ESPECIALIDAD_ID = UUID.randomUUID();

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_asAdmin_returns200() throws Exception {
        when(service.list(any(), any(), any(Boolean.class), any(), any()))
                .thenReturn(List.of(sampleResult()));

        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(service.createOrLink(any(), any(), any(), any()))
                .thenReturn(sampleResult());

        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades")
                        .contentType(APPLICATION_JSON)
                        .content("{\"nombre\":\"Kinesiologia\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void create_asProfesional_returns403() throws Exception {
        when(service.createOrLink(any(), any(), any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades")
                        .contentType(APPLICATION_JSON)
                        .content("{\"nombre\":\"Kinesiologia\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_asAdmin_returns200() throws Exception {
        when(service.update(any(), any(), any(), any(), any()))
                .thenReturn(sampleResult());

        mvc.perform(put("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades/" + ESPECIALIDAD_ID)
                        .contentType(APPLICATION_JSON)
                        .content("{\"nombre\":\"Fisiatria\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void patch_asProfesional_returns403() throws Exception {
        when(service.activate(any(), any(), any(), any()))
                .thenThrow(new AccessDeniedException("forbidden"));

        mvc.perform(patch("/api/v1/consultorios/" + CONSULTORIO_ID + "/especialidades/" + ESPECIALIDAD_ID + "/activar")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private EspecialidadResult sampleResult() {
        return new EspecialidadResult(
                ESPECIALIDAD_ID,
                CONSULTORIO_ID,
                "Kinesiologia",
                "kinesiologia",
                true,
                Instant.now(),
                Instant.now()
        );
    }
}
