package com.akine_api.application.service;

import com.akine_api.application.dto.command.AddDuracionTurnoCommand;
import com.akine_api.application.dto.command.RemoveDuracionTurnoCommand;
import com.akine_api.application.dto.result.ConsultorioDuracionTurnoResult;
import com.akine_api.application.port.output.ConsultorioDuracionTurnoRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.ConsultorioDuracionTurno;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioDuracionTurnoService {

    private final ConsultorioDuracionTurnoRepositoryPort duracionRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ConsultorioDuracionTurnoService(ConsultorioDuracionTurnoRepositoryPort duracionRepo,
                                           ConsultorioRepositoryPort consultorioRepo,
                                           UserRepositoryPort userRepo) {
        this.duracionRepo = duracionRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioDuracionTurnoResult> list(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return duracionRepo.findByConsultorioId(consultorioId).stream().map(this::toResult).toList();
    }

    public ConsultorioDuracionTurnoResult add(AddDuracionTurnoCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        if (duracionRepo.existsByConsultorioIdAndMinutos(cmd.consultorioId(), cmd.minutos())) {
            throw new IllegalArgumentException("La duracion ya existe para este consultorio");
        }
        ConsultorioDuracionTurno saved = duracionRepo.save(
                new ConsultorioDuracionTurno(UUID.randomUUID(), cmd.consultorioId(), cmd.minutos())
        );
        return toResult(saved);
    }

    public void remove(RemoveDuracionTurnoCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        duracionRepo.deleteByConsultorioIdAndMinutos(cmd.consultorioId(), cmd.minutos());
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

    private ConsultorioDuracionTurnoResult toResult(ConsultorioDuracionTurno d) {
        return new ConsultorioDuracionTurnoResult(d.getId(), d.getConsultorioId(), d.getMinutos());
    }
}
