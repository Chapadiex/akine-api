package com.akine_api.controller;

import com.akine_api.application.dto.result.AdjuntoClinicoDownloadResult;
import com.akine_api.application.dto.result.AdjuntoClinicoResult;
import com.akine_api.application.dto.result.DiagnosticoClinicoResult;
import com.akine_api.application.dto.result.HistoriaClinicaActiveCaseSummaryResult;
import com.akine_api.application.dto.result.HistoriaClinicaAntecedenteResult;
import com.akine_api.application.dto.result.HistoriaClinicaLegajoStatusResult;
import com.akine_api.application.dto.result.HistoriaClinicaOverviewResult;
import com.akine_api.application.dto.result.HistoriaClinicaPacienteResult;
import com.akine_api.application.dto.result.HistoriaClinicaSesionSummaryResult;
import com.akine_api.application.dto.result.HistoriaClinicaTimelineEventResult;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceItem;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceResult;
import com.akine_api.application.dto.result.SesionClinicaResult;
import com.akine_api.application.service.HistoriaClinicaService;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTimelineEventType;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class HistoriaClinicaControllerTest {

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID SESION_ID = UUID.randomUUID();
    private static final UUID DIAGNOSTICO_ID = UUID.randomUUID();
    private static final UUID ADJUNTO_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired MockMvc mvc;
    @MockBean HistoriaClinicaService service;

    @Test
    void workspace_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", CONSULTORIO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVO")
    void workspace_withNonClinicalRole_returns403() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", CONSULTORIO_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void workspace_withClinicalRole_returns200() throws Exception {
        when(service.getWorkspace(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), anySet()))
                .thenReturn(workspaceResult());

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", CONSULTORIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].pacienteNombre").value("Ana"))
                .andExpect(jsonPath("$.items[0].estado").value("BORRADOR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void paciente_returns200() throws Exception {
        when(service.getPaciente(any(), any(), any(), anySet())).thenReturn(pacienteResult());

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}", CONSULTORIO_ID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PACIENTE_ID.toString()))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void overview_returns200() throws Exception {
        when(service.getOverview(any(), any(), any(), anySet())).thenReturn(overviewResult());

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/overview", CONSULTORIO_ID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legajo.exists").value(true))
                .andExpect(jsonPath("$.casosActivos[0].descripcion").value("Lumbalgia mecanica"))
                .andExpect(jsonPath("$.ultimaSesion.resumen").value("Control semanal"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createLegajo_returns201() throws Exception {
        when(service.createLegajo(any(), any(), anySet())).thenReturn(overviewResult());

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/legajo", CONSULTORIO_ID, PACIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profesionalId": "%s",
                                  "fechaAtencion": "2026-03-07T10:00:00",
                                  "motivoConsulta": "Dolor lumbar",
                                  "resumenClinico": "Evaluacion inicial",
                                  "evaluacion": "Rigidez matinal",
                                  "casoDescripcion": "Lumbalgia mecanica",
                                  "casoFechaInicio": "2026-03-01",
                                  "antecedentes": [
                                    {
                                      "label": "Alergia",
                                      "valueText": "Diclofenac",
                                      "critical": true
                                    }
                                  ]
                                }
                                """.formatted(PROFESIONAL_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.legajo.exists").value(true))
                .andExpect(jsonPath("$.antecedentesRelevantes[0].label").value("Alergia"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAntecedentes_returns200() throws Exception {
        when(service.getAntecedentes(any(), any(), any(), anySet()))
                .thenReturn(List.of(antecedenteResult()));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/antecedentes", CONSULTORIO_ID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].label").value("Alergia"))
                .andExpect(jsonPath("$[0].critical").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAntecedentes_returns200() throws Exception {
        when(service.updateAntecedentes(any(), any(), anySet()))
                .thenReturn(List.of(antecedenteResult()));

        mvc.perform(put("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/antecedentes", CONSULTORIO_ID, PACIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "antecedentes": [
                                    {
                                      "label": "Alergia",
                                      "valueText": "Diclofenac",
                                      "critical": true,
                                      "notes": "Evitar AINEs"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notes").value("Evitar AINEs"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void timeline_returns200() throws Exception {
        when(service.getTimeline(any(), any(), any(), any(), anySet()))
                .thenReturn(List.of(timelineEventResult()));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/timeline", CONSULTORIO_ID, PACIENTE_ID)
                        .param("type", "sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SESION"))
                .andExpect(jsonPath("$[0].title").value("Sesion de seguimiento"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listSesiones_returns200() throws Exception {
        when(service.listSesiones(any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), anySet()))
                .thenReturn(List.of(sesionResult(HistoriaClinicaSesionEstado.BORRADOR)));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones", CONSULTORIO_ID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(SESION_ID.toString()))
                .andExpect(jsonPath("$[0].estado").value("BORRADOR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSesion_returns200() throws Exception {
        when(service.getSesion(any(), any(), any(), any(), anySet()))
                .thenReturn(sesionResult(HistoriaClinicaSesionEstado.BORRADOR));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones/{sid}",
                        CONSULTORIO_ID, PACIENTE_ID, SESION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SESION_ID.toString()))
                .andExpect(jsonPath("$.adjuntos[0].originalFilename").value("nota.pdf"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSesion_returns201() throws Exception {
        when(service.createSesion(any(), any(), anySet()))
                .thenReturn(sesionResult(HistoriaClinicaSesionEstado.BORRADOR));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones", CONSULTORIO_ID, PACIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profesionalId": "%s",
                                  "fechaAtencion": "2026-03-07T10:00:00",
                                  "tipoAtencion": "SEGUIMIENTO",
                                  "motivoConsulta": "Motivo",
                                  "resumenClinico": "Resumen"
                                }
                                """.formatted(PROFESIONAL_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SESION_ID.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSesion_returns200() throws Exception {
        when(service.updateSesion(any(), any(), anySet()))
                .thenReturn(sesionResult(HistoriaClinicaSesionEstado.BORRADOR));

        mvc.perform(put("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones/{sid}",
                        CONSULTORIO_ID, PACIENTE_ID, SESION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profesionalId": "%s",
                                  "fechaAtencion": "2026-03-07T10:00:00",
                                  "tipoAtencion": "TRATAMIENTO",
                                  "motivoConsulta": "Motivo",
                                  "resumenClinico": "Resumen"
                                }
                                """.formatted(PROFESIONAL_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoAtencion").value("SEGUIMIENTO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void closeSesion_returns200() throws Exception {
        when(service.closeSesion(any(), any(), anySet()))
                .thenReturn(sesionResult(HistoriaClinicaSesionEstado.CERRADA));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones/{sid}/cerrar",
                        CONSULTORIO_ID, PACIENTE_ID, SESION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void annulSesion_returns200() throws Exception {
        when(service.annulSesion(any(), any(), anySet()))
                .thenReturn(sesionResult(HistoriaClinicaSesionEstado.ANULADA));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones/{sid}/anular",
                        CONSULTORIO_ID, PACIENTE_ID, SESION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listDiagnosticos_returns200() throws Exception {
        when(service.listDiagnosticos(any(), any(), any(), anySet()))
                .thenReturn(List.of(diagnosticoResult(DiagnosticoClinicoEstado.ACTIVO)));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/diagnosticos", CONSULTORIO_ID, PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DIAGNOSTICO_ID.toString()))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDiagnostico_returns201() throws Exception {
        when(service.createDiagnostico(any(), any(), anySet()))
                .thenReturn(diagnosticoResult(DiagnosticoClinicoEstado.ACTIVO));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/diagnosticos", CONSULTORIO_ID, PACIENTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profesionalId": "%s",
                                  "diagnosticoCodigo": "A01",
                                  "fechaInicio": "2026-03-01"
                                }
                                """.formatted(PROFESIONAL_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Diagnostico de prueba"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDiagnostico_returns200() throws Exception {
        when(service.updateDiagnostico(any(), any(), anySet()))
                .thenReturn(diagnosticoResult(DiagnosticoClinicoEstado.ACTIVO));

        mvc.perform(put("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/diagnosticos/{did}",
                        CONSULTORIO_ID, PACIENTE_ID, DIAGNOSTICO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "profesionalId": "%s",
                                  "diagnosticoCodigo": "A01",
                                  "fechaInicio": "2026-03-01"
                                }
                                """.formatted(PROFESIONAL_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resolveDiagnostico_returns200() throws Exception {
        when(service.resolveDiagnostico(any(), any(), anySet()))
                .thenReturn(diagnosticoResult(DiagnosticoClinicoEstado.RESUELTO));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/diagnosticos/{did}/resolver",
                        CONSULTORIO_ID, PACIENTE_ID, DIAGNOSTICO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fechaFin": "2026-03-15"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void discardDiagnostico_returns200() throws Exception {
        when(service.discardDiagnostico(any(), any(), anySet()))
                .thenReturn(diagnosticoResult(DiagnosticoClinicoEstado.DESCARTADO));

        mvc.perform(post("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/diagnosticos/{did}/descartar",
                        CONSULTORIO_ID, PACIENTE_ID, DIAGNOSTICO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fechaFin": "2026-03-15"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESCARTADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadAdjunto_returns201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nota.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test".getBytes(StandardCharsets.UTF_8)
        );
        when(service.addAdjunto(any(), any(), any(), any(), any(), anySet()))
                .thenReturn(adjuntoResult());

        mvc.perform(multipart("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/sesiones/{sid}/adjuntos",
                        CONSULTORIO_ID, PACIENTE_ID, SESION_ID)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ADJUNTO_ID.toString()))
                .andExpect(jsonPath("$.originalFilename").value("nota.pdf"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void downloadAdjunto_returnsHeadersAndBody() throws Exception {
        when(service.downloadAdjunto(any(), any(), any(), any(), anySet()))
                .thenReturn(new AdjuntoClinicoDownloadResult(
                        "nota.pdf",
                        "application/pdf",
                        4L,
                        "test".getBytes(StandardCharsets.UTF_8)
                ));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/adjuntos/{aid}",
                        CONSULTORIO_ID, PACIENTE_ID, ADJUNTO_ID))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("nota.pdf")))
                .andExpect(content().bytes("test".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdjunto_returns204() throws Exception {
        doNothing().when(service).deleteAdjunto(any(), any(), any(), any(), anySet());

        mvc.perform(delete("/api/v1/consultorios/{cid}/historia-clinica/pacientes/{pid}/adjuntos/{aid}",
                        CONSULTORIO_ID, PACIENTE_ID, ADJUNTO_ID))
                .andExpect(status().isNoContent());
    }

    private HistoriaClinicaWorkspaceResult workspaceResult() {
        return new HistoriaClinicaWorkspaceResult(
                List.of(),
                List.of(new HistoriaClinicaWorkspaceItem(
                        SESION_ID,
                        PACIENTE_ID,
                        "Ana",
                        "Perez",
                        "30111222",
                        PROFESIONAL_ID,
                        "Dr. House",
                        LocalDateTime.of(2026, 3, 7, 10, 0),
                        HistoriaClinicaSesionEstado.BORRADOR,
                        HistoriaClinicaTipoAtencion.EVALUACION,
                        "Control inicial",
                        Instant.parse("2026-03-07T10:00:00Z")
                )),
                0,
                20,
                1
        );
    }

    private HistoriaClinicaPacienteResult pacienteResult() {
        return new HistoriaClinicaPacienteResult(
                PACIENTE_ID,
                CONSULTORIO_ID,
                "30111222",
                "Ana",
                "Perez",
                "1155554444",
                "ana@mail.com",
                LocalDate.of(1990, 5, 20),
                "OSDE",
                "210",
                "12345",
                true,
                1,
                LocalDateTime.of(2026, 3, 7, 10, 0),
                Instant.parse("2026-03-07T10:00:00Z")
        );
    }

    private HistoriaClinicaOverviewResult overviewResult() {
        return new HistoriaClinicaOverviewResult(
                pacienteResult(),
                new HistoriaClinicaLegajoStatusResult(
                        true,
                        UUID.randomUUID(),
                        Instant.parse("2026-03-01T10:00:00Z"),
                        Instant.parse("2026-03-07T10:00:00Z")
                ),
                List.of("Alergia: Diclofenac"),
                List.of(antecedenteResult()),
                List.of(new HistoriaClinicaActiveCaseSummaryResult(
                        DIAGNOSTICO_ID,
                        PROFESIONAL_ID,
                        "Dr. House",
                        "A01",
                        "Lumbalgia mecanica",
                        DiagnosticoClinicoEstado.ACTIVO,
                        LocalDate.of(2026, 3, 1),
                        3,
                        "Dolor en descenso"
                )),
                List.of(),
                new HistoriaClinicaSesionSummaryResult(
                        SESION_ID,
                        PROFESIONAL_ID,
                        "Dr. House",
                        LocalDateTime.of(2026, 3, 7, 10, 0),
                        HistoriaClinicaSesionEstado.BORRADOR,
                        HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                        "Control semanal"
                ),
                List.of(adjuntoResult()),
                "Dr. House",
                null,
                null,
                null
        );
    }

    private HistoriaClinicaAntecedenteResult antecedenteResult() {
        return new HistoriaClinicaAntecedenteResult(
                UUID.randomUUID(),
                "medication",
                "diclofenac",
                "Alergia",
                "Diclofenac",
                true,
                "Evitar AINEs",
                Instant.parse("2026-03-07T10:00:00Z")
        );
    }

    private HistoriaClinicaTimelineEventResult timelineEventResult() {
        return new HistoriaClinicaTimelineEventResult(
                "sesion-1",
                HistoriaClinicaTimelineEventType.SESION,
                LocalDateTime.of(2026, 3, 7, 10, 0),
                PROFESIONAL_ID,
                "Dr. House",
                "Sesion de seguimiento",
                "Control semanal",
                "BORRADOR",
                SESION_ID
        );
    }

    private SesionClinicaResult sesionResult(HistoriaClinicaSesionEstado estado) {
        Instant now = Instant.parse("2026-03-07T10:00:00Z");
        return new SesionClinicaResult(
                SESION_ID,
                CONSULTORIO_ID,
                PACIENTE_ID,
                PROFESIONAL_ID,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 3, 7, 10, 0),
                estado,
                HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                "Motivo",
                "Resumen clinico",
                "Subjetivo",
                "Objetivo",
                "Evaluacion",
                "Plan",
                null,
                null,
                null,
                HistoriaClinicaOrigenRegistro.MANUAL,
                USER_ID,
                USER_ID,
                estado == HistoriaClinicaSesionEstado.BORRADOR ? null : USER_ID,
                now,
                now,
                estado == HistoriaClinicaSesionEstado.BORRADOR ? null : now,
                List.of(adjuntoResult())
        );
    }

    private DiagnosticoClinicoResult diagnosticoResult(DiagnosticoClinicoEstado estado) {
        return new DiagnosticoClinicoResult(
                DIAGNOSTICO_ID,
                CONSULTORIO_ID,
                PACIENTE_ID,
                PROFESIONAL_ID,
                SESION_ID,
                "A01",
                "Diagnostico de prueba",
                null,
                null,
                null,
                null,
                null,
                estado,
                LocalDate.of(2026, 3, 1),
                estado == DiagnosticoClinicoEstado.ACTIVO ? null : LocalDate.of(2026, 3, 15),
                "Notas",
                Instant.parse("2026-03-01T10:00:00Z"),
                Instant.parse("2026-03-07T10:00:00Z")
        );
    }

    private AdjuntoClinicoResult adjuntoResult() {
        return new AdjuntoClinicoResult(
                ADJUNTO_ID,
                SESION_ID,
                null,
                null,
                "nota.pdf",
                "application/pdf",
                4L,
                Instant.parse("2026-03-07T10:00:00Z")
        );
    }
}
