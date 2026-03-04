package com.akine_api.application.service;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.domain.exception.ConsultorioInactiveException;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.Consultorio;

import java.util.UUID;

public final class ConsultorioStateGuardService {

    private ConsultorioStateGuardService() {
    }

    public static Consultorio requireExists(ConsultorioRepositoryPort consultorioRepo, UUID consultorioId) {
        return consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + consultorioId));
    }

    public static Consultorio requireActive(ConsultorioRepositoryPort consultorioRepo, UUID consultorioId) {
        Consultorio consultorio = requireExists(consultorioRepo, consultorioId);
        if (!consultorio.isActive()) {
            throw new ConsultorioInactiveException("Consultorio inactivo. Solo ADMIN puede reactivarlo.");
        }
        return consultorio;
    }
}
