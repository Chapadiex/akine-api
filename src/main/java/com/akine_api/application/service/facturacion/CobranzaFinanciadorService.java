package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.facturacion.*;
import com.akine_api.domain.repository.facturacion.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CobranzaFinanciadorService {

    private final LiquidacionFinanciadorRepositoryPort liquidacionRepository;
    private final LiquidacionAjusteRepositoryPort ajusteRepository;
    private final PagoFinanciadorRepositoryPort pagoRepository;

    @Transactional
    public LiquidacionFinanciador registrarLiquidacion(LiquidacionFinanciador liquidacion) {
        liquidacion.setEstadoConciliacion(EstadoConciliacion.PENDIENTE);
        return liquidacionRepository.save(liquidacion);
    }

    @Transactional
    public LiquidacionAjuste registrarAjuste(LiquidacionAjuste ajuste) {
        // Al registrar un ajuste, deberíamos recalcular los totales de la liquidación
        LiquidacionFinanciador liq = liquidacionRepository.findById(ajuste.getLiquidacionId())
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));
        
        LiquidacionAjuste saved = ajusteRepository.save(ajuste);
        
        // Actualizar totales de liquidación (simplificado)
        if (ajuste.getTipoAjuste().name().startsWith("DEBITO")) {
            liq.setImporteDebitos(liq.getImporteDebitos().add(ajuste.getImporte()));
            liq.setImporteNeto(liq.getImporteBruto().subtract(liq.getImporteDebitos()));
            liquidacionRepository.save(liq);
        }
        
        return saved;
    }

    @Transactional
    public PagoFinanciador registrarPago(PagoFinanciador pago) {
        pago.setConciliado(false);
        return pagoRepository.save(pago);
    }
}
