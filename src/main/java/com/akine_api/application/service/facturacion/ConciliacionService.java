package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.facturacion.*;
import com.akine_api.domain.repository.facturacion.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConciliacionService {

    private final AtencionFacturableRepositoryPort atencionRepository;
    private final LotePresentacionItemRepositoryPort loteItemRepository;
    private final LiquidacionAjusteRepositoryPort ajusteRepository;
    // Repositorios adicionales se inyectarían aquí para nombres, etc.

    @Transactional(readOnly = true)
    public List<ConciliacionAtencion> generarInformePaciente(UUID pacienteId) {
        List<ConciliacionAtencion> informe = new ArrayList<>();
        
        // 1. Obtener todas las atenciones del paciente
        List<AtencionFacturable> atenciones = atencionRepository.findByPacienteId(pacienteId);

        for (AtencionFacturable at : atenciones) {
            // 2. Buscar si está en algún lote (Simplificado: asumiendo que el lote_item tiene la info)
            // En una implementación real usaríamos un JOIN o un port específico
            
            BigDecimal presentado = at.getImporteTotalSnapshot(); // Por defecto lo que se mandó
            BigDecimal liquidado = presentado; // Se asume liquidado completo si no hay ajustes
            
            // 3. (Simulado) Buscar ajustes para esta atención
            // List<LiquidacionAjuste> ajustes = ajusteRepository.findByLoteItemId(...);
            // liquidado = liquidado.subtract(sumaAjustes);

            informe.add(ConciliacionAtencion.builder()
                    .atencionId(at.getAtencionId())
                    .pacienteNombre("Paciente " + pacienteId)
                    .importeSnapshot(at.getImporteUnitarioSnapshot())
                    .importePresentado(presentado)
                    .importeLiquidado(liquidado)
                    .importePagado(liquidado) // Simulado
                    .diferencia(at.getImporteUnitarioSnapshot().subtract(liquidado))
                    .estadoFinal(at.getEstadoFacturacion().name())
                    .build());
        }

        return informe;
    }
}
