package com.akine_api.controller;

import com.akine_api.application.dto.result.DisponibilidadProfesionalResult;
import com.akine_api.application.service.DisponibilidadProfesionalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class DisponibilidadControllerTest {

    @Autowired MockMvc mvc;
    @MockBean DisponibilidadProfesionalService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/profesionales/" + PROFESIONAL_ID + "/disponibilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diaSemana\":\"MONDAY\",\"horaInicio\":\"09:00\",\"horaFin\":\"12:00\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(service.create(any(), any(), any()))
                .thenReturn(new DisponibilidadProfesionalResult(
                        UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(9, 0), LocalTime.of(12, 0), true));
        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/profesionales/" + PROFESIONAL_ID + "/disponibilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diaSemana\":\"MONDAY\",\"horaInicio\":\"09:00\",\"horaFin\":\"12:00\"}"))
                .andExpect(status().isCreated());
    }
}
