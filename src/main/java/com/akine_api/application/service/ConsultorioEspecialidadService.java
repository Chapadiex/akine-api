package com.akine_api.application.service;

import com.akine_api.application.dto.result.EspecialidadResult;
import com.akine_api.application.port.output.ConsultorioEspecialidadRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.EspecialidadCatalogoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.EspecialidadNotFoundException;
import com.akine_api.domain.model.ConsultorioEspecialidad;
import com.akine_api.domain.model.EspecialidadCatalogo;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ConsultorioEspecialidadService {

    private final ConsultorioEspecialidadRepositoryPort consultorioEspecialidadRepo;
    private final EspecialidadCatalogoRepositoryPort especialidadCatalogoRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ConsultorioEspecialidadService(ConsultorioEspecialidadRepositoryPort consultorioEspecialidadRepo,
                                          EspecialidadCatalogoRepositoryPort especialidadCatalogoRepo,
                                          ConsultorioRepositoryPort consultorioRepo,
                                          UserRepositoryPort userRepo) {
        this.consultorioEspecialidadRepo = consultorioEspecialidadRepo;
        this.especialidadCatalogoRepo = especialidadCatalogoRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResult> list(UUID consultorioId,
                                         String search,
                                         boolean includeInactive,
                                         String userEmail,
                                         Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);

        List<ConsultorioEspecialidad> relations;
        if (search != null && !search.isBlank()) {
            relations = consultorioEspecialidadRepo.findByConsultorioIdAndNombreContaining(
                    consultorioId,
                    search.trim(),
                    includeInactive
            );
        } else if (includeInactive) {
            relations = consultorioEspecialidadRepo.findByConsultorioId(consultorioId);
        } else {
            relations = consultorioEspecialidadRepo.findByConsultorioIdAndActivo(consultorioId, true);
        }

        Map<UUID, EspecialidadCatalogo> catalogById = new HashMap<>();
        List<UUID> ids = relations.stream().map(ConsultorioEspecialidad::getEspecialidadId).distinct().toList();
        for (EspecialidadCatalogo item : especialidadCatalogoRepo.findByIds(ids)) {
            catalogById.put(item.getId(), item);
        }

        return relations.stream()
                .map(rel -> {
                    EspecialidadCatalogo cat = catalogById.get(rel.getEspecialidadId());
                    if (cat == null) {
                        throw new EspecialidadNotFoundException("Especialidad no encontrada: " + rel.getEspecialidadId());
                    }
                    return toResult(rel, cat);
                })
                .sorted(Comparator.comparing(EspecialidadResult::nombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public EspecialidadResult createOrLink(UUID consultorioId,
                                           String nombre,
                                           String userEmail,
                                           Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        String cleanedName = validateNombre(nombre);
        String slug = normalizeSlug(cleanedName);
        if (slug.isBlank()) {
            throw new IllegalArgumentException("El nombre no es valido para generar slug.");
        }

        Instant now = Instant.now();
        EspecialidadCatalogo catalog = especialidadCatalogoRepo.findBySlug(slug)
                .orElseGet(() -> especialidadCatalogoRepo.save(
                        new EspecialidadCatalogo(UUID.randomUUID(), cleanedName, slug, true, now)
                ));

        if (!catalog.isActivo()) {
            catalog.activate();
            catalog = especialidadCatalogoRepo.save(catalog);
        }
        UUID catalogId = catalog.getId();

        ConsultorioEspecialidad relation = consultorioEspecialidadRepo
                .findByConsultorioIdAndEspecialidadId(consultorioId, catalogId)
                .orElseGet(() -> new ConsultorioEspecialidad(UUID.randomUUID(), consultorioId, catalogId, true, now));

        if (!relation.isActivo()) {
            relation.activate();
        }
        relation = consultorioEspecialidadRepo.save(relation);

        return toResult(relation, catalog);
    }

    public EspecialidadResult activate(UUID consultorioId,
                                       UUID especialidadId,
                                       String userEmail,
                                       Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        ConsultorioEspecialidad relation = findRelationOrThrow(consultorioId, especialidadId);
        if (!relation.isActivo()) {
            relation.activate();
            relation = consultorioEspecialidadRepo.save(relation);
        }
        EspecialidadCatalogo catalog = findCatalogOrThrow(especialidadId);
        return toResult(relation, catalog);
    }

    public EspecialidadResult update(UUID consultorioId,
                                     UUID especialidadId,
                                     String nombre,
                                     String userEmail,
                                     Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        ConsultorioEspecialidad relation = findRelationOrThrow(consultorioId, especialidadId);
        EspecialidadCatalogo catalog = findCatalogOrThrow(especialidadId);

        String cleanedName = validateNombre(nombre);
        String slug = normalizeSlug(cleanedName);
        if (slug.isBlank()) {
            throw new IllegalArgumentException("El nombre no es valido para generar slug.");
        }

        especialidadCatalogoRepo.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(especialidadId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una especialidad con ese nombre.");
                });

        if (!catalog.getNombre().equals(cleanedName) || !catalog.getSlug().equals(slug)) {
            catalog.rename(cleanedName, slug);
            catalog = especialidadCatalogoRepo.save(catalog);
        }

        return toResult(relation, catalog);
    }

    public EspecialidadResult deactivate(UUID consultorioId,
                                         UUID especialidadId,
                                         String userEmail,
                                         Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        ConsultorioEspecialidad relation = findRelationOrThrow(consultorioId, especialidadId);
        if (relation.isActivo()) {
            relation.deactivate();
            relation = consultorioEspecialidadRepo.save(relation);
        }
        EspecialidadCatalogo catalog = findCatalogOrThrow(especialidadId);
        return toResult(relation, catalog);
    }

    public void enableDefaultsForConsultorio(UUID consultorioId) {
        ConsultorioStateGuardService.requireExists(consultorioRepo, consultorioId);
        Instant now = Instant.now();
        for (EspecialidadCatalogo catalog : especialidadCatalogoRepo.findAll()) {
            consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(consultorioId, catalog.getId())
                    .orElseGet(() -> consultorioEspecialidadRepo.save(
                            new ConsultorioEspecialidad(UUID.randomUUID(), consultorioId, catalog.getId(), true, now)
                    ));
        }
    }

    String normalizeSlug(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "")
                .replaceAll("-{2,}", "-");
        return normalized;
    }

    private String validateNombre(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        String cleaned = nombre.trim();
        if (cleaned.length() < 3 || cleaned.length() > 80) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 80 caracteres.");
        }
        return cleaned;
    }

    private ConsultorioEspecialidad findRelationOrThrow(UUID consultorioId, UUID especialidadId) {
        return consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(consultorioId, especialidadId)
                .orElseThrow(() -> new EspecialidadNotFoundException("Especialidad no habilitada en consultorio: " + especialidadId));
    }

    private EspecialidadCatalogo findCatalogOrThrow(UUID especialidadId) {
        return especialidadCatalogoRepo.findById(especialidadId)
                .orElseThrow(() -> new EspecialidadNotFoundException("Especialidad no encontrada: " + especialidadId));
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

    private EspecialidadResult toResult(ConsultorioEspecialidad rel, EspecialidadCatalogo cat) {
        return new EspecialidadResult(
                cat.getId(),
                rel.getConsultorioId(),
                cat.getNombre(),
                cat.getSlug(),
                rel.isActivo(),
                rel.getCreatedAt(),
                rel.getUpdatedAt()
        );
    }
}
