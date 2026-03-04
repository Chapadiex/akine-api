package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.ObraSocialEstado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ObraSocialUpsertRequest(
        @NotBlank(message = "El acronimo es obligatorio")
        @Size(min = 2, max = 20, message = "El acronimo debe tener entre 2 y 20 caracteres")
        String acronimo,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre completo debe tener entre 3 y 120 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El CUIT es obligatorio")
        @Size(max = 13, message = "El CUIT no puede superar 13 caracteres")
        String cuit,

        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        String email,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono,

        @Size(max = 30, message = "El telefono alternativo no puede superar 30 caracteres")
        String telefonoAlternativo,

        @Size(max = 120, message = "El representante no puede superar 120 caracteres")
        String representante,

        @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres")
        String observacionesInternas,

        @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
        String direccionLinea,

        ObraSocialEstado estado,

        @NotEmpty(message = "Debe cargar al menos un plan")
        @Valid
        List<PlanRequest> planes
) {}

