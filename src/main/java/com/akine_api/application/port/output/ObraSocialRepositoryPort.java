package com.akine_api.application.port.output;

import com.akine_api.application.dto.result.ObraSocialListItemResult;
import com.akine_api.domain.model.ObraSocial;
import com.akine_api.domain.model.ObraSocialEstado;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ObraSocialRepositoryPort {
    ObraSocial save(ObraSocial obraSocial);
    Optional<ObraSocial> findById(UUID obraSocialId);
    Optional<ObraSocial> findByIdAndConsultorioId(UUID obraSocialId, UUID consultorioId);
    boolean existsByConsultorioIdAndCuit(UUID consultorioId, String cuit);
    boolean existsByConsultorioIdAndCuitAndIdNot(UUID consultorioId, String cuit, UUID id);
    org.springframework.data.domain.Page<ObraSocialListItemResult> search(UUID consultorioId, String q, ObraSocialEstado estado, Boolean conPlanes, Pageable pageable);
}

