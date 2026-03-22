package com.akine_api.application.service;

import com.akine_api.domain.model.ConfiguracionConsultorio;
import com.akine_api.domain.repository.ConfiguracionConsultorioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfiguracionConsultorioService {

    private final ConfiguracionConsultorioRepositoryPort repositoryPort;

    @Transactional(readOnly = true)
    public ConfiguracionConsultorio findByConsultorioId(UUID consultorioId) {
        return repositoryPort.findByConsultorioId(consultorioId)
                .orElse(defaultsFor(consultorioId));
    }

    @Transactional
    public ConfiguracionConsultorio upsert(UUID consultorioId, ConfiguracionConsultorio config) {
        config.setConsultorioId(consultorioId);
        repositoryPort.findByConsultorioId(consultorioId)
                .ifPresent(existing -> config.setId(existing.getId()));
        return repositoryPort.save(config);
    }

    private ConfiguracionConsultorio defaultsFor(UUID consultorioId) {
        return ConfiguracionConsultorio.builder()
                .consultorioId(consultorioId)
                .politicaNoShow("NO_COBRAR")
                .alertaSesionSinCierreHoras(24)
                .formatoNumeracionRecibo("REC-{year}-{seq:06}")
                .habilitarMultiplesCajas(false)
                .monedaDefault("ARS")
                .build();
    }
}
