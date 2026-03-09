package com.akine_api.application.service;

import com.akine_api.application.dto.result.DiagnosticosMedicosResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.User;
import com.akine_api.infrastructure.persistence.entity.ConsultorioDiagnosticosMedicosEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioDiagnosticosMedicosJpaRepository;
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
public class ConsultorioDiagnosticosMedicosService {

    public enum DefaultsMode {
        RESET,
        ADD_MISSING
    }

    private static final Set<String> VALID_TIPOS = Set.of(
            "DIAGNOSTICO_MEDICO",
            "MOTIVO_DERIVACION",
            "FUNCIONAL",
            "POSTQUIRURGICO"
    );

    private final ConsultorioDiagnosticosMedicosJpaRepository maestroRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final ObjectMapper objectMapper;

    public ConsultorioDiagnosticosMedicosService(ConsultorioDiagnosticosMedicosJpaRepository maestroRepo,
                                                 ConsultorioRepositoryPort consultorioRepo,
                                                 UserRepositoryPort userRepo,
                                                 ObjectMapper objectMapper) {
        this.maestroRepo = maestroRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public DiagnosticosMedicosResult get(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return maestroRepo.findById(consultorioId)
                .map(this::toResult)
                .orElseGet(() -> toResult(consultorioId, loadDefaultRoot(consultorioId), Instant.now(), "system", Instant.now(), "system"));
    }

    public DiagnosticosMedicosResult upsert(UUID consultorioId,
                                            String version,
                                            String pais,
                                            String idioma,
                                            JsonNode tipos,
                                            JsonNode categorias,
                                            JsonNode diagnosticos,
                                            String userEmail,
                                            Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        ObjectNode root = objectMapper.createObjectNode();
        root.put("consultorioId", consultorioId.toString());
        root.put("version", sanitizeVersion(version));
        root.put("pais", sanitizeRequired(pais, "El pais es obligatorio"));
        root.put("idioma", sanitizeRequired(idioma, "El idioma es obligatorio"));
        root.set("tipos", tipos.deepCopy());
        root.set("categorias", categorias.deepCopy());
        root.set("diagnosticos", diagnosticos.deepCopy());
        validateRoot(root);

        String actor = resolveActor(userEmail);
        ConsultorioDiagnosticosMedicosEntity existing = maestroRepo.findById(consultorioId).orElse(null);
        if (existing != null) {
            assertNoPhysicalDeletion(readRoot(existing.getMaestroJson()), root);
        }
        return toResult(saveRoot(consultorioId, root, actor, existing));
    }

    public DiagnosticosMedicosResult restoreDefaults(UUID consultorioId,
                                                     DefaultsMode mode,
                                                     String userEmail,
                                                     Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        String actor = resolveActor(userEmail);
        ObjectNode defaults = loadDefaultRoot(consultorioId);
        ConsultorioDiagnosticosMedicosEntity existing = maestroRepo.findById(consultorioId).orElse(null);
        ObjectNode toPersist = defaults;
        if (existing != null && mode == DefaultsMode.ADD_MISSING) {
            toPersist = mergeAddMissing(readRoot(existing.getMaestroJson()), defaults);
        }
        validateRoot(toPersist);
        return toResult(saveRoot(consultorioId, toPersist, actor, existing));
    }

    @Transactional(readOnly = true)
    public DiagnosticoMedicoSnapshot requireActiveDiagnostico(UUID consultorioId, String codigo) {
        JsonNode diagnosticos = maestroRepo.findById(consultorioId)
                .map(entity -> readRoot(entity.getMaestroJson()).path("diagnosticos"))
                .orElseGet(() -> loadDefaultRoot(consultorioId).path("diagnosticos"));

        for (JsonNode diagnostico : diagnosticos) {
            if (codigo.equals(diagnostico.path("codigoInterno").asText())) {
                if (!diagnostico.path("activo").asBoolean(false)) {
                    throw new IllegalArgumentException("El diagnostico medico seleccionado esta inactivo");
                }
                return new DiagnosticoMedicoSnapshot(
                        diagnostico.path("codigoInterno").asText(null),
                        diagnostico.path("nombre").asText(null),
                        diagnostico.path("tipo").asText(null),
                        diagnostico.path("categoriaCodigo").asText(null),
                        resolveCategoriaNombre(diagnostico.path("categoriaCodigo").asText(null), consultorioId),
                        diagnostico.path("subcategoria").asText(null),
                        diagnostico.path("regionAnatomica").asText(null)
                );
            }
        }
        throw new IllegalArgumentException("El diagnostico medico seleccionado no existe");
    }

    private String resolveCategoriaNombre(String categoriaCodigo, UUID consultorioId) {
        if (categoriaCodigo == null || categoriaCodigo.isBlank()) {
            return null;
        }
        JsonNode categorias = maestroRepo.findById(consultorioId)
                .map(entity -> readRoot(entity.getMaestroJson()).path("categorias"))
                .orElseGet(() -> loadDefaultRoot(consultorioId).path("categorias"));
        for (JsonNode categoria : categorias) {
            if (categoriaCodigo.equals(categoria.path("codigo").asText())) {
                return categoria.path("nombre").asText(null);
            }
        }
        return null;
    }

    private ConsultorioDiagnosticosMedicosEntity saveRoot(UUID consultorioId,
                                                          ObjectNode root,
                                                          String actor,
                                                          ConsultorioDiagnosticosMedicosEntity existing) {
        Instant now = Instant.now();
        ConsultorioDiagnosticosMedicosEntity entity = existing != null ? existing : new ConsultorioDiagnosticosMedicosEntity();
        entity.setConsultorioId(consultorioId);
        entity.setVersion(root.path("version").asText("1.0.0"));
        entity.setMaestroJson(writeRoot(root));
        if (existing == null) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(actor);
        }
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(actor);
        return maestroRepo.save(entity);
    }

