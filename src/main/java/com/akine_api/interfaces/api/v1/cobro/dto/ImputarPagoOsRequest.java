package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ImputarPagoOsRequest {

    @NotNull
    private UUID cajaDiariaId;
}
