package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateBoxCommand;
import com.akine_api.application.dto.command.UpdateBoxCapacidadCommand;
import com.akine_api.application.dto.command.UpdateBoxCommand;
import com.akine_api.application.dto.result.BoxResult;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.BoxNotFoundException;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class BoxService {

    private final BoxRepositoryPort boxRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public BoxService(BoxRepositoryPort boxRepo,
                      ConsultorioRepositoryPort consultorioRepo,
                      UserRepositoryPort userRepo) {
        this.boxRepo = boxRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<BoxResult> list(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return boxRepo.findByConsultorioId(consultorioId).stream().map(this::toResult).toList();
    }

    @Transactional(readOnly = true)
    public BoxResult getById(UUID consultorioId, UUID boxId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        Box box = boxRepo.findById(boxId)
                .filter(b -> b.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + boxId));
        return toResult(box);
    }

    public BoxResult create(CreateBoxCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        if (cmd.codigo() != null && !cmd.codigo().isBlank()
                && boxRepo.existsByCodigoAndConsultorioId(cmd.codigo(), cmd.consultorioId())) {
            throw new IllegalArgumentException("El código '" + cmd.codigo() + "' ya existe en este consultorio");
        }
        Box box = new Box(UUID.randomUUID(), cmd.consultorioId(), cmd.nombre(),
                cmd.codigo(), cmd.tipo(), BoxCapacidadTipo.UNLIMITED, null, true, Instant.now());
        return toResult(boxRepo.save(box));
    }

    public BoxResult update(UpdateBoxCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Box box = boxRepo.findById(cmd.id())
                .filter(b -> b.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.id()));
        box.update(cmd.nombre(), cmd.codigo(), cmd.tipo(), cmd.activo());
        return toResult(boxRepo.save(box));
    }

    public BoxResult updateCapacidad(UpdateBoxCapacidadCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Box box = boxRepo.findById(cmd.id())
                .filter(b -> b.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.id()));
        if (cmd.capacityType() == BoxCapacidadTipo.LIMITED && (cmd.capacity() == null || cmd.capacity() <= 0)) {
            throw new IllegalArgumentException("Si la capacidad es LIMITED, capacity debe ser > 0");
        }
        box.updateCapacidad(cmd.capacityType(), cmd.capacity());
        return toResult(boxRepo.save(box));
    }

    public void inactivate(UUID consultorioId, UUID boxId, String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Box box = boxRepo.findById(boxId)
                .filter(b -> b.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + boxId));
        box.inactivate();
        boxRepo.save(box);
    }

    public BoxResult activate(UUID consultorioId, UUID boxId, String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Box box = boxRepo.findById(boxId)
                .filter(b -> b.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + boxId));
        box.activate();
        return toResult(boxRepo.save(box));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

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

    private BoxResult toResult(Box b) {
        return new BoxResult(b.getId(), b.getConsultorioId(), b.getNombre(),
                b.getCodigo(), b.getTipo(), b.getCapacityType(), b.getCapacity(), b.isActivo(),
                b.getCreatedAt(), b.getUpdatedAt());
    }
}