    private DiagnosticosMedicosResult toResult(ConsultorioDiagnosticosMedicosEntity entity) {
        return toResult(entity.getConsultorioId(), readRoot(entity.getMaestroJson()), entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy());
    }

    private DiagnosticosMedicosResult toResult(UUID consultorioId,
                                               ObjectNode root,
                                               Instant createdAt,
                                               String createdBy,
                                               Instant updatedAt,
                                               String updatedBy) {
        return new DiagnosticosMedicosResult(
                consultorioId,
                root.path("version").asText("1.0.0"),
                root.path("pais").asText("AR"),
                root.path("idioma").asText("es"),
                root.path("tipos"),
                root.path("categorias"),
                root.path("diagnosticos"),
                createdAt,
                createdBy,
                updatedAt,
                updatedBy
        );
    }

    private ObjectNode loadDefaultRoot(UUID consultorioId) {
        try (InputStream in = new ClassPathResource("catalog/consultorio-diagnosticos-medicos.json").getInputStream()) {
            ObjectNode root = (ObjectNode) objectMapper.readTree(in);
            root.put("consultorioId", consultorioId.toString());
            return root;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el maestro de diagnosticos medicos por defecto", ex);
        }
    }

    private ObjectNode readRoot(String rawJson) {
        try {
            return (ObjectNode) objectMapper.readTree(rawJson);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Maestro de diagnosticos medicos invalido persistido", ex);
        }
    }

