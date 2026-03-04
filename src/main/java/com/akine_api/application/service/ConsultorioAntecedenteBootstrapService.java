package com.akine_api.application.service;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.infrastructure.persistence.entity.ConsultorioAntecedenteCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioAntecedenteCatalogJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioAntecedenteBootstrapService {

    private final ConsultorioAntecedenteCatalogJpaRepository catalogRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final ObjectMapper objectMapper;

    public ConsultorioAntecedenteBootstrapService(ConsultorioAntecedenteCatalogJpaRepository catalogRepo,
                                                  ConsultorioRepositoryPort consultorioRepo,
                                                  ObjectMapper objectMapper) {
        this.catalogRepo = catalogRepo;
        this.consultorioRepo = consultorioRepo;
        this.objectMapper = objectMapper;
    }

    public void ensureDefaults(UUID consultorioId, String actor) {
        assertConsultorioExists(consultorioId);
        if (catalogRepo.findById(consultorioId).isPresent()) {
            return;
        }

        ObjectNode defaultRoot = loadDefaultRoot(consultorioId);
        String version = defaultRoot.path("version").asText("1.0.0");
        JsonNode categories = defaultRoot.path("categories");

        ConsultorioAntecedenteCatalogEntity entity = new ConsultorioAntecedenteCatalogEntity();
        Instant now = Instant.now();
        entity.setConsultorioId(consultorioId);
        entity.setVersion(version);
        entity.setCatalogJson(writeJson(categories));
        entity.setCreatedAt(now);
        entity.setCreatedBy(resolveActor(actor));
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(resolveActor(actor));
        catalogRepo.save(entity);
    }

    private ObjectNode loadDefaultRoot(UUID consultorioId) {
        try (InputStream in = new ClassPathResource("catalog/consultorio-antecedentes.catalog.json").getInputStream()) {
            ObjectNode root = (ObjectNode) objectMapper.readTree(in);
            root.put("consultorioId", consultorioId.toString());
            return root;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el catalogo por defecto", ex);
        }
    }

    private String writeJson(JsonNode categories) {
        try {
            return objectMapper.writeValueAsString(categories);
        } catch (Exception ex) {
            throw new IllegalArgumentException("No se pudo serializar el catalogo");
        }
    }

    private String resolveActor(String actor) {
        if (actor == null || actor.isBlank()) return "system";
        return actor.trim().toLowerCase();
    }

    private void assertConsultorioExists(UUID consultorioId) {
        consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + consultorioId));
    }
}
