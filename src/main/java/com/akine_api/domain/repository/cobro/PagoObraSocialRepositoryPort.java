package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.PagoObraSocial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoObraSocialRepositoryPort {
    PagoObraSocial save(PagoObraSocial pago);
    Optional<PagoObraSocial> findById(UUID id);
    List<PagoObraSocial> findByConsultorioId(UUID consultorioId);
    List<PagoObraSocial> findByLoteId(UUID loteId);
}
