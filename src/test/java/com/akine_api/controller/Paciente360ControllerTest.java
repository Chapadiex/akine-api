package com.akine_api.controller;

import com.akine_api.application.dto.result.Paciente360HeaderResult;
import com.akine_api.application.dto.result.Paciente360PagosResult;
import com.akine_api.application.service.Paciente360Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class Paciente360ControllerTest {

    @Autowired MockMvc mvc;
    @MockBean Paciente360Service service;

    @Test
    void header_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{cid}/pacientes/{pid}/360/header",
                        UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void header_ok_returns200() throws Exception {
        when(service.getHeader(any(), any(), any(), anySet())).thenReturn(new Paciente360HeaderResult(
                UUID.randomUUID(),
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
                true,
                false,
                "Sin cobertura registrada",
                Instant.now(),
                Instant.now()
        ));

        mvc.perform(get("/api/v1/consultorios/{cid}/pacientes/{pid}/360/header",
                        UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("30111222"))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pagos_ok_returns200() throws Exception {
        when(service.getPagos(any(), any(), anyInt(), anyInt(), any(), anySet())).thenReturn(
                new Paciente360PagosResult(
                        new Paciente360PagosResult.Summary(
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                null,
                                BigDecimal.ZERO
                        ),
                        List.of(),
                        List.of(new Paciente360PagosResult.ConciliationItem("ok", "OK", "Sin movimientos")),
                        0,
                        20,
                        0
                )
        );

        mvc.perform(get("/api/v1/consultorios/{cid}/pacientes/{pid}/360/pagos",
                        UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.saldoPendiente").value(0))
                .andExpect(jsonPath("$.conciliacion[0].estado").value("OK"));
    }
}
