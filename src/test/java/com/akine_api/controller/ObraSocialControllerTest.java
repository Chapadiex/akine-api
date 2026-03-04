package com.akine_api.controller;

import com.akine_api.application.dto.result.ObraSocialDetailResult;
import com.akine_api.application.dto.result.ObraSocialListItemResult;
import com.akine_api.application.dto.result.PagedResult;
import com.akine_api.application.dto.result.PlanResult;
import com.akine_api.application.service.ObraSocialService;
import com.akine_api.domain.exception.ObraSocialConflictException;
import com.akine_api.domain.model.ObraSocialEstado;
import com.akine_api.domain.model.TipoCobertura;
import com.akine_api.domain.model.TipoCoseguro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ObraSocialControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ObraSocialService obraSocialService;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID OBRA_SOCIAL_ID = UUID.randomUUID();

    @Test
    void list_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/{id}/obras-sociales", CONSULTORIO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_withAuth_returns200() throws Exception {
        when(obraSocialService.list(any(), any(), any(), any(), anyInt(), anyInt(), any(), any(Set.class)))
                .thenReturn(new PagedResult<>(List.of(new ObraSocialListItemResult(
                        OBRA_SOCIAL_ID, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                        "osde@test.com", "123", "Rep", ObraSocialEstado.ACTIVE,
                        1, true, Instant.now(), Instant.now()
                )), 0, 20, 1));

        mvc.perform(get("/api/v1/consultorios/{id}/obras-sociales", CONSULTORIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].acronimo").value("OSDE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withValidPayload_returns201() throws Exception {
        when(obraSocialService.create(any(), any(), any(Set.class))).thenReturn(sampleDetail());

        mvc.perform(post("/api/v1/consultorios/{id}/obras-sociales", CONSULTORIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(OBRA_SOCIAL_ID.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withConflict_returns409() throws Exception {
        when(obraSocialService.create(any(), any(), any(Set.class)))
                .thenThrow(new ObraSocialConflictException("dup"));

        mvc.perform(post("/api/v1/consultorios/{id}/obras-sociales", CONSULTORIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidPayload_returns422() throws Exception {
        mvc.perform(post("/api/v1/consultorios/{id}/obras-sociales", CONSULTORIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"acronimo\":\"A\",\"nombreCompleto\":\"\",\"planes\":[]}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEstado_returns200() throws Exception {
        when(obraSocialService.changeEstado(any(), any(), any(Set.class))).thenReturn(sampleDetail());

        mvc.perform(patch("/api/v1/consultorios/{cId}/obras-sociales/{osId}/estado", CONSULTORIO_ID, OBRA_SOCIAL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acronimo").value("OSDE"));
    }

    private String validPayload() {
        return """
                {
                  "acronimo": "OSDE",
                  "nombreCompleto": "OSDE 210",
                  "cuit": "30-71234568-0",
                  "email": "osde@test.com",
                  "telefono": "1155550000",
                  "estado": "ACTIVE",
                  "planes": [
                    {
                      "nombreCorto": "210",
                      "nombreCompleto": "Plan 210",
                      "tipoCobertura": "PORCENTAJE",
                      "valorCobertura": 80,
                      "tipoCoseguro": "MONTO",
                      "valorCoseguro": 1000,
                      "prestacionesSinAutorizacion": 2,
                      "activo": true
                    }
                  ]
                }
                """;
    }

    private ObraSocialDetailResult sampleDetail() {
        return new ObraSocialDetailResult(
                OBRA_SOCIAL_ID,
                CONSULTORIO_ID,
                "OSDE",
                "OSDE 210",
                "30712345689",
                "osde@test.com",
                "1155550000",
                null,
                "Rep",
                null,
                null,
                ObraSocialEstado.ACTIVE,
                List.of(new PlanResult(
                        UUID.randomUUID(),
                        "210",
                        "Plan 210",
                        TipoCobertura.PORCENTAJE,
                        BigDecimal.valueOf(80),
                        TipoCoseguro.MONTO,
                        BigDecimal.valueOf(1000),
                        1,
                        null,
                        true,
                        Instant.now(),
                        Instant.now()
                )),
                Instant.now(),
                Instant.now()
        );
    }
}


