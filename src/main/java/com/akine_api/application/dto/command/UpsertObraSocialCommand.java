package com.akine_api.application.dto.command;

import com.akine_api.domain.model.ObraSocialEstado;

import java.util.List;
import java.util.UUID;

public record UpsertObraSocialCommand(
        UUID id,
        UUID consultorioId,
        String acronimo,
        String nombreCompleto,
        String cuit,
        String email,
        String telefono,
        String telefonoAlternativo,
        String representante,
        String observacionesInternas,
        String direccionLinea,
        ObraSocialEstado estado,
        List<PlanCommand> planes
) {}

