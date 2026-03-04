package com.akine_api.application.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultorioEspecialidadBootstrapService {

    private final ConsultorioEspecialidadService consultorioEspecialidadService;

    public ConsultorioEspecialidadBootstrapService(ConsultorioEspecialidadService consultorioEspecialidadService) {
        this.consultorioEspecialidadService = consultorioEspecialidadService;
    }

    public void enableDefaultsForConsultorio(UUID consultorioId) {
        consultorioEspecialidadService.enableDefaultsForConsultorio(consultorioId);
    }
}
