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

import java.util.List;
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
        ConsultorioHorario horario = horarioRepo
                .findByConsultorioIdAndDiaSemana(cmd.consultorioId(), cmd.diaSemana())
                .map(existing -> {
                    existing.update(cmd.horaApertura(), cmd.horaCierre());
                    return existing;
                })
                .orElseGet(() -> new ConsultorioHorario(
                        UUID.randomUUID(),
                        cmd.consultorioId(),
                        cmd.diaSemana(),
                        cmd.horaApertura(),
                        cmd.horaCierre(),
                        true
                ));
        return toResult(horarioRepo.save(horario));
    }

    public void delete(DeleteHorarioConsultorioCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        ConsultorioHorario horario = horarioRepo
                .findByConsultorioIdAndDiaSemana(cmd.consultorioId(), cmd.diaSemana())
                .orElseThrow(() -> new ConsultorioHorarioNotFoundException(
                        "Horario no encontrado para " + cmd.diaSemana()));
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
