package com.akine_api.controller;

import com.akine_api.application.dto.result.ProfesionalConsultorioResult;
import com.akine_api.application.service.ProfesionalConsultorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AsignacionControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ProfesionalConsultorioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();

    @Test
    void asignar_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profesionalId\":\"" + PROFESIONAL_ID + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void asignar_asAdmin_returns201() throws Exception {
        when(service.asignar(any(), any(), any())).thenReturn(
                new ProfesionalConsultorioResult(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, "N", "A", true)
        );
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/asignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profesionalId\":\"" + PROFESIONAL_ID + "\"}"))
                .andExpect(status().isCreated());
    }
}
