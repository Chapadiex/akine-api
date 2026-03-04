package com.akine_api.application.dto.command;

import com.akine_api.domain.model.ObraSocialEstado;

import java.util.UUID;

public record ChangeObraSocialEstadoCommand(
        UUID consultorioId,
        UUID obraSocialId,
        ObraSocialEstado estado
) {}

