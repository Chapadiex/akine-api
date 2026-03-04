package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateDisponibilidadCommand;
import com.akine_api.application.dto.command.DeleteDisponibilidadCommand;
import com.akine_api.application.dto.command.UpdateDisponibilidadCommand;
import com.akine_api.application.dto.result.DisponibilidadProfesionalResult;
import com.akine_api.application.port.output.ConsultorioHorarioRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DisponibilidadProfesionalRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioHorarioNotFoundException;
import com.akine_api.domain.exception.DisponibilidadFueraDeHorarioException;
import com.akine_api.domain.exception.DisponibilidadProfesionalNotFoundException;
import com.akine_api.domain.exception.DisponibilidadSolapamientoException;
import com.akine_api.domain.exception.ProfesionalConsultorioNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.ConsultorioHorario;
import com.akine_api.domain.model.DisponibilidadProfesional;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class DisponibilidadProfesionalService {

    private final DisponibilidadProfesionalRepositoryPort disponibilidadRepo;
    private final ConsultorioHorarioRepositoryPort horarioRepo;
    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public DisponibilidadProfesionalService(DisponibilidadProfesionalRepositoryPort disponibilidadRepo,
                                            ConsultorioHorarioRepositoryPort horarioRepo,
                                            ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                                            ProfesionalRepositoryPort profesionalRepo,
                                            ConsultorioRepositoryPort consultorioRepo,
                                            UserRepositoryPort userRepo) {
        this.disponibilidadRepo = disponibilidadRepo;
        this.horarioRepo = horarioRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadProfesionalResult> list(UUID profesionalId, UUID consultorioId, String userEmail, Set<String> roles) {
        assertProfesionalExists(profesionalId);
        assertConsultorioExists(consultorioId);
        assertCanManage(profesionalId, consultorioId, userEmail, roles);
        return disponibilidadRepo.findByProfesionalIdAndConsultorioId(profesionalId, consultorioId).stream()
                .map(this::toResult)
                .toList();
    }

    public DisponibilidadProfesionalResult create(CreateDisponibilidadCommand cmd, String userEmail, Set<String> roles) {
        assertProfesionalExists(cmd.profesionalId());
        assertConsultorioExists(cmd.consultorioId());
        assertCanManage(cmd.profesionalId(), cmd.consultorioId(), userEmail, roles);
        validateAsignacionActiva(cmd.profesionalId(), cmd.consultorioId());
        validateDentroHorarioConsultorio(cmd.consultorioId(), cmd);
        validateSinSolapamiento(cmd.profesionalId(), cmd.consultorioId(), cmd.diaSemana(), cmd.horaInicio(), cmd.horaFin(), null);

        DisponibilidadProfesional saved = disponibilidadRepo.save(new DisponibilidadProfesional(
                UUID.randomUUID(),
                cmd.profesionalId(),
                cmd.consultorioId(),
                cmd.diaSemana(),
                cmd.horaInicio(),
                cmd.horaFin(),
                true
        ));
        return toResult(saved);
    }

    public DisponibilidadProfesionalResult update(UpdateDisponibilidadCommand cmd, String userEmail, Set<String> roles) {
        assertProfesionalExists(cmd.profesionalId());
        assertConsultorioExists(cmd.consultorioId());
        assertCanManage(cmd.profesionalId(), cmd.consultorioId(), userEmail, roles);
        DisponibilidadProfesional disponibilidad = disponibilidadRepo.findById(cmd.id())
                .orElseThrow(() -> new DisponibilidadProfesionalNotFoundException("Disponibilidad no encontrada: " + cmd.id()));
        validateAsignacionActiva(cmd.profesionalId(), cmd.consultorioId());
        validateDentroHorarioConsultorio(cmd.consultorioId(), new CreateDisponibilidadCommand(
                cmd.profesionalId(), cmd.consultorioId(), cmd.diaSemana(), cmd.horaInicio(), cmd.horaFin()
        ));
        validateSinSolapamiento(cmd.profesionalId(), cmd.consultorioId(), cmd.diaSemana(), cmd.horaInicio(), cmd.horaFin(), cmd.id());

        disponibilidad.update(cmd.profesionalId(), cmd.consultorioId(), cmd.diaSemana(), cmd.horaInicio(), cmd.horaFin());
        return toResult(disponibilidadRepo.save(disponibilidad));
    }

    public void delete(DeleteDisponibilidadCommand cmd, String userEmail, Set<String> roles) {
        assertCanManage(cmd.profesionalId(), cmd.consultorioId(), userEmail, roles);
        disponibilidadRepo.findById(cmd.id())
                .orElseThrow(() -> new DisponibilidadProfesionalNotFoundException("Disponibilidad no encontrada: " + cmd.id()));
        disponibilidadRepo.deleteById(cmd.id());
    }

    private void validateAsignacionActiva(UUID profesionalId, UUID consultorioId) {
        ProfesionalConsultorio pc = profesionalConsultorioRepo
                .findByProfesionalIdAndConsultorioId(profesionalId, consultorioId)
                .orElseThrow(() -> new ProfesionalConsultorioNotFoundException(
                        "El profesional no esta asignado al consultorio"));
        if (!pc.isActivo()) {
            throw new ProfesionalConsultorioNotFoundException("El profesional no tiene asignacion activa");
        }
    }

    private void validateDentroHorarioConsultorio(UUID consultorioId, CreateDisponibilidadCommand cmd) {
        List<ConsultorioHorario> horarios = horarioRepo
                .findByConsultorioIdAndDiaSemana(consultorioId, cmd.diaSemana())
                .stream().filter(ConsultorioHorario::isActivo).toList();
        if (horarios.isEmpty()) {
            throw new ConsultorioHorarioNotFoundException("No existe horario para el dia " + cmd.diaSemana());
        }

        boolean insideAny = horarios.stream().anyMatch(h ->
                !cmd.horaInicio().isBefore(h.getHoraApertura()) &&
                        !cmd.horaFin().isAfter(h.getHoraCierre()));
        if (!insideAny) {
            throw new DisponibilidadFueraDeHorarioException("La disponibilidad esta fuera del horario del consultorio");
        }
    }

    private void validateSinSolapamiento(UUID profesionalId, UUID consultorioId, java.time.DayOfWeek diaSemana,
                                         java.time.LocalTime inicio, java.time.LocalTime fin, UUID excludeId) {
        List<DisponibilidadProfesional> existentes = disponibilidadRepo
                .findByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, diaSemana);

        boolean overlap = existentes.stream()
                .filter(DisponibilidadProfesional::isActivo)
                .filter(e -> excludeId == null || !e.getId().equals(excludeId))
                .anyMatch(e -> inicio.isBefore(e.getHoraFin()) && fin.isAfter(e.getHoraInicio()));
        if (overlap) {
            throw new DisponibilidadSolapamientoException("La disponibilidad se solapa con otra existente");
        }
    }

    private void assertProfesionalExists(UUID profesionalId) {
        profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + profesionalId));
    }

    private void assertConsultorioExists(UUID consultorioId) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
    }

    private void assertCanManage(UUID profesionalId, UUID consultorioId, String userEmail, Set<String> roles) {
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
        if (roles.contains("ROLE_PROFESIONAL")) {
            Profesional own = profesionalRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new AccessDeniedException("Profesional no encontrado"));
            if (!own.getId().equals(profesionalId)) {
                throw new AccessDeniedException("Solo puede gestionar su propia agenda");
            }
            return;
        }
        throw new AccessDeniedException("Permiso denegado");
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private DisponibilidadProfesionalResult toResult(DisponibilidadProfesional d) {
        return new DisponibilidadProfesionalResult(
                d.getId(),
                d.getProfesionalId(),
                d.getConsultorioId(),
                d.getDiaSemana(),
                d.getHoraInicio(),
                d.getHoraFin(),
                d.isActivo()
        );
    }
}
