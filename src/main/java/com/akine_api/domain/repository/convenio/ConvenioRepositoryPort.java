package com.akine_api.domain.repository.convenio;

import com.akine_api.domain.model.convenio.Convenio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioRepositoryPort {
    Convenio save(Convenio c);
    Optional<Convenio> findById(UUID id);
    List<Convenio> findByConsultorioId(UUID consultorioId);
    void delete(UUID id);
}
