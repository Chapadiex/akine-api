package com.akine_api.controller;

import com.akine_api.application.dto.result.AntecedenteCatalogResult;
import com.akine_api.application.service.ConsultorioAntecedenteCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AntecedenteCatalogControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ConsultorioAntecedenteCatalogService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @Test
    void get_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/antecedentes-catalogo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void get_asAdmin_returns200() throws Exception {
        when(service.get(any(), any(), any()))
                .thenReturn(sampleResult());

        mvc.perform(get("/api/v1/consultorios/" + CONSULTORIO_ID + "/antecedentes-catalogo"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void upsert_asAdmin_returns200() throws Exception {
        when(service.upsert(any(), any(), any(), any(), any()))
                .thenReturn(sampleResult());

        mvc.perform(put("/api/v1/consultorios/" + CONSULTORIO_ID + "/antecedentes-catalogo")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "version":"1.0.0",
                                  "categories":[
                                    {"code":"APP","name":"Personales patológicos","order":10,"active":true,"items":[]},
                                    {"code":"AQX","name":"Quirúrgicos","order":20,"active":true,"items":[]},
                                    {"code":"MED","name":"Medicación actual","order":30,"active":true,"items":[]},
                                    {"code":"TRAU","name":"Traumatológicos","order":40,"active":true,"items":[]},
                                    {"code":"FAM","name":"Familiares","order":50,"active":true,"items":[]},
                                    {"code":"HAB","name":"Hábitos y estilo de vida","order":60,"active":true,"items":[]},
                                    {"code":"ALG","name":"Alergias","order":70,"active":true,"items":[]}
                                  ]
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restoreDefaults_asAdmin_returns200() throws Exception {
        when(service.restoreDefaults(any(), any(), any(), any()))
                .thenReturn(sampleResult());

        mvc.perform(post("/api/v1/consultorios/" + CONSULTORIO_ID + "/antecedentes-catalogo/defaults/restore")
                        .param("mode", "ADD_MISSING")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private AntecedenteCatalogResult sampleResult() {
        try {
            return new AntecedenteCatalogResult(
                    CONSULTORIO_ID,
                    "1.0.0",
                    mapper.readTree("[]"),
                    Instant.now(),
                    "admin@test.com",
                    Instant.now(),
                    "admin@test.com"
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

