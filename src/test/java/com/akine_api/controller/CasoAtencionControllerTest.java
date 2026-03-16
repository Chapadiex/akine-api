package com.akine_api.controller;

import com.akine_api.application.dto.result.CasoAtencionResult;
import com.akine_api.application.dto.result.CasoAtencionSummaryResult;
import com.akine_api.application.service.CasoAtencionService;
import com.akine_api.domain.exception.CasoAtencionNotFoundException;
import com.akine_api.domain.model.CasoAtencionEstado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CasoAtencionControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CasoAtencionService service;

    private static final UUID CID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    private static final UUID LEGAJO_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
    private static final UUID CASO_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000003");
    private static final UUID PACIENTE_ID = UUID.fromString("dddddddd-0000-0000-0000-000000000004");

    private CasoAtencionResult sampleResult() {
        return new CasoAtencionResult(
                CASO_ID, LEGAJO_ID, CID, PACIENTE_ID,
                null, null,
                "CONSULTA_DIRECTA",
                LocalDateTime.of(2026, 3, 15, 10, 0),
                "Dolor lumbar", null, null, null, null,
                CasoAtencionEstado.BORRADOR, "NORMAL",
                null, 0, 0,
                Instant.now(), Instant.now()
        );
    }

    private CasoAtencionSummaryResult sampleSummary() {
        return new CasoAtencionSummaryResult(
                CASO_ID, LEGAJO_ID, PACIENTE_ID,
                null, null,
                "CONSULTA_DIRECTA",
                LocalDateTime.of(2026, 3, 15, 10, 0),
                "Dolor lumbar", null, null,
                CasoAtencionEstado.BORRADOR, "NORMAL",
                0, 0
        );
    }

    // ── create ──────────────────────────────────────────────────────────────

    @Test
    void create_withoutAuth_returns401() throws Exception {
        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/legajos/{lid}/casos", CID, LEGAJO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":"%s","motivoConsulta":"Dolor lumbar"}
                                """.formatted(PACIENTE_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void create_ok_returns201() throws Exception {
        when(service.createCasoAtencion(eq(CID), any(), anyString(), anySet()))
                .thenReturn(sampleResult());

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/legajos/{lid}/casos", CID, LEGAJO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":"%s","motivoConsulta":"Dolor lumbar","tipoOrigen":"CONSULTA_DIRECTA"}
                                """.formatted(PACIENTE_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.motivoConsulta").value("Dolor lumbar"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void create_accessDenied_returns403() throws Exception {
        when(service.createCasoAtencion(eq(CID), any(), anyString(), anySet()))
                .thenThrow(new AccessDeniedException("Sin acceso"));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/legajos/{lid}/casos", CID, LEGAJO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pacienteId":"%s","motivoConsulta":"Dolor lumbar"}
                                """.formatted(PACIENTE_ID)))
                .andExpect(status().isForbidden());
    }

    // ── list by legajo ───────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void listByLegajo_ok_returns200() throws Exception {
        when(service.getCasosPorLegajo(eq(LEGAJO_ID), eq(CID), anyString(), anySet()))
                .thenReturn(List.of(sampleSummary()));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/legajos/{lid}/casos", CID, LEGAJO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("BORRADOR"))
                .andExpect(jsonPath("$[0].motivoConsulta").value("Dolor lumbar"));
    }

    // ── get by id ───────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void getById_ok_returns200() throws Exception {
        when(service.getCasoAtencion(eq(CASO_ID), eq(CID), anyString(), anySet()))
                .thenReturn(sampleResult());

        mvc.perform(get("/api/v1/consultorios/{cid}/casos-atencion/{id}", CID, CASO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CASO_ID.toString()));
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void getById_notFound_returns404() throws Exception {
        when(service.getCasoAtencion(eq(CASO_ID), eq(CID), anyString(), anySet()))
                .thenThrow(new CasoAtencionNotFoundException(CASO_ID));

        mvc.perform(get("/api/v1/consultorios/{cid}/casos-atencion/{id}", CID, CASO_ID))
                .andExpect(status().isNotFound());
    }

    // ── cambiar estado ───────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void cambiarEstado_ok_returns200() throws Exception {
        CasoAtencionResult activoResult = new CasoAtencionResult(
                CASO_ID, LEGAJO_ID, CID, PACIENTE_ID,
                null, null,
                "CONSULTA_DIRECTA",
                LocalDateTime.of(2026, 3, 15, 10, 0),
                "Dolor lumbar", null, null, null, null,
                CasoAtencionEstado.ACTIVO, "NORMAL",
                null, 0, 0,
                Instant.now(), Instant.now()
        );
        when(service.cambiarEstado(eq(CASO_ID), eq(CID), any(), anyString(), anySet()))
                .thenReturn(activoResult);

        mvc.perform(patch("/api/v1/consultorios/{cid}/casos-atencion/{id}/estado", CID, CASO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nuevoEstado":"ACTIVO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void cambiarEstado_invalidBody_returns400() throws Exception {
        mvc.perform(patch("/api/v1/consultorios/{cid}/casos-atencion/{id}/estado", CID, CASO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── casos activos por paciente ───────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PROFESIONAL")
    void casosActivosPorPaciente_ok_returns200() throws Exception {
        when(service.getCasosActivosPorPaciente(eq(PACIENTE_ID), eq(CID), anyString(), anySet()))
                .thenReturn(List.of(sampleSummary()));

        mvc.perform(get("/api/v1/consultorios/{cid}/pacientes/{pid}/casos-activos", CID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
