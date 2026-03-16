package com.akine_api.application.dto.command;

import com.akine_api.domain.model.CasoAtencionEstado;

public record CambiarEstadoCasoAtencionCommand(
        CasoAtencionEstado nuevoEstado
) {}
