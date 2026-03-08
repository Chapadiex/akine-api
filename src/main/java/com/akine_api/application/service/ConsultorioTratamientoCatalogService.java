package com.akine_api.application.service;

import com.akine_api.application.dto.result.TratamientoCatalogResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.User;
import com.akine_api.infrastructure.persistence.entity.ConsultorioTratamientoCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioTratamientoCatalogJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioTratamientoCatalogService {

    private final ConsultorioTratamientoCatalogJpaRepository catalogRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final ObjectMapper objectMapper;

    public ConsultorioTratamientoCatalogService(ConsultorioTratamientoCatalogJpaRepository catalogRepo,
                                                ConsultorioRepositoryPort consultorioRepo,
                                                UserRepositoryPort userRepo,
                                                ObjectMapper objectMapper) {
        this.catalogRepo = catalogRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public TratamientoCatalogResult get(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return catalogRepo.findById(consultorioId)
                .map(this::toResult)
                .orElseGet(() -> new TratamientoCatalogResult(
                        consultorioId,
                        "1.0.0",
                        loadDefaultItemsForConsultorio(consultorioId),
                        Instant.now(),
                        "system",
                        Instant.now(),
                        "system"
                ));
    }

    public TratamientoCatalogResult upsert(UUID consultorioId,
                                           String version,
                                           JsonNode items,
                                           String userEmail,
                                           Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        validateItems(items);

        String actor = resolveActor(userEmail);
        ConsultorioTratamientoCatalogEntity existing = catalogRepo.findById(consultorioId).orElse(null);
        ConsultorioTratamientoCatalogEntity entity = existing != null ? existing : new ConsultorioTratamientoCatalogEntity();
        Instant now = Instant.now();
        entity.setConsultorioId(consultorioId);
        entity.setVersion(version != null && !version.isBlank() ? version.trim() : "1.0.0");
        entity.setCatalogJson(writeJson(items));
        if (existing == null) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(actor);
        }
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(actor);
        return toResult(catalogRepo.save(entity));
    }

    private TratamientoCatalogResult toResult(ConsultorioTratamientoCatalogEntity entity) {
        return new TratamientoCatalogResult(
                entity.getConsultorioId(),
                entity.getVersion(),
                readItems(entity.getCatalogJson()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private String writeJson(JsonNode items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("No se pudo serializar el catalogo de tratamientos");
        }
    }

    private JsonNode readItems(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Catalogo de tratamientos invalido persistido");
        }
    }

    private JsonNode loadDefaultItemsForConsultorio(UUID consultorioId) {
        try (InputStream in = new ClassPathResource("catalog/consultorio-tratamientos.catalog.json").getInputStream()) {
            ObjectNode root = (ObjectNode) objectMapper.readTree(in);
            root.put("consultorioId", consultorioId.toString());
            return root.path("items");
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el catalogo de tratamientos por defecto", ex);
        }
    }

    private void validateItems(JsonNode items) {
        if (items == null || !items.isArray() || items.isEmpty()) {
            throw new IllegalArgumentException("El catalogo de tratamientos requiere items");
        }
        Set<String> codes = new HashSet<>();
        for (JsonNode item : items) {
            String code = textRequired(item, "code", "Tratamiento sin code");
            textRequired(item, "label", "Tratamiento sin label");
            boolRequired(item, "active", "Tratamiento sin active");
            intRequired(item, "order", "Tratamiento sin order");
            if (!codes.add(code)) {
                throw new IllegalArgumentException("Tratamiento duplicado: " + code);
            }
        }
    }

    private String textRequired(JsonNode node, String key, String error) {
        String value = node.path(key).asText("");
        if (value.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        return value.trim();
    }

    private boolean boolRequired(JsonNode node, String key, String error) {
        if (!node.has(key)) {
            throw new IllegalArgumentException(error);
        }
        return node.path(key).asBoolean();
    }

    private int intRequired(JsonNode node, String key, String error) {
        if (!node.has(key)) {
            throw new IllegalArgumentException(error);
        }
        return node.path(key).asInt();
    }

    private void assertConsultorioExists(UUID consultorioId) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return;
        }
        UUID userId = resolveUserId(userEmail);
        if (!consultorioRepo.findConsultorioIdsByUserId(userId).contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN") && !roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        assertCanRead(consultorioId, userEmail, roles);
    }

    private UUID resolveUserId(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private String resolveActor(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .map(User::getEmail)
                .orElse(userEmail);
    }
}
