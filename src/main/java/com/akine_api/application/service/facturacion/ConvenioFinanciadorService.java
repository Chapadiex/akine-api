package com.akine_api.application.service.facturacion;

import com.akine_api.application.service.cobertura.FinanciadorSaludService;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConvenioFinanciadorService {

    private final ConvenioFinanciadorRepositoryPort repositoryPort;
    private final FinanciadorSaludService financiadorService;

    @Transactional
    public ConvenioFinanciador create(ConvenioFinanciador convenio) {
        financiadorService.findById(convenio.getFinanciadorId());
        validarSolapamiento(convenio, null);
        convenio.setActivo(true);
        return repositoryPort.save(convenio);
    }

    @Transactional
    public ConvenioFinanciador update(UUID id, ConvenioFinanciador convenio) {
        findById(id);
        validarSolapamiento(convenio, id);
        convenio.setId(id);
        return repositoryPort.save(convenio);
    }

    @Transactional(readOnly = true)
    public List<ConvenioFinanciador> findByFinanciadorId(UUID financiadorId) {
        return repositoryPort.findByFinanciadorId(financiadorId);
    }

    @Transactional(readOnly = true)
    public List<ConvenioFinanciador> findByConsultorioId(UUID consultorioId) {
        return repositoryPort.findByConsultorioId(consultorioId);
    }

    @Transactional(readOnly = true)
    public ConvenioFinanciador findById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado"));
    }

    /**
     * Busca el convenio vigente para una sesión. Usado por LiquidacionCalculadorService (Fase 3).
     * Retorna Optional.empty() si no hay convenio → circuito particular.
     */
    @Transactional(readOnly = true)
    public Optional<ConvenioFinanciador> findVigente(UUID financiadorId, UUID planId,
                                                      UUID consultorioId, LocalDate fecha) {
        return repositoryPort.findVigenteByFinanciadorPlanConsultorio(financiadorId, planId, consultorioId, fecha);
    }

    private void validarSolapamiento(ConvenioFinanciador convenio, UUID excludeId) {
        if (convenio.getConsultorioId() == null) return;
        boolean solapa = repositoryPort.existsSolapamiento(
                convenio.getFinanciadorId(),
                convenio.getPlanId(),
                convenio.getConsultorioId(),
                convenio.getVigenciaDesde(),
                convenio.getVigenciaHasta(),
                excludeId);
        if (solapa) {
            throw new RuntimeException(
                    "Ya existe un convenio activo para este financiador/plan/consultorio en el período indicado");
        }
    }
}
