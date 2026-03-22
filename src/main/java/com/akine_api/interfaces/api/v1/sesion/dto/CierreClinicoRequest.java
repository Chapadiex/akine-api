package com.akine_api.interfaces.api.v1.sesion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CierreClinicoRequest {
    @NotNull
    @Positive
    private Integer duracionRealMinutos;
    @NotBlank
    private String tratamientoRealizado;
    @NotBlank
    private String resultadoClinico;
    @NotBlank
    private String conductaSiguiente;
    private Boolean requiereSeguimiento;
    private String observacionesClincias;
    private Boolean esGrupal;
}
