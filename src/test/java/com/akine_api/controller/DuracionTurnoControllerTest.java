package com.akine_api.controller;

import com.akine_api.application.dto.result.ConsultorioDuracionTurnoResult;
import com.akine_api.application.service.ConsultorioDuracionTurnoService;
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
class DuracionTurnoControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ConsultorioDuracionTurnoService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @Test
    void add_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/duraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minutos\":30}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void add_asAdmin_returns201() throws Exception {
        when(service.add(any(), any(), any()))
                .thenReturn(new ConsultorioDuracionTurnoResult(UUID.randomUUID(), CONSULTORIO_ID, 30));
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/duraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"minutos\":30}"))
                .andExpect(status().isCreated());
    }
}
