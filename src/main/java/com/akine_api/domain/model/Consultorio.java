package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Consultorio {

    private final UUID id;
    private String name;
    private String cuit;
    private String address;
    private String phone;
    private String email;
    private String status;
    private UUID empresaId;
    private final Instant createdAt;
    private Instant updatedAt;

    public Consultorio(UUID id, String name, String cuit, String address,
                       String phone, String email, String status, Instant createdAt) {
        this(id, name, cuit, address, phone, email, status, null, createdAt);
    }

    public Consultorio(UUID id, String name, String cuit, String address,
                       String phone, String email, String status, UUID empresaId, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.empresaId = empresaId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String name, String cuit, String address, String phone, String email) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void inactivate() {
        this.status = "INACTIVE";
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.status = "ACTIVE";
        this.updatedAt = Instant.now();
    }

    public void assignEmpresa(UUID empresaId) {
        this.empresaId = empresaId;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() { return "ACTIVE".equals(this.status); }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCuit() { return cuit; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public UUID getEmpresaId() { return empresaId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
