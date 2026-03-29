package com.akine_api.domain.repository.convenio;

import com.akine_api.domain.model.convenio.Arancel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArancelRepositoryPort {
    Arancel save(Arancel a);
    Optional<Arancel> findById(UUID id);
    List<Arancel> findByConvenioVersionId(UUID versionId);
    List<Arancel> findVigentesByVersion(UUID versionId, LocalDate fecha);
    void deleteById(UUID id);
}