    private String writeRoot(JsonNode root) {
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("No se pudo serializar el maestro de diagnosticos medicos", ex);
        }
    }

    private ObjectNode mergeAddMissing(ObjectNode current, ObjectNode defaults) {
        ObjectNode merged = current.deepCopy();
        merged.put("version", defaults.path("version").asText(current.path("version").asText("1.0.0")));
        merged.put("pais", current.path("pais").asText(defaults.path("pais").asText("AR")));
        merged.put("idioma", current.path("idioma").asText(defaults.path("idioma").asText("es")));

        Set<String> currentTipos = new HashSet<>();
        ArrayNode tipos = objectMapper.createArrayNode();
        if (current.path("tipos").isArray()) {
            current.path("tipos").forEach(item -> {
                String value = item.asText();
                currentTipos.add(value);
                tipos.add(value);
            });
        }
        defaults.path("tipos").forEach(item -> {
            String value = item.asText();
            if (currentTipos.add(value)) {
                tipos.add(value);
            }
        });
        merged.set("tipos", tipos);

        merged.set("categorias", mergeByCodigo(current.path("categorias"), defaults.path("categorias")));
        merged.set("diagnosticos", mergeByCodigo(current.path("diagnosticos"), defaults.path("diagnosticos")));
        return merged;
    }

    private ArrayNode mergeByCodigo(JsonNode current, JsonNode defaults) {
        ArrayNode merged = objectMapper.createArrayNode();
        Map<String, JsonNode> currentByCode = byCode(current, current == null ? "codigo" : inferCodeField(current));
        Set<String> processed = new HashSet<>();

        for (JsonNode item : defaults) {
            String codeField = inferItemCodeField(item);
            String code = item.path(codeField).asText();
            JsonNode existing = currentByCode.get(code);
            merged.add(existing == null ? item.deepCopy() : existing.deepCopy());
            processed.add(code);
        }
        if (current != null && current.isArray()) {
            for (JsonNode item : current) {
                String codeField = inferItemCodeField(item);
                String code = item.path(codeField).asText();
                if (!processed.contains(code)) {
                    merged.add(item.deepCopy());
                }
            }
        }
        return merged;
    }

    private String inferCodeField(JsonNode items) {
        if (items == null || !items.isArray() || items.isEmpty()) {
            return "codigo";
        }
        return inferItemCodeField(items.get(0));
    }

    private String inferItemCodeField(JsonNode item) {
        return item.has("codigoInterno") ? "codigoInterno" : "codigo";
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
        if (!root.path("tipos").isArray() || root.path("tipos").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere tipos");
        }
        if (!root.path("categorias").isArray() || root.path("categorias").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere categorias");
        }
        if (!root.path("diagnosticos").isArray() || root.path("diagnosticos").isEmpty()) {
            throw new IllegalArgumentException("El maestro requiere diagnosticos");
        }

        Set<String> tipos = new HashSet<>();
        for (JsonNode tipo : root.path("tipos")) {
            String value = tipo.asText();
            if (!VALID_TIPOS.contains(value)) {
                throw new IllegalArgumentException("Tipo de diagnostico invalido: " + value);
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

        Set<String> diagnosticos = new HashSet<>();
        for (JsonNode diagnostico : root.path("diagnosticos")) {
            String codigoInterno = sanitizeRequired(diagnostico.path("codigoInterno").asText(null), "Diagnostico sin codigo interno");
            String nombre = sanitizeRequired(diagnostico.path("nombre").asText(null), "Diagnostico sin nombre");
            String categoriaCodigo = sanitizeRequired(diagnostico.path("categoriaCodigo").asText(null), "Diagnostico sin categoria");
            String tipo = sanitizeRequired(diagnostico.path("tipo").asText(null), "Diagnostico sin tipo");
            sanitizeRequired(diagnostico.path("regionAnatomica").asText(null), "Diagnostico sin region anatomica");
            if (!diagnosticos.add(codigoInterno)) {
                throw new IllegalArgumentException("Diagnostico duplicado: " + codigoInterno);
            }
            if (!tipos.contains(tipo)) {
                throw new IllegalArgumentException("El diagnostico " + nombre + " referencia tipo invalido: " + tipo);
            }
            if (!categoriaByCodigo.containsKey(categoriaCodigo)) {
                throw new IllegalArgumentException("El diagnostico " + nombre + " referencia una categoria inexistente: " + categoriaCodigo);
            }
            if (!diagnostico.has("activo")) {
                throw new IllegalArgumentException("El diagnostico " + codigoInterno + " requiere activo");
            }
            if (!diagnostico.has("lateralidadAplica")) {
                throw new IllegalArgumentException("El diagnostico " + codigoInterno + " requiere lateralidadAplica");
            }
            if (!diagnostico.has("requiereOrdenMedica")) {
                throw new IllegalArgumentException("El diagnostico " + codigoInterno + " requiere requiereOrdenMedica");
            }
        }
    }

    private void assertNoPhysicalDeletion(ObjectNode existing, ObjectNode updated) {
        Set<String> existingCodes = collectDiagnosticoCodes(existing.path("diagnosticos"));
        Set<String> updatedCodes = collectDiagnosticoCodes(updated.path("diagnosticos"));
        existingCodes.removeAll(updatedCodes);
        if (!existingCodes.isEmpty()) {
            throw new IllegalArgumentException("No se permite borrar diagnosticos medicos del maestro; debes inactivarlos");
        }
    }

    private Set<String> collectDiagnosticoCodes(JsonNode diagnosticos) {
        Set<String> codes = new HashSet<>();
        if (diagnosticos != null && diagnosticos.isArray()) {
            diagnosticos.forEach(item -> codes.add(item.path("codigoInterno").asText()));
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

    public record DiagnosticoMedicoSnapshot(
            String codigo,
            String nombre,
            String tipo,
            String categoriaCodigo,
            String categoriaNombre,
            String subcategoria,
            String regionAnatomica
    ) {
    }
}
