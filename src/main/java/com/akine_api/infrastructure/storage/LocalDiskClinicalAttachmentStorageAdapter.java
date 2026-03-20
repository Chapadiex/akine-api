package com.akine_api.infrastructure.storage;

import com.akine_api.application.port.output.AttachmentStoragePort;
import com.akine_api.domain.exception.HistoriaClinicaStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Component
public class LocalDiskClinicalAttachmentStorageAdapter implements AttachmentStoragePort {

    private final Path root;

    public LocalDiskClinicalAttachmentStorageAdapter(
            @Value("${akine.clinical.attachments-root:${java.io.tmpdir}/akine-clinical}") String rootDir) {
        this.root = Paths.get(rootDir);
    }

    @Override
    public String store(UUID consultorioId,
                        UUID pacienteId,
                        UUID ownerId,
                        UUID adjuntoId,
                        String originalFilename,
                        byte[] content) {
        String extension = resolveExtension(originalFilename);
        String storageKey = consultorioId + "/" + pacienteId + "/" + ownerId + "/" + adjuntoId + extension;
        Path target = root.resolve(storageKey);
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
            return storageKey;
        } catch (IOException ex) {
            throw new HistoriaClinicaStorageException("No se pudo guardar el adjunto clinico", ex);
        }
    }

    @Override
    public byte[] load(String storageKey) {
        Path source = root.resolve(storageKey);
        try {
            return Files.readAllBytes(source);
        } catch (IOException ex) {
            throw new HistoriaClinicaStorageException("No se pudo leer el adjunto clinico", ex);
        }
    }

    @Override
    public void delete(String storageKey) {
        Path source = root.resolve(storageKey);
        try {
            Files.deleteIfExists(source);
        } catch (IOException ex) {
            throw new HistoriaClinicaStorageException("No se pudo eliminar el adjunto clinico", ex);
        }
    }

    private String resolveExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}
