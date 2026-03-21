package com.akine_api.interfaces.api.v1.user.dto;

import java.util.List;

public record UserContextResponse(
        String tipo,
        String matricula,
        List<String> especialidades,
        String nroDocumento,
        String domicilio,
        String cargo,
        String dni
) {}
