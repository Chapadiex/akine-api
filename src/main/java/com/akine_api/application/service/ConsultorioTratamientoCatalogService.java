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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioTratamientoCatalogService {

    public enum DefaultsMode {
        RESET,
        ADD_MISSING
    }

    private static final Set<String> VALID_TIPOS = Set.of("PRINCIPAL", "TECNICA");
    private static final Set<String> VALID_MODALIDADES = Set.of(
            "CONSULTORIO",
            "DOMICILIO",
            "PILETA",
            "INTERNACION",
            "INSTITUCION"
    );

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
                .orElseGet(() -> toResult(consultorioId, loadDefaultRoot(consultorioId), Instant.now(), "system", Instant.now(), "system"));
    }

    public TratamientoCatalogResult upsert(UUID consultorioId,
                                           String version,
                                           String monedaNomenclador,
                                           String pais,
                                           JsonNode observaciones,
                                           JsonNode tipos,
                                           JsonNode categorias,
                                           JsonNode tratamientos,
                                           String userEmail,
                                           Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        ObjectNode root = objectMapper.createObjectNode();
        root.put("consultorioId", consultorioId.toString());
        root.put("version", sanitizeVersion(version));
        root.put("monedaNomenclador", sanitizeRequired(monedaNomenclador, "La moneda del nomenclador es obligatoria"));
        root.put("pais", sanitizeRequired(pais, "El pais es obligatorio"));
        root.set("observaciones", observaciones.deepCopy());
        root.set("tipos", tipos.deepCopy());
        root.set("categorias", categorias.deepCopy());
        root.set("tratamientos", tratamientos.deepCopy());
        validateRoot(root);

        String actor = resolveActor(userEmail);
        ConsultorioTratamientoCatalogEntity existing = catalogRepo.findById(consultorioId).orElse(null);
        if (existing != null) {
            assertNoPhysicalDeletion(readRoot(existing.getCatalogJson()), root);
        }
        return toResult(saveRoot(consultorioId, root, actor, existing));
    }

    public TratamientoCatalogResult restoreDefaults(UUID consultorioId,
                                                    DefaultsMode mode,
                                                    String userEmail,
                                                    Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        String actor = resolveActor(userEmail);
        ObjectNode defaults = loadDefaultRoot(consultorioId);
        ConsultorioTratamientoCatalogEntity existing = catalogRepo.findById(consultorioId).orElse(null);
        ObjectNode toPersist = defaults;
        if (existing != null && mode == DefaultsMode.ADD_MISSING) {
            toPersist = mergeAddMissing(readRoot(existing.getCatalogJson()), defaults);
        }
        validateRoot(toPersist);
        return toResult(saveRoot(consultorioId, toPersist, actor, existing));
    }

    @Transactional(readOnly = true)
    public TratamientoSnapshot requireActiveTreatment(UUID consultorioId, String tratamientoId) {
        String normalizedCode = sanitizeRequired(tratamientoId, "El tratamiento seleccionado es obligatorio");
        ObjectNode root = catalogRepo.findById(consultorioId)
                .map(entity -> readRoot(entity.getCatalogJson()))
                .orElseGet(() -> loadDefaultRoot(consultorioId));

        Map<String, String> categoriaNombreByCodigo = new HashMap<>();
        root.path("categorias").forEach(categoria ->
                categoriaNombreByCodigo.put(categoria.path("codigo").asText(), categoria.path("nombre").asText(null)));

        for (JsonNode tratamiento : root.path("tratamientos")) {
            if (normalizedCode.equals(tratamiento.path("codigoInterno").asText())) {
                if (!tratamiento.path("activo").asBoolean(false)) {
                    throw new IllegalArgumentException("El tratamiento seleccionado esta inactivo");
                }
                String categoriaCodigo = tratamiento.path("categoriaCodigo").asText(null);
                return new TratamientoSnapshot(
                        tratamiento.path("codigoInterno").asText(null),
                        tratamiento.path("nombre").asText(null),
                        categoriaCodigo,
                        categoriaNombreByCodigo.get(categoriaCodigo),
                        tratamiento.path("tipo").asText(null),
                        tratamiento.path("requiereAutorizacion").asBoolean(false),
                        tratamiento.path("requierePrescripcionMedica").asBoolean(false),
                        tratamiento.hasNonNull("duracionSugeridaMinutos")
                                ? tratamiento.path("duracionSugeridaMinutos").asInt()
                                : null
                );
            }
        }
        throw new IllegalArgumentException("El tratamiento seleccionado no existe");
    }

    private ConsultorioTratamientoCatalogEntity saveRoot(UUID consultorioId,
                                                         ObjectNode root,
                                                         String actor,
                                                         ConsultorioTratamientoCatalogEntity existing) {
        Instant now = Instant.now();
        ConsultorioTratamientoCatalogEntity entity = existing != null ? existing : new ConsultorioTratamientoCatalogEntity();
        entity.setConsultorioId(consultorioId);
        entity.setVersion(root.path("version").asText("1.0.0"));
        entity.setCatalogJson(writeRoot(root));
        if (existing == null) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(actor);
        }
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(actor);
        return catalogRepo.save(entity);
    }

    private TratamientoCatalogResult toResult(ConsultorioTratamientoCatalogEntity entity) {
        return toResult(entity.getConsultorioId(), readRoot(entity.getCatalogJson()), entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy());
    }

    private TratamientoCatalogResult toResult(UUID consultorioId,
                                              ObjectNode root,
                                              Instant createdAt,
                                              String createdBy,
                                              Instant updatedAt,
                                              String updatedBy) {
        return new TratamientoCatalogResult(
                consultorioId,
                root.path("version").asText("1.0.0"),
                root.path("monedaNomenclador").asText("ARS"),
                root.path("pais").asText("AR"),
                root.path("observaciones"),
                root.path("tipos"),
                root.path("categorias"),
                root.path("tratamientos"),
                createdAt,
                createdBy,
                updatedAt,
                updatedBy
        );
    }

    private ObjectNode loadDefaultRoot(UUID consultorioId) {
        try (InputStream in = new ClassPathResource("catalog/consultorio-tratamientos.catalog.json").getInputStream()) {
            ObjectNode root = (ObjectNode) objectMapper.readTree(in);
            root.put("consultorioId", consultorioId.toString());
            return root;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el maestro de tratamientos por defecto", ex);
        }
    }

    private ObjectNode readRoot(String rawJson) {
        try {
            ObjectNode persisted = (ObjectNode) objectMapper.readTree(rawJson);
            if (persisted.has("tratamientos")) {
                return persisted;
            }
            return normalizeLegacyRoot(persisted);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Maestro de tratamientos invalido persistido", ex);
        }
    }

    private String writeRoot(JsonNode root) {
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("No se pudo serializar el maestro de tratamientos");
        }
    }

    private ObjectNode normalizeLegacyRoot(ObjectNode legacy) {
        ObjectNode normalized = objectMapper.createObjectNode();
        normalized.put("consultorioId", legacy.path("consultorioId").asText(""));
        normalized.put("version", legacy.path("version").asText("1.0.0"));
        normalized.put("monedaNomenclador", "ARS");
        normalized.put("pais", "AR");
        normalized.set("observaciones", objectMapper.createArrayNode()
                .add("Catalogo legado normalizado automaticamente al nuevo maestro tipado.")
                .add("Completa categorias, tipo y reglas operativas desde Configuracion > Tratamientos."));
        normalized.set("tipos", objectMapper.createArrayNode().add("PRINCIPAL").add("TECNICA"));

        ArrayNode categorias = objectMapper.createArrayNode();
        ArrayNode tratamientos = objectMapper.createArrayNode();
        Map<String, String> categoryCodes = new LinkedHashMap<>();
        int order = 0;
        for (JsonNode item : legacy.path("items")) {
            String categoryName = sanitizeRequired(item.path("category").asText("GENERAL"), "Tratamiento legacy sin categoria");
            String categoryCode = legacyCategoryCode(categoryName, categoryCodes.size() + 1);
            categoryCodes.putIfAbsent(categoryName, categoryCode);
            ObjectNode tratamiento = objectMapper.createObjectNode();
            String code = sanitizeRequired(item.path("code").asText(null), "Tratamiento legacy sin code");
            tratamiento.put("id", "LEGACY_" + code);
            tratamiento.put("codigoInterno", code);
            tratamiento.put("nombre", sanitizeRequired(item.path("label").asText(null), "Tratamiento legacy sin label"));
            tratamiento.put("categoriaCodigo", categoryCode);
            tratamiento.put("tipo", "PRINCIPAL");
            tratamiento.put("descripcion", item.path("label").asText("Tratamiento legacy migrado"));
            tratamiento.put("facturable", true);
            tratamiento.put("requierePrescripcionMedica", false);
            tratamiento.put("requiereAutorizacion", false);
            tratamiento.put("duracionSugeridaMinutos", 45);
            tratamiento.set("modalidades", objectMapper.createArrayNode().add("CONSULTORIO"));
            tratamiento.put("activo", item.path("active").asBoolean(true));
            tratamiento.putNull("precioReferencia");
            tratamiento.set("codigosFinanciador", objectMapper.createArrayNode());
            tratamiento.put("order", item.path("order").asInt(order += 10));
            tratamientos.add(tratamiento);
        }
        categoryCodes.forEach((name, code) -> {
            ObjectNode categoria = objectMapper.createObjectNode();
            categoria.put("id", "LEGACY_" + code);
            categoria.put("codigo", code);
            categoria.put("nombre", name);
            categorias.add(categoria);
        });
        normalized.set("categorias", categorias);
        normalized.set("tratamientos", tratamientos);
        return normalized;
    }

    private String legacyCategoryCode(String categoryName, int ordinal) {
        String normalized = categoryName
                .trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return normalized.isBlank() ? "LEGACY_CAT_" + ordinal : normalized;
    }

    private ObjectNode mergeAddMissing(ObjectNode current, ObjectNode defaults) {
        ObjectNode normalizedCurrent = current.has("tratamientos") ? current.deepCopy() : normalizeLegacyRoot(current);
        ObjectNode merged = normalizedCurrent.deepCopy();
        merged.put("version", defaults.path("version").asText(normalizedCurrent.path("version").asText("1.0.0")));
        merged.put("monedaNomenclador", normalizedCurrent.path("monedaNomenclador").asText(defaults.path("monedaNomenclador").asText("ARS")));
        merged.put("pais", normalizedCurrent.path("pais").asText(defaults.path("pais").asText("AR")));

        ArrayNode observaciones = objectMapper.createArrayNode();
        Set<String> observacionesVistas = new HashSet<>();
        normalizedCurrent.path("observaciones").forEach(item -> {
            String value = item.asText();
            if (observacionesVistas.add(value)) {
                observaciones.add(value);
            }
        });
        defaults.path("observaciones").forEach(item -> {
            String value = item.asText();
            if (observacionesVistas.add(value)) {
                observaciones.add(value);
            }
        });
        merged.set("observaciones", observaciones);

        ArrayNode tipos = objectMapper.createArrayNode();
        Set<String> currentTipos = new HashSet<>();
        normalizedCurrent.path("tipos").forEach(item -> {
            String value = item.asText();
            if (currentTipos.add(value)) {
                tipos.add(value);
            }
        });
        defaults.path("tipos").forEach(item -> {
            String value = item.asText();
            if (currentTipos.add(value)) {
                tipos.add(value);
            }
        });
        merged.set("tipos", tipos);

        merged.set("categorias", mergeByCode(normalizedCurrent.path("categorias"), defaults.path("categorias"), "codigo"));
        merged.set("tratamientos", mergeByCode(normalizedCurrent.path("tratamientos"), defaults.path("tratamientos"), "codigoInterno"));
        return merged;
    }

    private ArrayNode mergeByCode(JsonNode current, JsonNode defaults, String codeField) {
        ArrayNode merged = objectMapper.createArrayNode();
        Map<String, JsonNode> currentByCode = byCode(current, codeField);
        Set<String> processed = new HashSet<>();

        for (JsonNode item : defaults) {
            String code = item.path(codeField).asText();
            JsonNode existing = currentByCode.get(code);
            merged.add(existing == null ? item.deepCopy() : existing.deepCopy());
            processed.add(code);
        }
        if (current != null && current.isArray()) {
            for (JsonNode item : current) {
                String code = item.path(codeField).asText();
                if (!processed.contains(code)) {
                    merged.add(item.deepCopy());
                }
            }
        }
        return merged;
    }

    private Map<String, JsonNode> byCode(JsonNode items, String codeField) {
        if (items == null || !items.isArray()) {
            return Map.of();
        }
        Map<String, JsonNode> output = new LinkedHashMap<>();
        for (JsonNode item : items) {
            String code = item.path(codeField).asText();
            if (!code.isBlank()) {
                output.put(code, item);
            }
        }
        return output;
    }

    private void validateRoot(JsonNode root) {
        if (!root.path("observaciones").isArray() || root.path("observaciones").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere observaciones base");
        }
        if (!root.path("tipos").isArray() || root.path("tipos").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere tipos");
        }
        if (!root.path("categorias").isArray() || root.path("categorias").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere categorias");
        }
        if (!root.path("tratamientos").isArray() || root.path("tratamientos").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere tratamientos");
        }

        Set<String> tipos = new HashSet<>();
        for (JsonNode tipo : root.path("tipos")) {
            String value = sanitizeRequired(tipo.asText(null), "Tipo de tratamiento vacio");
            if (!VALID_TIPOS.contains(value)) {
                throw new IllegalArgumentException("Tipo de tratamiento invalido: " + value);
            }
            tipos.add(value);
        }

        Set<String> categorias = new HashSet<>();
        Map<String, String> categoriaByCodigo = new HashMap<>();
        for (JsonNode categoria : root.path("categorias")) {
            String codigo = sanitizeRequired(categoria.path("codigo").asText(null), "Categoria sin codigo");
            String nombre = sanitizeRequired(categoria.path("nombre").asText(null), "Categoria sin nombre");
            if (!categorias.add(codigo)) {
                throw new IllegalArgumentException("Categoria duplicada: " + codigo);
            }
            categoriaByCodigo.put(codigo, nombre);
        }

        Set<String> tratamientos = new HashSet<>();
        for (JsonNode tratamiento : root.path("tratamientos")) {
            String codigoInterno = sanitizeRequired(tratamiento.path("codigoInterno").asText(null), "Tratamiento sin codigo interno");
            String nombre = sanitizeRequired(tratamiento.path("nombre").asText(null), "Tratamiento sin nombre");
            String categoriaCodigo = sanitizeRequired(tratamiento.path("categoriaCodigo").asText(null), "Tratamiento sin categoria");
            String tipo = sanitizeRequired(tratamiento.path("tipo").asText(null), "Tratamiento sin tipo");
            sanitizeRequired(tratamiento.path("descripcion").asText(null), "Tratamiento sin descripcion");
            if (!tratamientos.add(codigoInterno)) {
                throw new IllegalArgumentException("Tratamiento duplicado: " + codigoInterno);
            }
            if (!tipos.contains(tipo)) {
                throw new IllegalArgumentException("El tratamiento " + nombre + " referencia tipo invalido: " + tipo);
            }
            if (!categoriaByCodigo.containsKey(categoriaCodigo)) {
                throw new IllegalArgumentException("El tratamiento " + nombre + " referencia una categoria inexistente: " + categoriaCodigo);
            }
            if (!tratamiento.has("facturable")
                    || !tratamiento.has("requierePrescripcionMedica")
                    || !tratamiento.has("requiereAutorizacion")
                    || !tratamiento.has("activo")) {
                throw new IllegalArgumentException("El tratamiento " + codigoInterno + " requiere flags operativos");
            }
            int duracion = tratamiento.path("duracionSugeridaMinutos").asInt(0);
            if (duracion <= 0) {
                throw new IllegalArgumentException("El tratamiento " + codigoInterno + " requiere una duracion sugerida valida");
            }
            if (!tratamiento.path("modalidades").isArray() || tratamiento.path("modalidades").isEmpty()) {
                throw new IllegalArgumentException("El tratamiento " + codigoInterno + " requiere modalidades");
            }
            Set<String> modalidades = new HashSet<>();
            for (JsonNode modalidad : tratamiento.path("modalidades")) {
                String value = sanitizeRequired(modalidad.asText(null), "Modalidad vacia");
                if (!VALID_MODALIDADES.contains(value)) {
                    throw new IllegalArgumentException("Modalidad invalida para el tratamiento " + codigoInterno + ": " + value);
                }
                if (!modalidades.add(value)) {
                    throw new IllegalArgumentException("Modalidad duplicada para el tratamiento " + codigoInterno + ": " + value);
                }
            }
            if (!tratamiento.path("codigosFinanciador").isArray()) {
                throw new IllegalArgumentException("El tratamiento " + codigoInterno + " requiere codigosFinanciador");
            }
        }
    }

    private void assertNoPhysicalDeletion(ObjectNode existing, ObjectNode updated) {
        Set<String> existingCodes = collectTreatmentCodes(existing.path("tratamientos"));
        Set<String> updatedCodes = collectTreatmentCodes(updated.path("tratamientos"));
        existingCodes.removeAll(updatedCodes);
        if (!existingCodes.isEmpty()) {
            throw new IllegalArgumentException("No se permite borrar tratamientos del maestro; debes inactivarlos");
        }
    }

    private Set<String> collectTreatmentCodes(JsonNode tratamientos) {
        Set<String> codes = new HashSet<>();
        if (tratamientos != null && tratamientos.isArray()) {
            tratamientos.forEach(item -> codes.add(item.path("codigoInterno").asText(item.path("code").asText())));
        }
        return codes;
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

    private String sanitizeVersion(String version) {
        return version != null && !version.isBlank() ? version.trim() : "1.0.0";
    }

    private String sanitizeRequired(String value, String error) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        return value.trim();
    }

    public record TratamientoSnapshot(
            String codigo,
            String nombre,
            String categoriaCodigo,
            String categoriaNombre,
            String tipo,
            boolean requiereAutorizacion,
            boolean requierePrescripcionMedica,
            Integer duracionSugeridaMinutos
    ) {
    }
}
