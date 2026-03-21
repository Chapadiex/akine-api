package com.akine_api.interfaces.api.v1.admin.dto;

import java.math.BigDecimal;

public record PlanDefinicionResponse(
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precioMensual,
        BigDecimal precioAnual,
        Integer maxConsultorios,
        Integer maxProfesionales,
        Integer maxPacientes,
        boolean moduloFacturacion,
        boolean moduloHistoriaClinica,
        boolean moduloObrasSociales,
        boolean moduloColaboradores,
        int orden
) {}
