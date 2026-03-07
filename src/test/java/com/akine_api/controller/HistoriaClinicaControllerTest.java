package com.akine_api.controller;

import com.akine_api.application.dto.result.AdjuntoClinicoDownloadResult;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceItem;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceResult;
import com.akine_api.application.service.HistoriaClinicaService;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class HistoriaClinicaControllerTest {

    @Autowired MockMvc mvc;
    @MockBean HistoriaClinicaService service;

    @Test
    void workspace_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVO")
    void workspace_withNonClinicalRole_returns403() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void workspace_withClinicalRole_returns200() throws Exception {
        when(service.getWorkspace(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), anySet()))
                .thenReturn(new HistoriaClinicaWorkspaceResult(
                        List.of(),
                        List.of(new HistoriaClinicaWorkspaceItem(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "Ana",
                                "Perez",
                                "30111222",
                                UUID.randomUUID(),
                                "Dr. House",
                                LocalDateTime.now(),
                                HistoriaClinicaSesionEstado.BORRADOR,
                                HistoriaClinicaTipoAtencion.EVALUACION,
                                "Control inicial",
                                Instant.now()
                        )),
                        0,
                        20,
                        1
                ));

        mvc.perform(get("/api/v1/consultorios/{cid}/historia-clinica/workspace", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].pacienteNombre").value("Ana"))
                .andExpect(jsonPath("$.items[0].estado").value("BORRADOR"));
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
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("nota.pdf")))
                .andExpect(content().bytes("test".getBytes(StandardCharsets.UTF_8)));
    }
}
