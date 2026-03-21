package com.akine_api.application.service;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.domain.exception.PlanLimitExceededException;
import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.domain.model.PlanFeature;
import com.akine_api.domain.model.SuscripcionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Valida límites de plan antes de crear recursos.
 * Todos los métodos son NO-OP si no existe una suscripción ACTIVE para la empresa,
 * permitiendo operar libremente a usuarios ADMIN y en contextos sin tenant activo.
 */
@Service
@Transactional(readOnly = true)
public class PlanGateService {

    private final SuscripcionRepositoryPort suscripcionRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final PlanDefinicionRepositoryPort planRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final PacienteConsultorioRepositoryPort pacienteConsultorioRepo;

    public PlanGateService(SuscripcionRepositoryPort suscripcionRepo,
                           ConsultorioRepositoryPort consultorioRepo,
                           PlanDefinicionRepositoryPort planRepo,
                           ProfesionalRepositoryPort profesionalRepo,
                           PacienteConsultorioRepositoryPort pacienteConsultorioRepo) {
        this.suscripcionRepo = suscripcionRepo;
        this.consultorioRepo = consultorioRepo;
        this.planRepo = planRepo;
        this.profesionalRepo = profesionalRepo;
        this.pacienteConsultorioRepo = pacienteConsultorioRepo;
    }

    /**
     * Verifica que la empresa no supere el límite de consultorios de su plan.
     * Se llama antes de crear un nuevo consultorio, pasando el empresaId del tenant.
     */
    public void checkConsultorioLimit(UUID empresaId) {
        if (empresaId == null) return;
        Optional<PlanDefinicion> planOpt = resolvePlanByEmpresa(empresaId);
        if (planOpt.isEmpty()) return;
        PlanDefinicion plan = planOpt.get();
        if (plan.getMaxConsultorios() == null) return;
        long actual = consultorioRepo.countByEmpresaId(empresaId);
        if (actual >= plan.getMaxConsultorios()) {
            throw new PlanLimitExceededException("consultorios", plan.getMaxConsultorios());
        }
    }

    /**
     * Verifica que el consultorio no supere el límite de profesionales de su plan.
     */
    public void checkProfesionalLimit(UUID consultorioId) {
        UUID empresaId = resolveEmpresaId(consultorioId);
        if (empresaId == null) return;
        Optional<PlanDefinicion> planOpt = resolvePlanByEmpresa(empresaId);
        if (planOpt.isEmpty()) return;
        PlanDefinicion plan = planOpt.get();
        if (plan.getMaxProfesionales() == null) return;
        long actual = profesionalRepo.countByConsultorioId(consultorioId);
        if (actual >= plan.getMaxProfesionales()) {
            throw new PlanLimitExceededException("profesionales", plan.getMaxProfesionales());
        }
    }

    /**
     * Verifica que el consultorio no supere el límite de pacientes de su plan.
     */
    public void checkPacienteLimit(UUID consultorioId) {
        UUID empresaId = resolveEmpresaId(consultorioId);
        if (empresaId == null) return;
        Optional<PlanDefinicion> planOpt = resolvePlanByEmpresa(empresaId);
        if (planOpt.isEmpty()) return;
        PlanDefinicion plan = planOpt.get();
        if (plan.getMaxPacientes() == null) return;
        long actual = pacienteConsultorioRepo.countByConsultorioId(consultorioId);
        if (actual >= plan.getMaxPacientes()) {
            throw new PlanLimitExceededException("pacientes", plan.getMaxPacientes());
        }
    }

    /**
     * Verifica que el plan del consultorio incluya la feature solicitada.
     * Para uso futuro cuando los módulos estén implementados.
     */
    public void checkFeature(UUID consultorioId, PlanFeature feature) {
        UUID empresaId = resolveEmpresaId(consultorioId);
        if (empresaId == null) return;
        Optional<PlanDefinicion> planOpt = resolvePlanByEmpresa(empresaId);
        if (planOpt.isEmpty()) return;
        PlanDefinicion plan = planOpt.get();
        if (!plan.hasFeature(feature)) {
            throw new com.akine_api.domain.exception.FeatureNotAvailableException(feature.name(), plan.getNombre());
        }
    }

    /**
     * Consulta sin lanzar excepción. Útil para mostrar/ocultar opciones en la UI.
     */
    public boolean hasFeature(UUID consultorioId, PlanFeature feature) {
        try {
            UUID empresaId = resolveEmpresaId(consultorioId);
            if (empresaId == null) return true;
            return resolvePlanByEmpresa(empresaId)
                    .map(p -> p.hasFeature(feature))
                    .orElse(true);
        } catch (Exception e) {
            return true;
        }
    }

    private UUID resolveEmpresaId(UUID consultorioId) {
        if (consultorioId == null) return null;
        return consultorioRepo.findById(consultorioId)
                .map(c -> c.getEmpresaId())
                .orElse(null);
    }

    private Optional<PlanDefinicion> resolvePlanByEmpresa(UUID empresaId) {
        return suscripcionRepo.findByEmpresaId(empresaId)
                .filter(s -> s.getStatus() == SuscripcionStatus.ACTIVE)
                .flatMap(s -> planRepo.findByCodigo(s.getPlanCode()));
    }
}
