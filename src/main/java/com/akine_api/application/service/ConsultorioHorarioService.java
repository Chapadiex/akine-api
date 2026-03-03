package com.akine_api.application.service;

import com.akine_api.application.dto.command.DeleteHorarioConsultorioCommand;
import com.akine_api.application.dto.command.SetHorarioConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioHorarioResult;
import com.akine_api.application.port.output.ConsultorioHorarioRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioHorarioNotFoundException;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.ConsultorioHorario;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioHorarioService {

    private final ConsultorioHorarioRepositoryPort horarioRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ConsultorioHorarioService(ConsultorioHorarioRepositoryPort horarioRepo,
                                     ConsultorioRepositoryPort consultorioRepo,
                                     UserRepositoryPort userRepo) {
        this.horarioRepo = horarioRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioHorarioResult> list(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return horarioRepo.findByConsultorioId(consultorioId).stream()
                .map(this::toResult)
                .toList();
    }

    public ConsultorioHorarioResult set(SetHorarioConsultorioCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        horarioRepo.deleteByConsultorioIdAndDiaSemana(cmd.consultorioId(), cmd.diaSemana());
        ConsultorioHorario horario = new ConsultorioHorario(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.diaSemana(),
                cmd.horaApertura(),
                cmd.horaCierre(),
                true
        );
        return toResult(horarioRepo.save(horario));
    }

    public ConsultorioHorarioResult add(SetHorarioConsultorioCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        validateNoOverlap(cmd.consultorioId(), cmd.diaSemana(), cmd.horaApertura(), cmd.horaCierre());

        ConsultorioHorario horario = new ConsultorioHorario(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.diaSemana(),
                cmd.horaApertura(),
                cmd.horaCierre(),
                true
        );
        return toResult(horarioRepo.save(horario));
    }

    public List<ConsultorioHorarioResult> addBatch(UUID consultorioId,
                                                   List<SetHorarioConsultorioCommand> commands,
                                                   String userEmail,
                                                   Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos una franja horaria");
        }

        Map<DayOfWeek, List<ConsultorioHorario>> byDay = new HashMap<>();
        for (ConsultorioHorario existing : horarioRepo.findByConsultorioId(consultorioId)) {
            if (!existing.isActivo()) continue;
            byDay.computeIfAbsent(existing.getDiaSemana(), k -> new ArrayList<>()).add(existing);
        }

        for (SetHorarioConsultorioCommand cmd : commands) {
            if (!consultorioId.equals(cmd.consultorioId())) {
                throw new IllegalArgumentException("Todos los tramos deben pertenecer al mismo consultorio");
            }
            validateNoOverlap(byDay.getOrDefault(cmd.diaSemana(), List.of()), cmd.diaSemana(), cmd.horaApertura(), cmd.horaCierre());

            ConsultorioHorario staged = new ConsultorioHorario(
                    UUID.randomUUID(),
                    consultorioId,
                    cmd.diaSemana(),
                    cmd.horaApertura(),
                    cmd.horaCierre(),
                    true
            );
            byDay.computeIfAbsent(cmd.diaSemana(), k -> new ArrayList<>()).add(staged);
        }

        return commands.stream()
                .map(cmd -> new ConsultorioHorario(
                        UUID.randomUUID(),
                        consultorioId,
                        cmd.diaSemana(),
                        cmd.horaApertura(),
                        cmd.horaCierre(),
                        true
                ))
                .map(horarioRepo::save)
                .map(this::toResult)
                .toList();
    }

    public void delete(DeleteHorarioConsultorioCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        List<ConsultorioHorario> horarios = horarioRepo
                .findByConsultorioIdAndDiaSemana(cmd.consultorioId(), cmd.diaSemana());
        if (horarios.isEmpty()) {
            throw new ConsultorioHorarioNotFoundException("Horario no encontrado para " + cmd.diaSemana());
        }
        horarioRepo.deleteByConsultorioIdAndDiaSemana(cmd.consultorioId(), cmd.diaSemana());
    }

    public void deleteById(UUID consultorioId, UUID horarioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        ConsultorioHorario horario = horarioRepo.findByConsultorioId(consultorioId).stream()
                .filter(h -> h.getId().equals(horarioId))
                .findFirst()
                .orElseThrow(() -> new ConsultorioHorarioNotFoundException("Horario no encontrado: " + horarioId));
        horarioRepo.deleteById(horario.getId());
    }

    private void assertConsultorioExists(UUID consultorioId) {
        consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + consultorioId));
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
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

    private void validateNoOverlap(UUID consultorioId, java.time.DayOfWeek diaSemana,
                                   LocalTime apertura, LocalTime cierre) {
        boolean overlap = horarioRepo.findByConsultorioIdAndDiaSemana(consultorioId, diaSemana).stream()
                .filter(ConsultorioHorario::isActivo)
                .anyMatch(existing -> apertura.isBefore(existing.getHoraCierre()) && cierre.isAfter(existing.getHoraApertura()));
        if (overlap) {
            throw new IllegalArgumentException("La franja horaria se solapa con otra ya configurada para el mismo dia");
        }
    }

    private void validateNoOverlap(List<ConsultorioHorario> dayHorarios, java.time.DayOfWeek diaSemana,
                                   LocalTime apertura, LocalTime cierre) {
        boolean overlap = dayHorarios.stream()
                .filter(ConsultorioHorario::isActivo)
                .anyMatch(existing -> apertura.isBefore(existing.getHoraCierre()) && cierre.isAfter(existing.getHoraApertura()));
        if (overlap) {
            throw new IllegalArgumentException("La franja horaria se solapa en " + diaSemana);
        }
    }

    private ConsultorioHorarioResult toResult(ConsultorioHorario h) {
        return new ConsultorioHorarioResult(
                h.getId(),
                h.getConsultorioId(),
                h.getDiaSemana(),
                h.getHoraApertura(),
                h.getHoraCierre(),
                h.isActivo()
        );
    }
}
