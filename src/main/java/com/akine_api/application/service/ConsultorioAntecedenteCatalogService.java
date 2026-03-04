package com.akine_api.application.service;

import com.akine_api.application.dto.result.AntecedenteCatalogResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.User;
import com.akine_api.infrastructure.persistence.entity.ConsultorioAntecedenteCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioAntecedenteCatalogJpaRepository;
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
import java.util.*;

@Service
@Transactional
public class ConsultorioAntecedenteCatalogService {

    public enum DefaultsMode {
        RESET,
        ADD_MISSING
    }

    private static final Set<String> VALID_VALUE_TYPES = Set.of(
            "BOOLEAN", "ENUM", "ENUM_MULTI", "REPEATABLE", "TEXT"
    );
    private static final Set<String> FIXED_CATEGORY_CODES = Set.of(
            "APP", "AQX", "MED", "TRAU", "FAM", "HAB", "ALG"
    );

    private final ConsultorioAntecedenteCatalogJpaRepository catalogRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final ObjectMapper objectMapper;

    public ConsultorioAntecedenteCatalogService(
            ConsultorioAntecedenteCatalogJpaRepository catalogRepo,
            ConsultorioRepositoryPort consultorioRepo,
            UserRepositoryPort userRepo,
            ObjectMapper objectMapper
    ) {
        this.catalogRepo = catalogRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public AntecedenteCatalogResult get(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);

        return catalogRepo.findById(consultorioId)
                .map(this::toResult)
                .orElseGet(() -> new AntecedenteCatalogResult(
                        consultorioId,
                        "1.0.0",
                        loadDefaultCategoriesForConsultorio(consultorioId),
                        Instant.now(),
                        "system",
                        Instant.now(),
                        "system"
                ));
    }

    public AntecedenteCatalogResult upsert(
            UUID consultorioId,
            String version,
            JsonNode categories,
            String userEmail,
            Set<String> roles
    ) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        String actor = resolveActor(userEmail);
        validateCatalogStructure(categories);

        ConsultorioAntecedenteCatalogEntity existing = catalogRepo.findById(consultorioId).orElse(null);
        if (existing != null) {
            assertNoPhysicalDeletion(existing.getCatalogJson(), categories);
        }

