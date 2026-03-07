package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateFeriadoCommand;
import com.akine_api.application.dto.result.ConsultorioFeriadoResult;
import com.akine_api.application.port.output.ConsultorioFeriadoRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.FeriadoNacionalProviderPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.dto.result.FeriadoSyncResult;
import com.akine_api.domain.model.ConsultorioFeriado;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioFeriadoService {

    private final ConsultorioFeriadoRepositoryPort feriadoRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final FeriadoNacionalProviderPort feriadoNacionalProvider;

    public ConsultorioFeriadoService(ConsultorioFeriadoRepositoryPort feriadoRepo,
                                      ConsultorioRepositoryPort consultorioRepo,
                                      UserRepositoryPort userRepo,
                                      FeriadoNacionalProviderPort feriadoNacionalProvider) {
        this.feriadoRepo = feriadoRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.feriadoNacionalProvider = feriadoNacionalProvider;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioFeriadoResult> list(UUID consultorioId, int year,
                                                String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        return feriadoRepo.findByConsultorioIdAndYear(consultorioId, year).stream()
                .map(this::toResult)
                .toList();
    }

    public ConsultorioFeriadoResult create(CreateFeriadoCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanManage(cmd.consultorioId(), userEmail, roles);

        if (feriadoRepo.existsByConsultorioIdAndFecha(cmd.consultorioId(), cmd.fecha())) {
            throw new IllegalArgumentException("Ya existe un feriado para esa fecha en este consultorio");
        }

        ConsultorioFeriado feriado = new ConsultorioFeriado(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.fecha(),
                cmd.descripcion(),
                Instant.now()
        );

        return toResult(feriadoRepo.save(feriado));
    }

    public void delete(UUID consultorioId, UUID feriadoId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanManage(consultorioId, userEmail, roles);

        ConsultorioFeriado feriado = feriadoRepo.findById(feriadoId)
                .orElseThrow(() -> new IllegalArgumentException("Feriado no encontrado: " + feriadoId));

        if (!feriado.getConsultorioId().equals(consultorioId)) {
            throw new AccessDeniedException("El feriado no pertenece a este consultorio");
        }

        feriadoRepo.deleteById(feriadoId);
    }

    public FeriadoSyncResult syncNacionales(UUID consultorioId, int year, String userEmail, Set<String> roles) {
        assertValidYear(year);
        assertConsultorioExists(consultorioId);
        assertCanManage(consultorioId, userEmail, roles);

        List<FeriadoNacionalProviderPort.FeriadoNacionalItem> fetchedItems = feriadoNacionalProvider.findByYear(year);
        Map<LocalDate, FeriadoNacionalProviderPort.FeriadoNacionalItem> fetchedByDate = new LinkedHashMap<>();
        for (FeriadoNacionalProviderPort.FeriadoNacionalItem item : fetchedItems) {
            fetchedByDate.putIfAbsent(item.fecha(), item);
        }

        Set<LocalDate> existingDates = feriadoRepo.findByConsultorioIdAndYear(consultorioId, year).stream()
                .map(ConsultorioFeriado::getFecha)
                .collect(java.util.stream.Collectors.toSet());

        int created = 0;
        for (FeriadoNacionalProviderPort.FeriadoNacionalItem item : fetchedByDate.values()) {
            if (existingDates.contains(item.fecha())) {
                continue;
            }
            ConsultorioFeriado feriado = new ConsultorioFeriado(
                    UUID.randomUUID(),
                    consultorioId,
                    item.fecha(),
                    buildDescripcion(item),
                    Instant.now()
            );
            feriadoRepo.save(feriado);
            created++;
        }

        int fetched = fetchedByDate.size();
        int skipped = fetched - created;
        return new FeriadoSyncResult(year, fetched, created, skipped);
    }

    private ConsultorioFeriadoResult toResult(ConsultorioFeriado f) {
        return new ConsultorioFeriadoResult(f.getId(), f.getConsultorioId(), f.getFecha(),
                f.getDescripcion(), f.getCreatedAt());
    }

    private void assertConsultorioExists(UUID consultorioId) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
    }

    private void assertCanAccess(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanManage(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        if (roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            UUID userId = resolveUserId(userEmail);
            List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
            if (!ids.contains(consultorioId)) {
                throw new AccessDeniedException("Sin acceso a este consultorio");
            }
            return;
        }
        throw new AccessDeniedException("Solo ADMIN o PROFESIONAL_ADMIN pueden gestionar feriados");
    }

    private void assertValidYear(int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("El anio debe estar entre 2000 y 2100");
        }
    }

    private String buildDescripcion(FeriadoNacionalProviderPort.FeriadoNacionalItem item) {
        String descripcion = item.nombre();
        if (item.tipo() != null && !item.tipo().isBlank()) {
            descripcion = descripcion + " (" + item.tipo() + ")";
        }
        if (descripcion.length() > 200) {
            return descripcion.substring(0, 200);
        }
        return descripcion;
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
