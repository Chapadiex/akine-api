package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AdjuntoClinico {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final UUID sesionId;
    private final String storageKey;
    private final String originalFilename;
    private final String contentType;
    private final long sizeBytes;
    private final UUID createdByUserId;
    private final Instant createdAt;

    public AdjuntoClinico(UUID id,
                          UUID consultorioId,
                          UUID pacienteId,
                          UUID sesionId,
                          String storageKey,
                          String originalFilename,
                          String contentType,
                          long sizeBytes,
                          UUID createdByUserId,
                          Instant createdAt) {
        if (consultorioId == null || pacienteId == null || sesionId == null) {
            throw new IllegalArgumentException("Consultorio, paciente y sesion son obligatorios");
        }
        if (storageKey == null || storageKey.isBlank()
                || originalFilename == null || originalFilename.isBlank()
                || contentType == null || contentType.isBlank()
                || sizeBytes <= 0) {
            throw new IllegalArgumentException("El adjunto clinico tiene metadata invalida");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.sesionId = sesionId;
        this.storageKey = storageKey;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getSesionId() { return sesionId; }
    public String getStorageKey() { return storageKey; }
    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public Instant getCreatedAt() { return createdAt; }
}
