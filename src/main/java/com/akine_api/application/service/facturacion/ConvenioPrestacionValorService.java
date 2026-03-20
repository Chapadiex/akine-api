package com.akine_api.application.service.facturacion;

import com.akine_api.application.service.cobertura.PlanFinanciadorService;
import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import com.akine_api.domain.repository.facturacion.ConvenioPrestacionValorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConvenioPrestacionValorService {

    private final ConvenioPrestacionValorRepositoryPort repositoryPort;
    private final ConvenioFinanciadorService convenioService;
    private final PrestacionArancelableService prestacionService;
    private final PlanFinanciadorService planService;

    @Transactional
    public ConvenioPrestacionValor create(ConvenioPrestacionValor valor) {
        // Validate existence
        convenioService.findById(valor.getConvenioId());
        prestacionService.findById(valor.getPrestacionId());
        if (valor.getPlanId() != null) {
            planService.findById(valor.getPlanId());
        }

        // Here we could add logic to ensure no overlapping dates for the same prestacion/plan/convenio
        
        valor.setActivo(true);
        return repositoryPort.save(valor);
    }

    @Transactional(readOnly = true)
    public List<ConvenioPrestacionValor> findByConvenioId(UUID convenioId) {
        return repositoryPort.findByConvenioId(convenioId);
    }
}