        ConsultorioAntecedenteCatalogEntity saved = saveCatalog(consultorioId, categories, version, actor, existing);
        return toResult(saved);
    }

    public AntecedenteCatalogResult restoreDefaults(
            UUID consultorioId,
            DefaultsMode mode,
            String userEmail,
            Set<String> roles
    ) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        String actor = resolveActor(userEmail);
        ObjectNode defaultRoot = loadDefaultRoot(consultorioId);
        String version = defaultRoot.path("version").asText("1.0.0");
        JsonNode defaultCategories = defaultRoot.path("categories");

        ConsultorioAntecedenteCatalogEntity existing = catalogRepo.findById(consultorioId).orElse(null);
        JsonNode categoriesToPersist;
        if (existing == null || mode == DefaultsMode.RESET) {
            categoriesToPersist = defaultCategories;
        } else {
            categoriesToPersist = mergeAddMissing(readCategories(existing.getCatalogJson()), defaultCategories);
        }

        validateCatalogStructure(categoriesToPersist);
        ConsultorioAntecedenteCatalogEntity saved = saveCatalog(consultorioId, categoriesToPersist, version, actor, existing);
        return toResult(saved);
    }

    private ConsultorioAntecedenteCatalogEntity saveCatalog(
            UUID consultorioId,
            JsonNode categories,
            String version,
            String actor,
            ConsultorioAntecedenteCatalogEntity existing
    ) {
        Instant now = Instant.now();
        ConsultorioAntecedenteCatalogEntity entity = existing != null ? existing : new ConsultorioAntecedenteCatalogEntity();
        entity.setConsultorioId(consultorioId);
        entity.setVersion(version != null && !version.isBlank() ? version.trim() : "1.0.0");
        entity.setCatalogJson(writeJson(categories));
        if (existing == null) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(actor);
        }
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(actor);
        return catalogRepo.save(entity);
    }

    private AntecedenteCatalogResult toResult(ConsultorioAntecedenteCatalogEntity e) {
        return new AntecedenteCatalogResult(
                e.getConsultorioId(),
                e.getVersion(),
                readCategories(e.getCatalogJson()),
                e.getCreatedAt(),
                e.getCreatedBy(),
                e.getUpdatedAt(),
                e.getUpdatedBy()
        );
    }

    private String writeJson(JsonNode categories) {
        try {
            return objectMapper.writeValueAsString(categories);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("No se pudo serializar el catalogo");
        }
    }

    private JsonNode readCategories(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Catalogo invalido persistido");
        }
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

    private JsonNode loadDefaultCategoriesForConsultorio(UUID consultorioId) {
        return loadDefaultRoot(consultorioId).path("categories");
    }

    private JsonNode mergeAddMissing(JsonNode currentCategories, JsonNode defaultCategories) {
        if (!currentCategories.isArray() || !defaultCategories.isArray()) {
            return defaultCategories;
        }

        ArrayNode merged = objectMapper.createArrayNode();
        Map<String, JsonNode> currentByCode = byCode(currentCategories);
        Set<String> added = new HashSet<>();

        for (JsonNode defaultCategory : defaultCategories) {
            String code = defaultCategory.path("code").asText();
            JsonNode current = currentByCode.get(code);
            if (current == null) {
                merged.add(defaultCategory.deepCopy());
                added.add(code);
                continue;
            }
            ObjectNode categoryMerged = current.deepCopy();
            JsonNode mergedItems = mergeItemsAddMissing(current.path("items"), defaultCategory.path("items"));
            categoryMerged.set("items", mergedItems);
            merged.add(categoryMerged);
            added.add(code);
        }

        for (JsonNode currentCategory : currentCategories) {
            String code = currentCategory.path("code").asText();
            if (!added.contains(code)) {
                merged.add(currentCategory.deepCopy());
            }
        }

        return merged;
    }

    private JsonNode mergeItemsAddMissing(JsonNode currentItems, JsonNode defaultItems) {
        if (!currentItems.isArray() || !defaultItems.isArray()) {
            return defaultItems;
        }
        ArrayNode merged = objectMapper.createArrayNode();
        Map<String, JsonNode> currentByCode = byCode(currentItems);
        Set<String> processed = new HashSet<>();

        for (JsonNode defaultItem : defaultItems) {
            String code = defaultItem.path("code").asText();
            JsonNode current = currentByCode.get(code);
            if (current == null) {
                merged.add(defaultItem.deepCopy());
                processed.add(code);
                continue;
            }
            ObjectNode itemMerged = current.deepCopy();
            if (current.path("options").isArray() && defaultItem.path("options").isArray()) {
                itemMerged.set("options", mergeArrayByCode(current.path("options"), defaultItem.path("options")));
            }
            if (current.path("fields").isArray() && defaultItem.path("fields").isArray()) {
                itemMerged.set("fields", mergeArrayByCode(current.path("fields"), defaultItem.path("fields")));
            }
            merged.add(itemMerged);
            processed.add(code);
        }

        for (JsonNode currentItem : currentItems) {
            String code = currentItem.path("code").asText();
            if (!processed.contains(code)) {
                merged.add(currentItem.deepCopy());
            }
        }
        return merged;
    }

    private JsonNode mergeArrayByCode(JsonNode current, JsonNode defaults) {
        ArrayNode merged = objectMapper.createArrayNode();
        Map<String, JsonNode> currentByCode = byCode(current);
        Set<String> processed = new HashSet<>();

        for (JsonNode def : defaults) {
            String code = def.path("code").asText();
            JsonNode existing = currentByCode.get(code);
            merged.add(existing == null ? def.deepCopy() : existing.deepCopy());
            processed.add(code);
        }
        for (JsonNode node : current) {
            String code = node.path("code").asText();
            if (!processed.contains(code)) {
                merged.add(node.deepCopy());
            }
        }
        return merged;
    }

    private Map<String, JsonNode> byCode(JsonNode arrayNode) {
        if (!arrayNode.isArray()) return Map.of();
        Map<String, JsonNode> byCode = new LinkedHashMap<>();
        for (JsonNode node : arrayNode) {
            String code = node.path("code").asText();
            if (!code.isBlank()) {
                byCode.put(code, node);
            }
        }
        return byCode;
    }

    private void validateCatalogStructure(JsonNode categories) {
        if (categories == null || !categories.isArray()) {
            throw new IllegalArgumentException("El catalogo requiere un array de categorias");
        }
        Set<String> codes = new HashSet<>();
        for (JsonNode category : categories) {
            String code = textRequired(category, "code", "Categoria sin code");
            textRequired(category, "name", "Categoria sin name");
            intRequired(category, "order", "Categoria sin order");
            boolRequired(category, "active", "Categoria sin active");
            if (!FIXED_CATEGORY_CODES.contains(code)) {
                throw new IllegalArgumentException("Categoria invalida: " + code);
            }
            if (!codes.add(code)) {
                throw new IllegalArgumentException("Categoria duplicada: " + code);
            }
            JsonNode items = category.path("items");
            if (!items.isArray()) {
                throw new IllegalArgumentException("La categoria " + code + " requiere items");
            }
            validateItems(items, code);
        }
        if (!codes.containsAll(FIXED_CATEGORY_CODES)) {
            Set<String> missing = new HashSet<>(FIXED_CATEGORY_CODES);
            missing.removeAll(codes);
            throw new IllegalArgumentException("Faltan categorias fijas: " + String.join(", ", missing));
        }
    }

    private void validateItems(JsonNode items, String categoryCode) {
        Set<String> itemCodes = new HashSet<>();
        for (JsonNode item : items) {
            String itemCode = textRequired(item, "code", "Item sin code en categoria " + categoryCode);
            textRequired(item, "label", "Item sin label: " + itemCode);
            String valueType = textRequired(item, "valueType", "Item sin valueType: " + itemCode);
            if (!VALID_VALUE_TYPES.contains(valueType)) {
                throw new IllegalArgumentException("valueType invalido en item " + itemCode + ": " + valueType);
            }
            boolRequired(item, "active", "Item sin active: " + itemCode);
            intRequired(item, "order", "Item sin order: " + itemCode);
            if (!itemCodes.add(itemCode)) {
                throw new IllegalArgumentException("Item duplicado en categoria " + categoryCode + ": " + itemCode);
            }

            if ("ENUM".equals(valueType) || "ENUM_MULTI".equals(valueType)) {
                JsonNode options = item.path("options");
                if (!options.isArray() || options.isEmpty()) {
                    throw new IllegalArgumentException("Item " + itemCode + " requiere options");
                }
                validateOptions(options, itemCode);
            }
            if ("REPEATABLE".equals(valueType)) {
                JsonNode fields = item.path("fields");
                if (!fields.isArray() || fields.isEmpty()) {
                    throw new IllegalArgumentException("Item " + itemCode + " requiere fields");
                }
                validateFields(fields, itemCode);
            }
        }
    }

    private void validateOptions(JsonNode options, String itemCode) {
        Set<String> optionCodes = new HashSet<>();
        for (JsonNode option : options) {
            String optionCode = textRequired(option, "code", "Opcion sin code en " + itemCode);
            textRequired(option, "label", "Opcion sin label en " + itemCode);
            boolRequired(option, "active", "Opcion sin active en " + itemCode);
            intRequired(option, "order", "Opcion sin order en " + itemCode);
            if (!optionCodes.add(optionCode)) {
                throw new IllegalArgumentException("Opcion duplicada " + optionCode + " en " + itemCode);
            }
        }
    }

    private void validateFields(JsonNode fields, String itemCode) {
        Set<String> fieldCodes = new HashSet<>();
        for (JsonNode field : fields) {
            String fieldCode = textRequired(field, "code", "Field sin code en " + itemCode);
            textRequired(field, "label", "Field sin label en " + itemCode);
            String type = textRequired(field, "type", "Field sin type en " + itemCode);
            if (!Set.of("TEXT", "NUMBER", "BOOLEAN").contains(type)) {
                throw new IllegalArgumentException("Field type invalido " + type + " en " + itemCode);
            }
            if (!fieldCodes.add(fieldCode)) {
                throw new IllegalArgumentException("Field duplicado " + fieldCode + " en " + itemCode);
            }
        }
    }

    private void assertNoPhysicalDeletion(String previousJson, JsonNode incomingCategories) {
        JsonNode previousCategories = readCategories(previousJson);

        Map<String, Set<String>> previousItemsByCategory = extractItemCodes(previousCategories);
        Map<String, Set<String>> incomingItemsByCategory = extractItemCodes(incomingCategories);
        for (Map.Entry<String, Set<String>> entry : previousItemsByCategory.entrySet()) {
            Set<String> incoming = incomingItemsByCategory.getOrDefault(entry.getKey(), Set.of());
            Set<String> missing = new HashSet<>(entry.getValue());
            missing.removeAll(incoming);
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException(
                        "No se permite borrar items fisicamente (" + entry.getKey() + "): "
                                + String.join(", ", missing) + ". Inactivalos."
                );
            }
        }

        Map<String, Set<String>> previousOptionsByItem = extractOptionCodes(previousCategories);
        Map<String, Set<String>> incomingOptionsByItem = extractOptionCodes(incomingCategories);
        for (Map.Entry<String, Set<String>> entry : previousOptionsByItem.entrySet()) {
            Set<String> incoming = incomingOptionsByItem.getOrDefault(entry.getKey(), Set.of());
            Set<String> missing = new HashSet<>(entry.getValue());
            missing.removeAll(incoming);
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException(
                        "No se permite borrar opciones fisicamente (" + entry.getKey() + "): "
                                + String.join(", ", missing) + ". Inactivalas."
                );
            }
        }
    }

    private Map<String, Set<String>> extractItemCodes(JsonNode categories) {
        Map<String, Set<String>> out = new HashMap<>();
        if (!categories.isArray()) return out;
        for (JsonNode category : categories) {
            String categoryCode = category.path("code").asText();
            Set<String> itemCodes = new HashSet<>();
            for (JsonNode item : category.path("items")) {
                String itemCode = item.path("code").asText();
                if (!itemCode.isBlank()) itemCodes.add(itemCode);
            }
            out.put(categoryCode, itemCodes);
        }
        return out;
    }

    private Map<String, Set<String>> extractOptionCodes(JsonNode categories) {
        Map<String, Set<String>> out = new HashMap<>();
        if (!categories.isArray()) return out;
        for (JsonNode category : categories) {
            for (JsonNode item : category.path("items")) {
                String itemCode = item.path("code").asText();
                if (itemCode.isBlank()) continue;
                Set<String> optionCodes = new HashSet<>();
                for (JsonNode option : item.path("options")) {
                    String optionCode = option.path("code").asText();
                    if (!optionCode.isBlank()) optionCodes.add(optionCode);
                }
                out.put(itemCode, optionCodes);
            }
        }
        return out;
    }

    private String textRequired(JsonNode node, String field, String message) {
        String value = node.path(field).asText();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private void intRequired(JsonNode node, String field, String message) {
        if (!node.has(field) || !node.get(field).canConvertToInt()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void boolRequired(JsonNode node, String field, String message) {
        if (!node.has(field) || !node.get(field).isBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void assertConsultorioExists(UUID consultorioId) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        if (!roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private String resolveActor(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            return "system";
        }
        return userEmail.trim().toLowerCase(Locale.ROOT);
    }
}
