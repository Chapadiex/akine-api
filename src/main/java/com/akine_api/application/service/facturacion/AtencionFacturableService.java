package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.facturacion.AtencionFacturable;
import com.akine_api.domain.repository.facturacion.AtencionFacturableRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtencionFacturableService {

    private final AtencionFacturableRepositoryPort repositoryPort;

    @Transactional(readOnly = true)
    public List<AtencionFacturable> findByPacienteId(UUID pacienteId) {
        return repositoryPort.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public AtencionFacturable findByAtencionId(UUID atencionId) {
        return repositoryPort.findByAtencionId(atencionId)
                .orElseThrow(() -> new RuntimeException("Atención facturable no encontrada"));
    }
}
