package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.PagoFinanciador;

import java.util.List;
import java.util.UUID;

public interface PagoFinanciadorRepositoryPort {
    PagoFinanciador save(PagoFinanciador pago);
    List<PagoFinanciador> findByFinanciadorId(UUID financiadorId);
}
