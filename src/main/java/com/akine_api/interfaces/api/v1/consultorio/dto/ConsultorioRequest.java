package com.akine_api.interfaces.api.v1.consultorio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultorioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String name,

        @Size(max = 13, message = "El CUIT no puede superar 13 caracteres")
        String cuit,

        @Size(max = 500, message = "El domicilio no puede superar 500 caracteres")
        String address,

        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        String phone,

        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        String email
) {}
