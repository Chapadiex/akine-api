package com.akine_api.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObraSocial {

    private final UUID id;
    private final UUID consultorioId;
    private String acronimo;
    private String nombreCompleto;
    private String cuit;
    private String email;
    private String telefono;
    private String telefonoAlternativo;
    private String representante;
    private String observacionesInternas;
    private String direccionLinea;
    private ObraSocialEstado estado;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<ObraSocialPlan> planes;

    public ObraSocial(UUID id,
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
                      Instant createdAt,
                      List<ObraSocialPlan> planes) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.acronimo = acronimo;
        this.nombreCompleto = nombreCompleto;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.telefonoAlternativo = telefonoAlternativo;
        this.representante = representante;
        this.observacionesInternas = observacionesInternas;
        this.direccionLinea = direccionLinea;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.planes = new ArrayList<>(planes == null ? List.of() : planes);
    }

    public void update(String acronimo,
                       String nombreCompleto,
                       String cuit,
                       String email,
                       String telefono,
                       String telefonoAlternativo,
                       String representante,
                       String observacionesInternas,
                       String direccionLinea,
                       ObraSocialEstado estado,
                       List<ObraSocialPlan> planes) {
        this.acronimo = acronimo;
        this.nombreCompleto = nombreCompleto;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.telefonoAlternativo = telefonoAlternativo;
        this.representante = representante;
        this.observacionesInternas = observacionesInternas;
        this.direccionLinea = direccionLinea;
        this.estado = estado;
        this.planes.clear();
        this.planes.addAll(planes);
        this.updatedAt = Instant.now();
    }

    public void changeEstado(ObraSocialEstado estado) {
        this.estado = estado;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public String getAcronimo() { return acronimo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCuit() { return cuit; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getTelefonoAlternativo() { return telefonoAlternativo; }
    public String getRepresentante() { return representante; }
    public String getObservacionesInternas() { return observacionesInternas; }
    public String getDireccionLinea() { return direccionLinea; }
    public ObraSocialEstado getEstado() { return estado; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<ObraSocialPlan> getPlanes() { return List.copyOf(planes); }
}

