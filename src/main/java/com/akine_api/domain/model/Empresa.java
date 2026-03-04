package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Empresa {

    private final UUID id;
    private String name;
    private String cuit;
    private String address;
    private String city;
    private String province;
    private final Instant createdAt;
    private Instant updatedAt;

    public Empresa(UUID id, String name, String cuit, String address, String city, String province, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.city = city;
        this.province = province;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String name, String cuit, String address, String city, String province) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.city = city;
        this.province = province;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCuit() { return cuit; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getProvince() { return province; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
