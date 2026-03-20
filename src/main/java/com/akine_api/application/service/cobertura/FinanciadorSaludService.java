package com.akine_api.application.service.cobertura;

import com.akine_api.domain.exception.FinanciadorDuplicadoException;
import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanciadorSaludService {

    private final FinanciadorSaludRepositoryPort repositoryPort;

    @Transactional
    public FinanciadorSalud create(FinanciadorSalud financiadorSalud) {
        if (repositoryPort.existsByNombreAndConsultorioId(financiadorSalud.getNombre(), financiadorSalud.getConsultorioId())) {
            throw new FinanciadorDuplicadoException(financiadorSalud.getNombre());
        }
        financiadorSalud.setActivo(true);
        return repositoryPort.save(financiadorSalud);
    }

    @Transactional
    public FinanciadorSalud update(UUID id, FinanciadorSalud financiadorSalud) {
        FinanciadorSalud existing = repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("FinanciadorSalud no encontrado"));
        if (repositoryPort.existsByNombreAndConsultorioIdExcludingId(financiadorSalud.getNombre(), existing.getConsultorioId(), id)) {
            throw new FinanciadorDuplicadoException(financiadorSalud.getNombre());
        }
        return repositoryPort.update(id, financiadorSalud);
    }

    @Transactional(readOnly = true)
    public List<FinanciadorSalud> findAllByConsultorioId(UUID consultorioId) {
        return repositoryPort.findAllByConsultorioId(consultorioId);
    }

    @Transactional(readOnly = true)
    public FinanciadorSalud findById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("FinanciadorSalud no encontrado"));
    }
}
