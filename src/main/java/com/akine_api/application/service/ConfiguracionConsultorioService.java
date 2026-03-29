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
    public ConfiguracionConsultorio upsert(UUID consultorioId, ConfiguracionConsultorio incoming) {
        ConfiguracionConsultorio base = repositoryPort.findByConsultorioId(consultorioId)
                .orElse(defaultsFor(consultorioId));

        // Merge: only override fields that are explicitly provided (non-null)
        if (incoming.getPoliticaNoShow() != null)           base.setPoliticaNoShow(incoming.getPoliticaNoShow());
        if (incoming.getNoShowHorasAviso() != null)         base.setNoShowHorasAviso(incoming.getNoShowHorasAviso());
        if (incoming.getAlertaSesionSinCierreHoras() != null) base.setAlertaSesionSinCierreHoras(incoming.getAlertaSesionSinCierreHoras());
        if (incoming.getFormatoNumeracionRecibo() != null)  base.setFormatoNumeracionRecibo(incoming.getFormatoNumeracionRecibo());
        if (incoming.getHabilitarMultiplesCajas() != null)  base.setHabilitarMultiplesCajas(incoming.getHabilitarMultiplesCajas());
        if (incoming.getMonedaDefault() != null)            base.setMonedaDefault(incoming.getMonedaDefault());
        if (incoming.getArancelParticularPorSesion() != null) base.setArancelParticularPorSesion(incoming.getArancelParticularPorSesion());

        base.setConsultorioId(consultorioId);
        return repositoryPort.save(base);
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
