package com.akine_api.application.service;

import com.akine_api.application.dto.command.CambiarEstadoTurnoCommand;
import com.akine_api.application.dto.command.CreateTurnoCommand;
import com.akine_api.application.dto.command.ReprogramarTurnoCommand;
import com.akine_api.application.dto.result.SlotDisponibleResult;
import com.akine_api.application.dto.result.TurnoResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.*;
import com.akine_api.domain.model.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TurnoService {

    private final TurnoRepositoryPort turnoRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final BoxRepositoryPort boxRepo;
    private final ConsultorioDuracionTurnoRepositoryPort duracionRepo;
    private final ConsultorioHorarioRepositoryPort horarioRepo;
    private final DisponibilidadProfesionalRepositoryPort disponibilidadRepo;
    private final UserRepositoryPort userRepo;

    public TurnoService(TurnoRepositoryPort turnoRepo,
                        ConsultorioRepositoryPort consultorioRepo,
                        ProfesionalRepositoryPort profesionalRepo,
                        ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                        BoxRepositoryPort boxRepo,
                        ConsultorioDuracionTurnoRepositoryPort duracionRepo,
                        ConsultorioHorarioRepositoryPort horarioRepo,
                        DisponibilidadProfesionalRepositoryPort disponibilidadRepo,
                        UserRepositoryPort userRepo) {
        this.turnoRepo = turnoRepo;
        this.consultorioRepo = consultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.boxRepo = boxRepo;
        this.duracionRepo = duracionRepo;
        this.horarioRepo = horarioRepo;
        this.disponibilidadRepo = disponibilidadRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<TurnoResult> listByRange(UUID consultorioId, LocalDateTime from, LocalDateTime to,
                                          UUID profesionalIdFilter, UUID boxIdFilter,
                                          TurnoEstado estadoFilter,
                                          String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        List<Turno> turnos = turnoRepo.findByConsultorioIdAndRange(consultorioId, from, to);

        // Para PROFESIONAL, solo ve sus propios turnos
        if (roles.contains("ROLE_PROFESIONAL") && !roles.contains("ROLE_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL_ADMIN") && !roles.contains("ROLE_ADMINISTRATIVO")) {
            UUID profId = resolveProfesionalIdByEmail(userEmail);
            if (profId != null) {
                turnos = turnos.stream().filter(t -> t.getProfesionalId().equals(profId)).toList();
            }
        }

        // Filtros opcionales
        if (profesionalIdFilter != null) {
            turnos = turnos.stream().filter(t -> t.getProfesionalId().equals(profesionalIdFilter)).toList();
        }
        if (boxIdFilter != null) {
            turnos = turnos.stream().filter(t -> boxIdFilter.equals(t.getBoxId())).toList();
        }
        if (estadoFilter != null) {
            turnos = turnos.stream().filter(t -> t.getEstado() == estadoFilter).toList();
        }

        // Enriquecer con nombres (batch lookup para evitar N+1)
        Map<UUID, Profesional> profesionalMap = profesionalRepo.findByConsultorioId(consultorioId)
                .stream().collect(Collectors.toMap(Profesional::getId, p -> p, (a, b) -> a));
        Map<UUID, Box> boxMap = boxRepo.findByConsultorioId(consultorioId)
                .stream().collect(Collectors.toMap(Box::getId, b -> b, (a, b) -> a));

        return turnos.stream()
                .map(t -> toResult(t, profesionalMap, boxMap))
                .toList();
    }

    public TurnoResult create(CreateTurnoCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanManage(cmd.consultorioId(), userEmail, roles);

        // Validar profesional asignado al consultorio
        profesionalRepo.findById(cmd.profesionalId())
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + cmd.profesionalId()));
        if (!profesionalConsultorioRepo.existsByProfesionalIdAndConsultorioId(cmd.profesionalId(), cmd.consultorioId())) {
            throw new ProfesionalConsultorioNotFoundException(
                    "El profesional no está asignado a este consultorio");
        }

        // Validar duración permitida
        assertDuracionAllowed(cmd.consultorioId(), cmd.duracionMinutos());

        // Validar slot dentro del horario del consultorio
        LocalDateTime start = cmd.fechaHoraInicio();
        LocalDateTime end = start.plusMinutes(cmd.duracionMinutos());
        assertSlotWithinConsultorioHorario(cmd.consultorioId(), start, end);

        // Validar slot dentro de disponibilidad del profesional
        assertSlotWithinProfesionalDisponibilidad(cmd.profesionalId(), cmd.consultorioId(), start, end);

        // Validar sin conflictos
        assertNoConflicts(cmd.consultorioId(), cmd.profesionalId(), cmd.boxId(), start, end, null);

        // Validar box si se especificó
        if (cmd.boxId() != null) {
            boxRepo.findById(cmd.boxId())
                    .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.boxId()));
        }

        Turno turno = new Turno(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.profesionalId(),
                cmd.boxId(),
                cmd.pacienteId(),
                cmd.fechaHoraInicio(),
                cmd.duracionMinutos(),
                TurnoEstado.PROGRAMADO,
                cmd.motivoConsulta(),
                cmd.notas(),
                Instant.now()
        );

        Turno saved = turnoRepo.save(turno);
        return toResultSingle(saved);
    }

    public TurnoResult reprogramar(UUID turnoId, ReprogramarTurnoCommand cmd,
                                    String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));

        assertCanManage(turno.getConsultorioId(), userEmail, roles);

        LocalDateTime newStart = cmd.nuevaFechaHoraInicio();
        LocalDateTime newEnd = newStart.plusMinutes(turno.getDuracionMinutos());

        assertSlotWithinConsultorioHorario(turno.getConsultorioId(), newStart, newEnd);
        assertSlotWithinProfesionalDisponibilidad(turno.getProfesionalId(), turno.getConsultorioId(), newStart, newEnd);
        assertNoConflicts(turno.getConsultorioId(), turno.getProfesionalId(), turno.getBoxId(), newStart, newEnd, turnoId);

        turno.reprogramar(newStart);
        Turno saved = turnoRepo.save(turno);
        return toResultSingle(saved);
    }

    public TurnoResult cambiarEstado(UUID turnoId, CambiarEstadoTurnoCommand cmd,
                                      String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));

        assertCanManage(turno.getConsultorioId(), userEmail, roles);

        turno.cambiarEstado(cmd.nuevoEstado());
        Turno saved = turnoRepo.save(turno);
        return toResultSingle(saved);
    }

    @Transactional(readOnly = true)
    public List<SlotDisponibleResult> getDisponibilidad(UUID consultorioId, UUID profesionalId,
                                                         LocalDate date, int duracionMinutos,
                                                         String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Obtener horario del consultorio para ese día
        Optional<ConsultorioHorario> horarioOpt = horarioRepo
                .findByConsultorioIdAndDiaSemana(consultorioId, dayOfWeek);
        if (horarioOpt.isEmpty() || !horarioOpt.get().isActivo()) {
            return List.of();
        }
        ConsultorioHorario horario = horarioOpt.get();

        // Obtener disponibilidad del profesional para ese día
        List<DisponibilidadProfesional> disponibilidades = disponibilidadRepo
                .findByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, dayOfWeek)
                .stream().filter(DisponibilidadProfesional::isActivo).toList();

        if (disponibilidades.isEmpty()) {
            return List.of();
        }

        // Obtener turnos existentes del profesional para ese día (no cancelados)
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Turno> turnosExistentes = turnoRepo.findByProfesionalIdAndRange(profesionalId, dayStart, dayEnd)
                .stream()
                .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                .toList();

        List<SlotDisponibleResult> slots = new ArrayList<>();

        for (DisponibilidadProfesional disp : disponibilidades) {
            // Intersectar con horario del consultorio
            LocalTime rangeStart = max(disp.getHoraInicio(), horario.getHoraApertura());
            LocalTime rangeEnd = min(disp.getHoraFin(), horario.getHoraCierre());

            if (!rangeStart.isBefore(rangeEnd)) continue;

            // Generar slots de duración especificada
            LocalTime cursor = rangeStart;
            while (cursor.plusMinutes(duracionMinutos).compareTo(rangeEnd) <= 0) {
                LocalDateTime slotStart = date.atTime(cursor);
                LocalDateTime slotEnd = slotStart.plusMinutes(duracionMinutos);

                // Verificar que no hay conflicto con turnos existentes
                boolean conflict = turnosExistentes.stream().anyMatch(t ->
                        t.getFechaHoraInicio().isBefore(slotEnd) && t.getFechaHoraFin().isAfter(slotStart));

                if (!conflict) {
                    slots.add(new SlotDisponibleResult(slotStart, slotEnd));
                }

                cursor = cursor.plusMinutes(duracionMinutos);
            }
        }

        return slots;
    }

    // ── Helpers de validación ──────────────────────────────────────────

    private void assertConsultorioExists(UUID consultorioId) {
        consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + consultorioId));
    }

    private void assertCanAccess(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanManage(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        if (roles.contains("ROLE_PROFESIONAL_ADMIN") || roles.contains("ROLE_ADMINISTRATIVO")
                || roles.contains("ROLE_PROFESIONAL")) {
            UUID userId = resolveUserId(userEmail);
            List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
            if (!ids.contains(consultorioId)) {
                throw new AccessDeniedException("Sin acceso a este consultorio");
            }
            return;
        }
        throw new AccessDeniedException("Permiso denegado");
    }

    private void assertDuracionAllowed(UUID consultorioId, int duracionMinutos) {
        boolean allowed = duracionRepo.existsByConsultorioIdAndMinutos(consultorioId, duracionMinutos);
        if (!allowed) {
            throw new SlotNoDisponibleException(
                    "La duración de " + duracionMinutos + " minutos no está habilitada para este consultorio");
        }
    }

    private void assertSlotWithinConsultorioHorario(UUID consultorioId, LocalDateTime start, LocalDateTime end) {
        DayOfWeek day = start.getDayOfWeek();
        Optional<ConsultorioHorario> horarioOpt = horarioRepo.findByConsultorioIdAndDiaSemana(consultorioId, day);
        if (horarioOpt.isEmpty() || !horarioOpt.get().isActivo()) {
            throw new SlotNoDisponibleException(
                    "El consultorio no tiene horario configurado para " + day);
        }
        ConsultorioHorario horario = horarioOpt.get();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        if (startTime.isBefore(horario.getHoraApertura()) || endTime.isAfter(horario.getHoraCierre())) {
            throw new SlotNoDisponibleException(
                    "El turno está fuera del horario del consultorio (" +
                            horario.getHoraApertura() + " - " + horario.getHoraCierre() + ")");
        }
    }

    private void assertSlotWithinProfesionalDisponibilidad(UUID profesionalId, UUID consultorioId,
                                                            LocalDateTime start, LocalDateTime end) {
        DayOfWeek day = start.getDayOfWeek();
        List<DisponibilidadProfesional> disponibilidades = disponibilidadRepo
                .findByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, day)
                .stream().filter(DisponibilidadProfesional::isActivo).toList();

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        boolean withinAny = disponibilidades.stream().anyMatch(d ->
                !startTime.isBefore(d.getHoraInicio()) && !endTime.isAfter(d.getHoraFin()));

        if (!withinAny) {
            throw new SlotNoDisponibleException(
                    "El turno está fuera de la disponibilidad del profesional para " + day);
        }
    }

    private void assertNoConflicts(UUID consultorioId, UUID profesionalId, UUID boxId,
                                    LocalDateTime start, LocalDateTime end, UUID excludeId) {
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = start.toLocalDate().plusDays(1).atStartOfDay();

        // Conflicto de profesional
        boolean profesionalConflict = turnoRepo.findByProfesionalIdAndRange(profesionalId, dayStart, dayEnd)
                .stream()
                .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                .filter(t -> excludeId == null || !t.getId().equals(excludeId))
                .anyMatch(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start));

        if (profesionalConflict) {
            throw new TurnoConflictException("El profesional ya tiene un turno en ese horario");
        }

        // Conflicto de box (turnos de cualquier profesional en el mismo box)
        if (boxId != null) {
            boolean boxConflict = turnoRepo.findByConsultorioIdAndRange(consultorioId, dayStart, dayEnd)
                    .stream()
                    .filter(t -> boxId.equals(t.getBoxId()))
                    .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                    .filter(t -> excludeId == null || !t.getId().equals(excludeId))
                    .anyMatch(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start));

            if (boxConflict) {
                throw new TurnoConflictException("El box ya está ocupado en ese horario");
            }
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private UUID resolveProfesionalIdByEmail(String email) {
        return profesionalRepo.findByEmail(email)
                .map(Profesional::getId)
                .orElse(null);
    }

    private TurnoResult toResult(Turno t, Map<UUID, Profesional> profesionalMap, Map<UUID, Box> boxMap) {
        Profesional prof = profesionalMap.get(t.getProfesionalId());
        Box box = t.getBoxId() != null ? boxMap.get(t.getBoxId()) : null;
        return new TurnoResult(
                t.getId(), t.getConsultorioId(), t.getProfesionalId(),
                prof != null ? prof.getNombre() : null,
                prof != null ? prof.getApellido() : null,
                t.getBoxId(),
                box != null ? box.getNombre() : null,
                t.getPacienteId(),
                t.getFechaHoraInicio(), t.getFechaHoraFin(),
                t.getDuracionMinutos(), t.getEstado(),
                t.getMotivoConsulta(), t.getNotas(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    private TurnoResult toResultSingle(Turno t) {
        Profesional prof = profesionalRepo.findById(t.getProfesionalId()).orElse(null);
        Box box = t.getBoxId() != null ? boxRepo.findById(t.getBoxId()).orElse(null) : null;

        return new TurnoResult(
                t.getId(), t.getConsultorioId(), t.getProfesionalId(),
                prof != null ? prof.getNombre() : null,
                prof != null ? prof.getApellido() : null,
                t.getBoxId(),
                box != null ? box.getNombre() : null,
                t.getPacienteId(),
                t.getFechaHoraInicio(), t.getFechaHoraFin(),
                t.getDuracionMinutos(), t.getEstado(),
                t.getMotivoConsulta(), t.getNotas(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    private LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalTime min(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }
}
