package com.akine_api.application.service.cobro;

import com.akine_api.domain.model.cobro.AuditoriaEvento;
import com.akine_api.domain.repository.cobro.AuditoriaEventoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditoriaEventoService {

    private final AuditoriaEventoRepositoryPort repositoryPort;

    /**
     * Insert-only: registers an audit event. Runs in its own transaction so that
     * the audit record is persisted even if the calling transaction rolls back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditoriaEvento registrar(UUID consultorioId, String entidad, UUID entidadId,
                                     String accion, String estadoAnterior, String estadoNuevo,
                                     UUID usuarioId, String motivo) {
        AuditoriaEvento evento = AuditoriaEvento.builder()
                .consultorioId(consultorioId)
                .entidad(entidad)
                .entidadId(entidadId)
                .accion(accion)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .usuarioId(usuarioId)
                .timestamp(Instant.now())
                .motivo(motivo)
                .build();
        return repositoryPort.save(evento);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEvento> findByEntidadId(String entidad, UUID entidadId) {
        return repositoryPort.findByEntidadId(entidad, entidadId);
    }
}
