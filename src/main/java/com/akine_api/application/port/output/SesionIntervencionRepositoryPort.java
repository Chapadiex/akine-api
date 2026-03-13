package com.akine_api.application.port.output;

import com.akine_api.domain.model.SesionIntervencion;
import java.util.List;
import java.util.UUID;

public interface SesionIntervencionRepositoryPort {
    SesionIntervencion save(SesionIntervencion intervencion);
    List<SesionIntervencion> saveAll(List<SesionIntervencion> intervenciones);
    List<SesionIntervencion> findBySesionId(UUID sesionId);
    void deleteBySesionId(UUID sesionId);
}
