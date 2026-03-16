package com.akine_api.application.service;

import com.akine_api.application.dto.command.CambiarEstadoCasoAtencionCommand;
import com.akine_api.application.dto.command.CreateCasoAtencionCommand;
import com.akine_api.application.dto.command.UpdateCasoAtencionCommand;
import com.akine_api.application.dto.result.CasoAtencionResult;
import com.akine_api.application.dto.result.CasoAtencionSummaryResult;
import com.akine_api.application.port.output.CasoAtencionRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.HistoriaClinicaLegajoRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.CasoAtencionNotFoundException;
import com.akine_api.domain.exception.HistoriaClinicaValidationException;
import com.akine_api.domain.model.CasoAtencion;
import com.akine_api.domain.model.CasoAtencionEstado;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CasoAtencionService {

    private final CasoAtencionRepositoryPort casoAtencionRepo;
    private final HistoriaClinicaLegajoRepositoryPort legajoRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public CasoAtencionService(CasoAtencionRepositoryPort casoAtencionRepo,
                                HistoriaClinicaLegajoRepositoryPort legajoRepo,
                                ProfesionalRepositoryPort profesionalRepo,
                                ConsultorioRepositoryPort consultorioRepo,
                                UserRepositoryPort userRepo) {
        this.casoAtencionRepo = casoAtencionRepo;
        this.legajoRepo = legajoRepo;
        this.profesionalRepo = profesionalRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public CasoAtencionResult createCasoAtencion(UUID consultorioId,
                                                  CreateCasoAtencionCommand cmd,
                                                  String userEmail,
                                                  Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        UUID actorUserId = resolveUserId(userEmail);

        legajoRepo.findById(cmd.legajoId())
                .filter(l -> l.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new HistoriaClinicaValidationException(
                        "Legajo de historia clínica no encontrado en el consultorio: " + cmd.legajoId()));

        CasoAtencion caso = new CasoAtencion(
                UUID.randomUUID(),
                cmd.legajoId(),
                consultorioId,
                cmd.pacienteId(),
                cmd.profesionalResponsableId(),
                cmd.tipoOrigen(),
                cmd.fechaApertura(),
                cmd.motivoConsulta(),
                cmd.diagnosticoMedico(),
                cmd.diagnosticoFuncional(),
                cmd.afeccionPrincipal(),
                cmd.coberturaId(),
                CasoAtencionEstado.BORRADOR,
                cmd.prioridad(),
                null,
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        );

        CasoAtencion saved = casoAtencionRepo.save(caso);
        return toResult(saved);
    }

    @Transactional(readOnly = true)
    public CasoAtencionResult getCasoAtencion(UUID id, UUID consultorioId, String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        CasoAtencion caso = casoAtencionRepo.findByIdAndConsultorioId(id, consultorioId)
                .orElseThrow(() -> new CasoAtencionNotFoundException(id));
        return toResult(caso);
    }

    @Transactional(readOnly = true)
    public List<CasoAtencionSummaryResult> getCasosPorLegajo(UUID legajoId, UUID consultorioId,
                                                              String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        legajoRepo.findById(legajoId)
                .filter(l -> l.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new HistoriaClinicaValidationException(
                        "Legajo no encontrado en el consultorio indicado"));

        return casoAtencionRepo.findByLegajoId(legajoId)
                .stream().map(this::toSummaryResult).toList();
    }

    @Transactional(readOnly = true)
    public List<CasoAtencionSummaryResult> getCasosActivosPorPaciente(UUID pacienteId, UUID consultorioId,
                                                                       String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        List<CasoAtencionEstado> activos = List.of(
                CasoAtencionEstado.ACTIVO,
                CasoAtencionEstado.EN_TRATAMIENTO,
                CasoAtencionEstado.EN_PAUSA);
        return casoAtencionRepo.findByPacienteIdAndConsultorioIdAndEstadoIn(pacienteId, consultorioId, activos)
                .stream().map(this::toSummaryResult).toList();
    }

    @Transactional(readOnly = true)
    public List<CasoAtencionSummaryResult> getCasosPorPaciente(UUID pacienteId, UUID consultorioId,
                                                                String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        return casoAtencionRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId)
                .stream().map(this::toSummaryResult).toList();
    }

    @Transactional
    public CasoAtencionResult updateCasoAtencion(UUID id, UUID consultorioId,
                                                  UpdateCasoAtencionCommand cmd,
                                                  String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        UUID actorUserId = resolveUserId(userEmail);

        CasoAtencion caso = casoAtencionRepo.findByIdAndConsultorioId(id, consultorioId)
                .orElseThrow(() -> new CasoAtencionNotFoundException(id));

        caso.update(cmd.motivoConsulta(), cmd.diagnosticoMedico(), cmd.diagnosticoFuncional(),
                cmd.afeccionPrincipal(), cmd.prioridad(), cmd.profesionalResponsableId(),
                actorUserId);

        return toResult(casoAtencionRepo.save(caso));
    }

    @Transactional
    public CasoAtencionResult cambiarEstado(UUID id, UUID consultorioId,
                                             CambiarEstadoCasoAtencionCommand cmd,
                                             String userEmail, Set<String> roles) {
        checkAccess(consultorioId, userEmail, roles);
        UUID actorUserId = resolveUserId(userEmail);

        CasoAtencion caso = casoAtencionRepo.findByIdAndConsultorioId(id, consultorioId)
                .orElseThrow(() -> new CasoAtencionNotFoundException(id));

        caso.cambiarEstado(cmd.nuevoEstado(), actorUserId);
        return toResult(casoAtencionRepo.save(caso));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void checkAccess(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return;
        }
        if (!roles.contains("ROLE_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        UUID userId = resolveUserId(userEmail);
        if (!consultorioRepo.findConsultorioIdsByUserId(userId).contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private String resolveNombreProfesional(UUID profesionalId) {
        if (profesionalId == null) return null;
        return profesionalRepo.findById(profesionalId)
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse(null);
    }

    private CasoAtencionResult toResult(CasoAtencion caso) {
        return new CasoAtencionResult(
                caso.getId(),
                caso.getLegajoId(),
                caso.getConsultorioId(),
                caso.getPacienteId(),
                caso.getProfesionalResponsableId(),
                resolveNombreProfesional(caso.getProfesionalResponsableId()),
                caso.getTipoOrigen(),
                caso.getFechaApertura(),
                caso.getMotivoConsulta(),
                caso.getDiagnosticoMedico(),
                caso.getDiagnosticoFuncional(),
                caso.getAfeccionPrincipal(),
                caso.getCoberturaId(),
                caso.getEstado(),
                caso.getPrioridad(),
                caso.getAtencionInicialId(),
                0,  // cantidadSesiones — se completará en Fase 5
                0,  // cantidadPlanes   — se completará en Fase 5
                caso.getCreatedAt(),
                caso.getUpdatedAt()
        );
    }

    private CasoAtencionSummaryResult toSummaryResult(CasoAtencion caso) {
        return new CasoAtencionSummaryResult(
                caso.getId(),
                caso.getLegajoId(),
                caso.getPacienteId(),
                caso.getProfesionalResponsableId(),
                resolveNombreProfesional(caso.getProfesionalResponsableId()),
                caso.getTipoOrigen(),
                caso.getFechaApertura(),
                caso.getMotivoConsulta(),
                caso.getDiagnosticoMedico(),
                caso.getAfeccionPrincipal(),
                caso.getEstado(),
                caso.getPrioridad(),
                0,  // cantidadSesiones — se completará en Fase 5
                0   // cantidadPlanes   — se completará en Fase 5
        );
    }
}
