package com.akine_api.application.service.facturacion;

import com.akine_api.application.service.cobertura.FinanciadorSaludService;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConvenioFinanciadorService {

    private final ConvenioFinanciadorRepositoryPort repositoryPort;
    private final FinanciadorSaludService financiadorService;

    @Transactional
    public ConvenioFinanciador create(ConvenioFinanciador convenio) {
        // Validate Financiador exists
        financiadorService.findById(convenio.getFinanciadorId());
        
        convenio.setActivo(true);
        return repositoryPort.save(convenio);
    }

    @Transactional(readOnly = true)
    public List<ConvenioFinanciador> findByFinanciadorId(UUID financiadorId) {
        return repositoryPort.findByFinanciadorId(financiadorId);
    }

    @Transactional(readOnly = true)
    public ConvenioFinanciador findById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado"));
    }
}
