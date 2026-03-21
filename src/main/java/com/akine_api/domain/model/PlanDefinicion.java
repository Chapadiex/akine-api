package com.akine_api.domain.model;

import java.math.BigDecimal;
import com.akine_api.domain.model.PlanFeature;

public class PlanDefinicion {

    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precioMensual;
    private final BigDecimal precioAnual;
    private final Integer maxConsultorios;
    private final Integer maxProfesionales;
    private final Integer maxPacientes;
    private final boolean moduloFacturacion;
    private final boolean moduloHistoriaClinica;
    private final boolean moduloObrasSociales;
    private final boolean moduloColaboradores;
    private final boolean activo;
    private final int orden;

    public PlanDefinicion(String codigo,
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
                          boolean activo,
                          int orden) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioMensual = precioMensual;
        this.precioAnual = precioAnual;
        this.maxConsultorios = maxConsultorios;
        this.maxProfesionales = maxProfesionales;
        this.maxPacientes = maxPacientes;
        this.moduloFacturacion = moduloFacturacion;
        this.moduloHistoriaClinica = moduloHistoriaClinica;
        this.moduloObrasSociales = moduloObrasSociales;
        this.moduloColaboradores = moduloColaboradores;
        this.activo = activo;
        this.orden = orden;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecioMensual() { return precioMensual; }
    public BigDecimal getPrecioAnual() { return precioAnual; }
    public Integer getMaxConsultorios() { return maxConsultorios; }
    public Integer getMaxProfesionales() { return maxProfesionales; }
    public Integer getMaxPacientes() { return maxPacientes; }
    public boolean isModuloFacturacion() { return moduloFacturacion; }
    public boolean isModuloHistoriaClinica() { return moduloHistoriaClinica; }
    public boolean isModuloObrasSociales() { return moduloObrasSociales; }
    public boolean isModuloColaboradores() { return moduloColaboradores; }
    public boolean isActivo() { return activo; }
    public int getOrden() { return orden; }

    public boolean hasFeature(PlanFeature feature) {
        return switch (feature) {
            case AGENDA -> true; // todos los planes tienen agenda
            case HISTORIA_CLINICA -> moduloHistoriaClinica;
            case FACTURACION -> moduloFacturacion;
            case OBRA_SOCIAL -> moduloObrasSociales;
        };
    }
}
