package com.akine_api.application.service.cobertura;

import com.akine_api.domain.model.cobertura.PacienteCobertura;
import com.akine_api.domain.repository.cobertura.PacienteCoberturaRepositoryPort;
import com.akine_api.infrastructure.persistence.repository.PacienteJpaRepository; // Using JPA directly just for validation for simplicity, or we could use PacienteRepositoryPort if it exists
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PacienteCoberturaService {

    private final PacienteCoberturaRepositoryPort coberturaRepositoryPort;
    private final FinanciadorSaludService financiadorService;
    private final PlanFinanciadorService planService;
    private final PacienteJpaRepository pacienteJpaRepository;

    @Transactional
    public PacienteCobertura create(PacienteCobertura cobertura) {
        // Validate Paciente exists
        if (!pacienteJpaRepository.existsById(cobertura.getPacienteId())) {
            throw new RuntimeException("Paciente no encontrado");
        }

        // Validate Financiador exists
        financiadorService.findById(cobertura.getFinanciadorId());

        // Validate Plan if provided
        if (cobertura.getPlanId() != null) {
            planService.findById(cobertura.getPlanId());
        }

        // Handle principal logic (if new is principal, set others to false - left for business rule detailing later)
        if (cobertura.getPrincipal() == null) {
            cobertura.setPrincipal(false);
        }

        cobertura.setActivo(true);
        return coberturaRepositoryPort.save(cobertura);
    }

    @Transactional(readOnly = true)
    public List<PacienteCobertura> findByPacienteId(UUID pacienteId) {
        return coberturaRepositoryPort.findByPacienteId(pacienteId);
    }
}
