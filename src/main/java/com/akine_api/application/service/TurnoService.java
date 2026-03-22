package com.akine_api.application.service;

import com.akine_api.application.dto.command.CambiarEstadoTurnoCommand;
import com.akine_api.application.dto.command.CreateTurnoCommand;
import com.akine_api.application.dto.command.ReprogramarTurnoCommand;
import com.akine_api.application.dto.result.BoxDisponibilidadResult;
import com.akine_api.application.dto.result.HistorialEstadoTurnoResult;
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
    private final HistorialEstadoTurnoRepositoryPort historialRepo;
    private final ConsultorioFeriadoRepositoryPort feriadoRepo;
    private final PacienteRepositoryPort pacienteRepo;

    public TurnoService(TurnoRepositoryPort turnoRepo,
                        ConsultorioRepositoryPort consultorioRepo,
                        ProfesionalRepositoryPort profesionalRepo,
                        ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                        BoxRepositoryPort boxRepo,
                        ConsultorioDuracionTurnoRepositoryPort duracionRepo,
                        ConsultorioHorarioRepositoryPort horarioRepo,
                        DisponibilidadProfesionalRepositoryPort disponibilidadRepo,
                        UserRepositoryPort userRepo,
                        HistorialEstadoTurnoRepositoryPort historialRepo,
                        ConsultorioFeriadoRepositoryPort feriadoRepo,
                        PacienteRepositoryPort pacienteRepo) {
        this.turnoRepo = turnoRepo;
        this.consultorioRepo = consultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.boxRepo = boxRepo;
        this.duracionRepo = duracionRepo;
        this.horarioRepo = horarioRepo;
        this.disponibilidadRepo = disponibilidadRepo;
        this.userRepo = userRepo;
        this.historialRepo = historialRepo;
        this.feriadoRepo = feriadoRepo;
        this.pacienteRepo = pacienteRepo;
    }

    public List<TurnoResult> listByRange(UUID consultorioId, LocalDateTime from, LocalDateTime to,
                                          UUID profesionalIdFilter, UUID boxIdFilter,
                                          TurnoEstado estadoFilter,
                                          String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        List<Turno> turnos = turnoRepo.findByConsultorioIdAndRange(consultorioId, from, to);
        autoCompletarTurnosVencidos(turnos, userEmail);

        // Para PROFESIONAL, solo ve sus propios turnos
        if (roles.contains("ROLE_PROFESIONAL") && !roles.contains("ROLE_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL_ADMIN") && !roles.contains("ROLE_ADMINISTRATIVO")) {
            UUID profId = resolveProfesionalIdByEmail(userEmail);
            if (profId != null) {
                turnos = turnos.stream().filter(t -> profId.equals(t.getProfesionalId())).toList();
            }
        }

        // Filtros opcionales
        if (profesionalIdFilter != null) {
            turnos = turnos.stream().filter(t -> profesionalIdFilter.equals(t.getProfesionalId())).toList();
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

        // Batch lookup pacientes
        Set<UUID> pacienteIds = turnos.stream()
                .map(Turno::getPacienteId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<UUID, Paciente> pacienteMap = new HashMap<>();
        for (UUID pid : pacienteIds) {
            pacienteRepo.findById(pid).ifPresent(p -> pacienteMap.put(pid, p));
        }

        return turnos.stream()
                .map(t -> toResult(t, profesionalMap, boxMap, pacienteMap))
                .toList();
    }

    public TurnoResult create(CreateTurnoCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanManage(cmd.consultorioId(), userEmail, roles);

        UUID profesionalId = cmd.profesionalId();
        if (profesionalId != null) {
            Profesional profesional = profesionalRepo.findById(profesionalId)
                    .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + profesionalId));
            if (!profesional.isActivo()) {
                throw new IllegalArgumentException("El profesional esta de baja y no puede recibir turnos nuevos.");
            }
            if (!profesionalConsultorioRepo.existsByProfesionalIdAndConsultorioId(profesionalId, cmd.consultorioId())) {
                throw new ProfesionalConsultorioNotFoundException(
                        "El profesional no esta asignado a este consultorio");
            }
        }

        // Validar duracion permitida
        assertDuracionAllowed(cmd.consultorioId(), cmd.duracionMinutos());

        LocalDateTime start = cmd.fechaHoraInicio();
        LocalDateTime end = start.plusMinutes(cmd.duracionMinutos());

        // Validar feriado
        assertNotFeriado(cmd.consultorioId(), start.toLocalDate());

        // Validar slot dentro del horario del consultorio
        assertSlotWithinConsultorioHorario(cmd.consultorioId(), start, end);

        // Validar slot dentro de disponibilidad del profesional (si se especifico)
        if (profesionalId != null) {
            assertSlotWithinProfesionalDisponibilidad(profesionalId, cmd.consultorioId(), start, end);
        }

        // Validar sin conflictos
        assertNoConflicts(cmd.consultorioId(), profesionalId, cmd.boxId(), start, end, null);

        // Validar paciente sin turnos solapados
        if (cmd.pacienteId() != null) {
            assertNoPacienteOverlap(cmd.pacienteId(), start, end);
        }

        // Validar box si se especifico
        if (cmd.boxId() != null) {
            Box box = boxRepo.findById(cmd.boxId())
                    .orElseThrow(() -> new BoxNotFoundException("Box no encontrado: " + cmd.boxId()));
            if (!box.isActivo()) {
                throw new IllegalArgumentException("El box está inactivo y no puede asignarse a nuevos turnos.");
            }
        }

        // Resolver creadoPorUserId
        UUID creadoPorUserId = cmd.creadoPorUserId();
        if (creadoPorUserId == null) {
            creadoPorUserId = resolveUserId(userEmail);
        }

        Turno turno = new Turno(
                UUID.randomUUID(),
                cmd.consultorioId(),
                profesionalId,
                cmd.boxId(),
                cmd.pacienteId(),
                cmd.fechaHoraInicio(),
                cmd.duracionMinutos(),
                TurnoEstado.PROGRAMADO,
                cmd.motivoConsulta(),
                cmd.notas(),
                cmd.tipoConsulta(),
                cmd.telefonoContacto(),
                creadoPorUserId,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );

        Turno saved = turnoRepo.save(turno);

        // Registrar historial inicial
        historialRepo.save(new HistorialEstadoTurno(
                UUID.randomUUID(), saved.getId(), null, TurnoEstado.PROGRAMADO,
                creadoPorUserId, null, Instant.now()
        ));

        return toResultSingle(saved);
    }

    public TurnoResult reprogramar(UUID turnoId, ReprogramarTurnoCommand cmd,
                                    String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));

        assertCanManage(turno.getConsultorioId(), userEmail, roles);

        LocalDateTime newStart = cmd.nuevaFechaHoraInicio();
        LocalDateTime newEnd = newStart.plusMinutes(turno.getDuracionMinutos());

        // Validar feriado en nueva fecha
        assertNotFeriado(turno.getConsultorioId(), newStart.toLocalDate());

        assertSlotWithinConsultorioHorario(turno.getConsultorioId(), newStart, newEnd);
        if (turno.getProfesionalId() != null) {
            assertSlotWithinProfesionalDisponibilidad(turno.getProfesionalId(), turno.getConsultorioId(), newStart, newEnd);
        }
        assertNoConflicts(turno.getConsultorioId(), turno.getProfesionalId(), turno.getBoxId(), newStart, newEnd, turnoId);

        TurnoEstado estadoActual = turno.getEstado();
        turno.reprogramar(newStart);
        Turno saved = turnoRepo.save(turno);

        // Registrar historial
        UUID userId = resolveUserId(userEmail);
        historialRepo.save(new HistorialEstadoTurno(
                UUID.randomUUID(), saved.getId(), estadoActual, estadoActual,
                userId, "Reprogramado", Instant.now()
        ));

        return toResultSingle(saved);
    }

    public TurnoResult cambiarEstado(UUID turnoId, CambiarEstadoTurnoCommand cmd,
                                      String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));

        assertCanManage(turno.getConsultorioId(), userEmail, roles);

        TurnoEstado estadoAnterior = turno.getEstado();

        // Si es cancelacion con motivo, usar el metodo cancelar()
        if (cmd.nuevoEstado() == TurnoEstado.CANCELADO && cmd.motivo() != null) {
            UUID canceladoPor = resolveUserId(userEmail);
            turno.cancelar(cmd.motivo(), canceladoPor);
        } else if (cmd.nuevoEstado() == TurnoEstado.EN_CURSO) {
            turno.iniciarAtencion(LocalDateTime.now());
        } else if (cmd.nuevoEstado() == TurnoEstado.COMPLETADO) {
            turno.finalizarAtencion(LocalDateTime.now());
        } else {
            turno.cambiarEstado(cmd.nuevoEstado());
        }

        Turno saved = turnoRepo.save(turno);

        // Registrar historial
        UUID userId = resolveUserId(userEmail);
        historialRepo.save(new HistorialEstadoTurno(
                UUID.randomUUID(), saved.getId(), estadoAnterior, cmd.nuevoEstado(),
                userId, cmd.motivo(), Instant.now()
        ));

        return toResultSingle(saved);
    }

    public TurnoResult realizarCheckIn(UUID turnoId, UUID consultorioId, String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));
        assertCanManage(consultorioId, userEmail, roles);
        TurnoEstado estadoAnterior = turno.getEstado();
        turno.cambiarEstado(TurnoEstado.CHECK_IN_REALIZADO);
        Turno saved = turnoRepo.save(turno);
        UUID userId = resolveUserId(userEmail);
        historialRepo.save(new HistorialEstadoTurno(
                UUID.randomUUID(), saved.getId(), estadoAnterior, TurnoEstado.CHECK_IN_REALIZADO,
                userId, null, Instant.now()
        ));
        return toResultSingle(saved);
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoTurnoResult> getHistorial(UUID turnoId, String userEmail, Set<String> roles) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado: " + turnoId));

        assertCanAccess(turno.getConsultorioId(), userEmail, roles);

        List<HistorialEstadoTurno> historial = historialRepo.findByTurnoId(turnoId);

        // Resolver emails de usuarios (batch)
        Set<UUID> userIds = historial.stream()
                .map(HistorialEstadoTurno::getCambiadoPorUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, String> emailMap = new HashMap<>();
        for (UUID uid : userIds) {
            userRepo.findById(uid).ifPresent(u -> emailMap.put(uid, u.getEmail()));
        }

        return historial.stream()
                .map(h -> new HistorialEstadoTurnoResult(
                        h.getId(), h.getTurnoId(), h.getEstadoAnterior(), h.getEstadoNuevo(),
                        emailMap.getOrDefault(h.getCambiadoPorUserId(), null),
                        h.getMotivo(), h.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoxDisponibilidadResult> getBoxesDisponibilidad(UUID consultorioId, LocalDateTime start,
                                                                 int duracionMinutos,
                                                                 String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        LocalDateTime end = start.plusMinutes(duracionMinutos);
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = start.toLocalDate().plusDays(1).atStartOfDay();

        List<Box> boxes = boxRepo.findByConsultorioId(consultorioId)
                .stream().filter(Box::isActivo).toList();

        List<Turno> turnosDelDia = turnoRepo.findByConsultorioIdAndRange(consultorioId, dayStart, dayEnd);

        return boxes.stream().map(box -> {
            if (box.getCapacityType() == BoxCapacidadTipo.UNLIMITED) {
                return new BoxDisponibilidadResult(box.getId(), box.getNombre(), true, null, null);
            }
            int capacidadTotal = box.getCapacity() != null ? box.getCapacity() : 0;
            int ocupados = (int) turnosDelDia.stream()
                    .filter(t -> box.getId().equals(t.getBoxId()))
                    .filter(t -> consumesCapacity(t.getEstado()))
                    .filter(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start))
                    .count();
            return new BoxDisponibilidadResult(box.getId(), box.getNombre(), ocupados < capacidadTotal, capacidadTotal, ocupados);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<SlotDisponibleResult> getDisponibilidad(UUID consultorioId, UUID profesionalId,
                                                         LocalDate date, int duracionMinutos,
                                                         String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanAccess(consultorioId, userEmail, roles);

        // Si es feriado, no hay slots disponibles
        if (feriadoRepo.existsByConsultorioIdAndFecha(consultorioId, date)) {
            return List.of();
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Obtener horario del consultorio para ese dia
        List<ConsultorioHorario> horarios = horarioRepo
                .findByConsultorioIdAndDiaSemana(consultorioId, dayOfWeek)
                .stream().filter(ConsultorioHorario::isActivo).toList();
        if (horarios.isEmpty()) {
            return List.of();
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        // Obtener turnos existentes para conflictos (no cancelados/ausentes)
        // Sin profesional: no se bloquean slots por turnos existentes del consultorio —
        // la capacidad de cada box es ilimitada por defecto y se valida por separado.
        List<Turno> turnosExistentes;
        if (profesionalId != null) {
            turnosExistentes = turnoRepo.findByProfesionalIdAndRange(profesionalId, dayStart, dayEnd)
                    .stream()
                    .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                    .toList();
        } else {
            turnosExistentes = List.of();
        }

        List<SlotDisponibleResult> slots = new ArrayList<>();
        Set<String> generatedKeys = new HashSet<>();

        if (profesionalId != null) {
            // Con profesional: interseccion horario consultorio x disponibilidad profesional
            List<DisponibilidadProfesional> disponibilidades = disponibilidadRepo
                    .findByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, dayOfWeek)
                    .stream().filter(DisponibilidadProfesional::isActivo).toList();

            if (disponibilidades.isEmpty()) {
                return List.of();
            }

            for (DisponibilidadProfesional disp : disponibilidades) {
                for (ConsultorioHorario horario : horarios) {
                    LocalTime rangeStart = max(disp.getHoraInicio(), horario.getHoraApertura());
                    LocalTime rangeEnd = min(disp.getHoraFin(), horario.getHoraCierre());
                    generateSlots(date, rangeStart, rangeEnd, duracionMinutos, turnosExistentes, generatedKeys, slots);
                }
            }
        } else {
            // Sin profesional: solo horarios del consultorio
            for (ConsultorioHorario horario : horarios) {
                generateSlots(date, horario.getHoraApertura(), horario.getHoraCierre(),
                        duracionMinutos, turnosExistentes, generatedKeys, slots);
            }
        }

        return slots;
    }

    // -- Helpers de validacion --

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
                    "La duracion de " + duracionMinutos + " minutos no esta habilitada para este consultorio");
        }
    }

    private void assertNotFeriado(UUID consultorioId, LocalDate fecha) {
        if (feriadoRepo.existsByConsultorioIdAndFecha(consultorioId, fecha)) {
            throw new FeriadoException("No se puede crear turno en dia feriado: " + fecha);
        }
    }

    private void assertNoPacienteOverlap(UUID pacienteId, LocalDateTime start, LocalDateTime end) {
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = start.toLocalDate().plusDays(1).atStartOfDay();
        List<Turno> turnosPaciente = turnoRepo.findByPacienteIdAndRange(pacienteId, dayStart, dayEnd);
        boolean overlap = turnosPaciente.stream()
                .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                .anyMatch(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start));
        if (overlap) {
            throw new TurnoPacienteSolapadoException("El paciente ya tiene un turno en ese horario");
        }
    }

    private void assertSlotWithinConsultorioHorario(UUID consultorioId, LocalDateTime start, LocalDateTime end) {
        DayOfWeek day = start.getDayOfWeek();
        List<ConsultorioHorario> horarios = horarioRepo.findByConsultorioIdAndDiaSemana(consultorioId, day)
                .stream().filter(ConsultorioHorario::isActivo).toList();
        if (horarios.isEmpty()) {
            throw new SlotNoDisponibleException(
                    "El consultorio no tiene horario configurado para " + day);
        }
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        boolean withinAny = horarios.stream().anyMatch(h ->
                !startTime.isBefore(h.getHoraApertura()) && !endTime.isAfter(h.getHoraCierre()));
        if (!withinAny) {
            throw new SlotNoDisponibleException(
                    "El turno esta fuera del horario del consultorio para " + day);
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
                    "El turno esta fuera de la disponibilidad del profesional para " + day);
        }
    }

    private void assertNoConflicts(UUID consultorioId, UUID profesionalId, UUID boxId,
                                    LocalDateTime start, LocalDateTime end, UUID excludeId) {
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = start.toLocalDate().plusDays(1).atStartOfDay();

        // Conflicto de profesional
        if (profesionalId != null) {
            boolean profesionalConflict = turnoRepo.findByProfesionalIdAndRange(profesionalId, dayStart, dayEnd)
                    .stream()
                    .filter(t -> t.getEstado() != TurnoEstado.CANCELADO && t.getEstado() != TurnoEstado.AUSENTE)
                    .filter(t -> excludeId == null || !t.getId().equals(excludeId))
                    .anyMatch(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start));

            if (profesionalConflict) {
                throw new TurnoConflictException("El profesional ya tiene un turno en ese horario");
            }
        }

        // Conflicto de box: respetar capacidad
        if (boxId != null) {
            Box box = boxRepo.findById(boxId).orElse(null);
            if (box != null && box.getCapacityType() != BoxCapacidadTipo.UNLIMITED) {
                long ocupados = turnoRepo.findByConsultorioIdAndRange(consultorioId, dayStart, dayEnd)
                        .stream()
                        .filter(t -> boxId.equals(t.getBoxId()))
                        .filter(t -> consumesCapacity(t.getEstado()))
                        .filter(t -> excludeId == null || !t.getId().equals(excludeId))
                        .filter(t -> t.getFechaHoraInicio().isBefore(end) && t.getFechaHoraFin().isAfter(start))
                        .count();
                int capacidad = box.getCapacity() != null ? box.getCapacity() : 0;
                if (ocupados >= capacidad) {
                    throw new TurnoConflictException("El box seleccionado no tiene capacidad disponible para el horario indicado.");
                }
            }
            // BoxCapacidadTipo.UNLIMITED: sin restriccion de cantidad
        }
    }

    private boolean consumesCapacity(TurnoEstado estado) {
        return estado != TurnoEstado.CANCELADO
                && estado != TurnoEstado.AUSENTE
                && estado != TurnoEstado.COMPLETADO;
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

    private TurnoResult toResult(Turno t, Map<UUID, Profesional> profesionalMap,
                                  Map<UUID, Box> boxMap, Map<UUID, Paciente> pacienteMap) {
        Profesional prof = profesionalMap.get(t.getProfesionalId());
        Box box = t.getBoxId() != null ? boxMap.get(t.getBoxId()) : null;
        Paciente paciente = t.getPacienteId() != null ? pacienteMap.get(t.getPacienteId()) : null;
        return new TurnoResult(
                t.getId(), t.getConsultorioId(), t.getProfesionalId(),
                prof != null ? prof.getNombre() : null,
                prof != null ? prof.getApellido() : null,
                t.getBoxId(),
                box != null ? box.getNombre() : null,
                t.getPacienteId(),
                paciente != null ? paciente.getNombre() : null,
                paciente != null ? paciente.getApellido() : null,
                paciente != null ? paciente.getDni() : null,
                t.getFechaHoraInicio(), t.getFechaHoraFin(),
                t.getFechaHoraInicioReal(), t.getFechaHoraFinReal(),
                t.getDuracionMinutos(), t.getEstado(),
                t.getTipoConsulta(),
                t.getMotivoConsulta(), t.getNotas(),
                t.getTelefonoContacto(),
                t.getCreadoPorUserId(),
                t.getMotivoCancelacion(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    private TurnoResult toResultSingle(Turno t) {
        Profesional prof = t.getProfesionalId() != null
                ? profesionalRepo.findById(t.getProfesionalId()).orElse(null)
                : null;
        Box box = t.getBoxId() != null ? boxRepo.findById(t.getBoxId()).orElse(null) : null;
        Paciente paciente = t.getPacienteId() != null
                ? pacienteRepo.findById(t.getPacienteId()).orElse(null)
                : null;

        return new TurnoResult(
                t.getId(), t.getConsultorioId(), t.getProfesionalId(),
                prof != null ? prof.getNombre() : null,
                prof != null ? prof.getApellido() : null,
                t.getBoxId(),
                box != null ? box.getNombre() : null,
                t.getPacienteId(),
                paciente != null ? paciente.getNombre() : null,
                paciente != null ? paciente.getApellido() : null,
                paciente != null ? paciente.getDni() : null,
                t.getFechaHoraInicio(), t.getFechaHoraFin(),
                t.getFechaHoraInicioReal(), t.getFechaHoraFinReal(),
                t.getDuracionMinutos(), t.getEstado(),
                t.getTipoConsulta(),
                t.getMotivoConsulta(), t.getNotas(),
                t.getTelefonoContacto(),
                t.getCreadoPorUserId(),
                t.getMotivoCancelacion(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    private void generateSlots(LocalDate date, LocalTime rangeStart, LocalTime rangeEnd,
                                int duracionMinutos, List<Turno> turnosExistentes,
                                Set<String> generatedKeys, List<SlotDisponibleResult> slots) {
        if (!rangeStart.isBefore(rangeEnd)) return;

        LocalTime cursor = rangeStart;
        while (cursor.plusMinutes(duracionMinutos).compareTo(rangeEnd) <= 0) {
            LocalDateTime slotStart = date.atTime(cursor);
            LocalDateTime slotEnd = slotStart.plusMinutes(duracionMinutos);

            boolean conflict = turnosExistentes.stream().anyMatch(t ->
                    t.getFechaHoraInicio().isBefore(slotEnd) && t.getFechaHoraFin().isAfter(slotStart));

            if (!conflict) {
                String key = slotStart + "|" + slotEnd;
                if (generatedKeys.add(key)) {
                    slots.add(new SlotDisponibleResult(slotStart, slotEnd));
                }
            }

            cursor = cursor.plusMinutes(duracionMinutos);
        }
    }

    private LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalTime min(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }

    private void autoCompletarTurnosVencidos(List<Turno> turnos, String userEmail) {
        UUID userId = resolveUserId(userEmail);
        LocalDateTime now = LocalDateTime.now();

        for (Turno turno : turnos) {
            if (turno.getEstado() != TurnoEstado.EN_CURSO) {
                continue;
            }
            if (turno.getFechaHoraFin().isAfter(now)) {
                continue;
            }

            turno.finalizarAtencion(turno.getFechaHoraFin());
            Turno saved = turnoRepo.save(turno);
            historialRepo.save(new HistorialEstadoTurno(
                    UUID.randomUUID(),
                    saved.getId(),
                    TurnoEstado.EN_CURSO,
                    TurnoEstado.COMPLETADO,
                    userId,
                    "Auto-finalizado al superar la hora programada",
                    Instant.now()
            ));
        }
    }
}
