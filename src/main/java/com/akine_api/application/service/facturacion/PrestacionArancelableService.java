package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.facturacion.PrestacionArancelable;
import com.akine_api.domain.repository.facturacion.PrestacionArancelableRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrestacionArancelableService {

    private final PrestacionArancelableRepositoryPort repositoryPort;

    @Transactional
    public PrestacionArancelable create(PrestacionArancelable prestacion) {
        // Validate unique code
        repositoryPort.findByCodigoInterno(prestacion.getCodigoInterno())
                .ifPresent(p -> { throw new RuntimeException("Código de prestación ya existe"); });

        prestacion.setActivo(true);
        return repositoryPort.save(prestacion);
    }

    @Transactional(readOnly = true)
    public List<PrestacionArancelable> findAll() {
        return repositoryPort.findAll();
    }

    @Transactional(readOnly = true)
    public PrestacionArancelable findById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestación no encontrada"));
    }
}
