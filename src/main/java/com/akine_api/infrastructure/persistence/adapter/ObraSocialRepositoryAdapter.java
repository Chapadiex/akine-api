package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.dto.result.ObraSocialListItemResult;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.domain.model.ObraSocial;
import com.akine_api.domain.model.ObraSocialEstado;
import com.akine_api.infrastructure.persistence.mapper.ObraSocialEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ObraSocialJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ObraSocialRepositoryAdapter implements ObraSocialRepositoryPort {

    private final ObraSocialJpaRepository obraSocialRepo;
    private final ObraSocialEntityMapper mapper;

    public ObraSocialRepositoryAdapter(ObraSocialJpaRepository obraSocialRepo,
                                       ObraSocialEntityMapper mapper) {
        this.obraSocialRepo = obraSocialRepo;
        this.mapper = mapper;
    }

    @Override
    public ObraSocial save(ObraSocial obraSocial) {
        return mapper.toDomain(obraSocialRepo.save(mapper.toEntity(obraSocial)));
    }

    @Override
    public Optional<ObraSocial> findById(UUID obraSocialId) {
        return obraSocialRepo.findById(obraSocialId).map(mapper::toDomain);
    }

    @Override
    public Optional<ObraSocial> findByIdAndConsultorioId(UUID obraSocialId, UUID consultorioId) {
        return obraSocialRepo.findByIdAndConsultorioId(obraSocialId, consultorioId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByConsultorioIdAndCuit(UUID consultorioId, String cuit) {
        return obraSocialRepo.existsByConsultorioIdAndCuit(consultorioId, cuit);
    }

    @Override
    public boolean existsByConsultorioIdAndCuitAndIdNot(UUID consultorioId, String cuit, UUID id) {
        return obraSocialRepo.existsByConsultorioIdAndCuitAndIdNot(consultorioId, cuit, id);
    }

    @Override
    public Page<ObraSocialListItemResult> search(UUID consultorioId, String q, ObraSocialEstado estado, Boolean conPlanes, Pageable pageable) {
        return obraSocialRepo.search(consultorioId, q, estado, conPlanes, pageable)
                .map(entity -> new ObraSocialListItemResult(
                        entity.getId(),
                        entity.getConsultorioId(),
                        entity.getAcronimo(),
                        entity.getNombreCompleto(),
                        entity.getCuit(),
                        entity.getEmail(),
                        entity.getTelefono(),
                        entity.getRepresentante(),
                        entity.getEstado(),
                        entity.getPlanes().size(),
                        !entity.getPlanes().isEmpty(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ));
    }
}

