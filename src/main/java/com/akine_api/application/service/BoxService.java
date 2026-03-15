package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateBoxCommand;
import com.akine_api.application.dto.command.UpdateBoxCapacidadCommand;
import com.akine_api.application.dto.command.UpdateBoxCommand;
import com.akine_api.application.dto.result.BoxResult;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.BoxNotFoundException;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.domain.model.Turno;
import com.akine_api.domain.model.TurnoEstado;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class BoxService {

    private final BoxRepositoryPort boxRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final TurnoRepositoryPort turnoRepo;
    private final UserRepositoryPort userRepo;

    public BoxService(BoxRepositoryPort boxRepo,
                      ConsultorioRepositoryPort consultorioRepo,
                      TurnoRepositoryPort turnoRepo,
                      UserRepositoryPort userRepo) {
        this.boxRepo = boxRepo;
        this.consultorioRepo = consultorioRepo;
        this.turnoRepo = turnoRepo;
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
        validateCapacity(cmd.capacityType(), cmd.capacity());
        Box box = new Box(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.nombre(),
                cmd.codigo(),
                cmd.tipo(),
                normalizeCapacityType(cmd.capacityType()),
                normalizeCapacity(cmd.capacityType(), cmd.capacity()),
                cmd.activo() == null || cmd.activo(),
                Instant.now()
        );
        return toResult(boxRepo.save(box));
    }

    public BoxResult update(UpdateBoxCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Box box = boxRepo.findById(cmd.id())
                .filter(b -> b.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.id()));
        validateCapacity(cmd.capacityType(), cmd.capacity());
        validateFutureAssignments(
                box,
                normalizeCapacityType(cmd.capacityType()),
                normalizeCapacity(cmd.capacityType(), cmd.capacity()),
                cmd.activo()
        );
        box.update(cmd.nombre(), cmd.codigo(), cmd.tipo(), cmd.activo());
        box.updateCapacidad(cmd.capacityType(), cmd.capacity());
        return toResult(boxRepo.save(box));
    }

    public BoxResult updateCapacidad(UpdateBoxCapacidadCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Box box = boxRepo.findById(cmd.id())
                .filter(b -> b.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.id()));
        validateCapacity(cmd.capacityType(), cmd.capacity());
        validateFutureAssignments(
                box,
                normalizeCapacityType(cmd.capacityType()),
                normalizeCapacity(cmd.capacityType(), cmd.capacity()),
                null
        );
        box.updateCapacidad(cmd.capacityType(), cmd.capacity());
        return toResult(boxRepo.save(box));
    }

    public void inactivate(UUID consultorioId, UUID boxId, String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Box box = boxRepo.findById(boxId)
                .filter(b -> b.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + boxId));
        if (!getFutureAssignedTurnos(consultorioId, boxId).isEmpty()) {
            throw new IllegalArgumentException("El box tiene turnos futuros asignados. Revise la agenda antes de inactivarlo.");
        }
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

    private void validateCapacity(BoxCapacidadTipo capacityType, Integer capacity) {
        if (normalizeCapacityType(capacityType) == BoxCapacidadTipo.LIMITED && (capacity == null || capacity <= 0)) {
            throw new IllegalArgumentException("La capacidad es obligatoria cuando el box es limitado.");
        }
    }

    private void validateFutureAssignments(Box box, BoxCapacidadTipo newCapacityType, Integer newCapacity, Boolean newActivo) {
        List<Turno> futureTurnos = getFutureAssignedTurnos(box.getConsultorioId(), box.getId());
        boolean willBeInactive = newActivo != null ? !newActivo : !box.isActivo();

        if (willBeInactive && !futureTurnos.isEmpty()) {
            throw new IllegalArgumentException("El box tiene turnos futuros asignados. Revise la agenda antes de inactivarlo.");
        }
        if (newCapacityType != BoxCapacidadTipo.LIMITED) {
            return;
        }

        long maxOverlap = futureTurnos.stream()
                .mapToLong(turno -> futureTurnos.stream()
                        .filter(other -> turno.getFechaHoraInicio().isBefore(other.getFechaHoraFin())
                                && turno.getFechaHoraFin().isAfter(other.getFechaHoraInicio()))
                        .count())
                .max()
                .orElse(0L);

        if (maxOverlap > (newCapacity != null ? newCapacity : 0)) {
            throw new IllegalArgumentException(
                    "No se puede guardar la nueva capacidad porque existen turnos asignados que superan el límite definido para este box.");
        }
    }

    private List<Turno> getFutureAssignedTurnos(UUID consultorioId, UUID boxId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime horizon = now.plusYears(5);
        return turnoRepo.findByConsultorioIdAndRange(consultorioId, now, horizon).stream()
                .filter(turno -> boxId.equals(turno.getBoxId()))
                .filter(turno -> consumesCapacity(turno.getEstado()))
                .toList();
    }

    private boolean consumesCapacity(TurnoEstado estado) {
        return estado != TurnoEstado.CANCELADO
                && estado != TurnoEstado.AUSENTE
                && estado != TurnoEstado.COMPLETADO;
    }

    private BoxCapacidadTipo normalizeCapacityType(BoxCapacidadTipo capacityType) {
        return capacityType == null ? BoxCapacidadTipo.UNLIMITED : capacityType;
    }

    private Integer normalizeCapacity(BoxCapacidadTipo capacityType, Integer capacity) {
        return normalizeCapacityType(capacityType) == BoxCapacidadTipo.UNLIMITED ? null : capacity;
    }
}
