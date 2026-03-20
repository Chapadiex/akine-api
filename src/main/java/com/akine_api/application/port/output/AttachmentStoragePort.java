package com.akine_api.application.port.output;

import java.util.UUID;

public interface AttachmentStoragePort {
    String store(UUID consultorioId,
                 UUID pacienteId,
                 UUID ownerId,
                 UUID adjuntoId,
                 String originalFilename,
                 byte[] content);

    byte[] load(String storageKey);

    void delete(String storageKey);
}
