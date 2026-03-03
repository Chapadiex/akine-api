package com.akine_api.controller;

import com.akine_api.application.dto.result.SlotDisponibleResult;
import com.akine_api.application.dto.result.TurnoResult;
import com.akine_api.application.service.TurnoService;
import com.akine_api.domain.model.TurnoEstado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TurnoControllerTest {

    @Autowired MockMvc mvc;
    @MockBean TurnoService service;

    private static final UUID CID = UUID.randomUUID();
    private static final UUID TURNO_ID = UUID.randomUUID();
    private static final UUID PROF_ID = UUID.randomUUID();

    private TurnoResult sampleResult() {
        return sampleResult(TurnoEstado.PROGRAMADO,
                LocalDateTime.of(2026, 3, 9, 10, 0),
                LocalDateTime.of(2026, 3, 9, 10, 30));
    }

    private TurnoResult sampleResult(TurnoEstado estado, LocalDateTime inicio, LocalDateTime fin) {
        return new TurnoResult(
                TURNO_ID, CID, PROF_ID, "Juan", "Perez",
                null, null, null, null, null, null,
                inicio, fin,
                30, estado, null,
                "Consulta", null,
                null, null, null,
                Instant.now(), Instant.now()
        );
    }

    // ── list ────────────────────────────────────────────────────────

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/" + CID + "/turnos")
                        .param("from", "2026-03-09T00:00:00")
                        .param("to", "2026-03-10T00:00:00"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_asAdmin_returns200() throws Exception {
        when(service.listByRange(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(sampleResult()));

        mvc.perform(get("/api/v1/consultorios/" + CID + "/turnos")
                        .param("from", "2026-03-09T00:00:00")
                        .param("to", "2026-03-10T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TURNO_ID.toString()));
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios/" + CID + "/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"profesionalId":"%s","fechaHoraInicio":"2026-03-09T10:00:00","duracionMinutos":30}
                                """.formatted(PROF_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        when(service.create(any(), any(), any())).thenReturn(sampleResult());

        mvc.perform(post("/api/v1/consultorios/" + CID + "/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"profesionalId":"%s","fechaHoraInicio":"2026-03-09T10:00:00","duracionMinutos":30}
                                """.formatted(PROF_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_invalidBody_returns422() throws Exception {
        mvc.perform(post("/api/v1/consultorios/" + CID + "/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── reprogramar ─────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void reprogramar_asAdmin_returns200() throws Exception {
        TurnoResult moved = sampleResult(TurnoEstado.PROGRAMADO,
                LocalDateTime.of(2026, 3, 9, 14, 0),
                LocalDateTime.of(2026, 3, 9, 14, 30));
        when(service.reprogramar(any(), any(), any(), any())).thenReturn(moved);

        mvc.perform(patch("/api/v1/consultorios/" + CID + "/turnos/" + TURNO_ID + "/reprogramar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nuevaFechaHoraInicio":"2026-03-09T14:00:00"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaHoraInicio").value("2026-03-09T14:00:00"));
    }

    // ── cambiarEstado ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void cambiarEstado_asAdmin_returns200() throws Exception {
        TurnoResult confirmed = sampleResult(TurnoEstado.CONFIRMADO,
                LocalDateTime.of(2026, 3, 9, 10, 0),
                LocalDateTime.of(2026, 3, 9, 10, 30));
        when(service.cambiarEstado(any(), any(), any(), any())).thenReturn(confirmed);

        mvc.perform(patch("/api/v1/consultorios/" + CID + "/turnos/" + TURNO_ID + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nuevoEstado":"CONFIRMADO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    // ── disponibilidad ──────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void disponibilidad_asAdmin_returns200() throws Exception {
        when(service.getDisponibilidad(any(), any(), any(), eq(30), any(), any()))
                .thenReturn(List.of(
                        new SlotDisponibleResult(
                                LocalDateTime.of(2026, 3, 9, 9, 0),
                                LocalDateTime.of(2026, 3, 9, 9, 30))
                ));

        mvc.perform(get("/api/v1/consultorios/" + CID + "/turnos/disponibilidad")
                        .param("date", "2026-03-09")
                        .param("profesionalId", PROF_ID.toString())
                        .param("duracion", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inicio").value("2026-03-09T09:00:00"));
    }
}
