package com.akine_api.domain.repository.convenio;

import com.akine_api.domain.model.convenio.ConvenioVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioVersionRepositoryPort {
    ConvenioVersion save(ConvenioVersion v);
    Optional<ConvenioVersion> findById(UUID id);
    List<ConvenioVersion> findByConvenioId(UUID convenioId);
    Optional<ConvenioVersion> findVigenteByConvenioId(UUID convenioId);
    int countLotesByVersionId(UUID versionId);
}
