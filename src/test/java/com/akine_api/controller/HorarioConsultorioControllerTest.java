package com.akine_api.controller;

import com.akine_api.application.dto.result.ConsultorioHorarioResult;
import com.akine_api.application.service.ConsultorioHorarioService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class HorarioConsultorioControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ConsultorioHorarioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/horarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_asAdmin_returns200() throws Exception {
        when(service.list(any(), any(), any())).thenReturn(List.of(
                new ConsultorioHorarioResult(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(8, 0), LocalTime.of(18, 0), true)
        ));
        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diaSemana").value("MONDAY"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void upsert_returns200() throws Exception {
        when(service.set(any(), any(), any())).thenReturn(
                new ConsultorioHorarioResult(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(8, 0), LocalTime.of(18, 0), true)
        );
        mvc.perform(put("/api/v1/consultorios/" + CONSULTORIO_ID + "/horarios/MONDAY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diaSemana\":\"MONDAY\",\"horaApertura\":\"08:00\",\"horaCierre\":\"18:00\"}"))
                .andExpect(status().isOk());
    }
}
