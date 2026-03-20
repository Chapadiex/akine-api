package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.facturacion.*;
import com.akine_api.domain.repository.facturacion.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LotePresentacionService {

    private final LotePresentacionRepositoryPort loteRepository;
    private final LotePresentacionItemRepositoryPort loteItemRepository;
    private final AtencionFacturableRepositoryPort atencionFacturableRepository;
    private final ConvenioFinanciadorService convenioService;

    @Transactional
    public LotePresentacion generarLote(UUID financiadorId, UUID convenioId, String periodo) {
        
        // 1. Validar convenio
        ConvenioFinanciador convenio = convenioService.findById(convenioId);

        // 2. Buscar atenciones facturables PENDIENTES para este convenio y periodo (simplificado por ahora)
        // En una implementación real filtraríamos por fecha de atención dentro del periodo
        List<AtencionFacturable> pendientes = atencionFacturableRepository.findByPacienteId(null); // Placeholder logic
        
        // Filtrado manual temporal para la demo/estructura
        List<AtencionFacturable> aIncluir = pendientes.stream()
                .filter(a -> a.getEstadoFacturacion() == EstadoFacturacion.PENDIENTE || a.getEstadoFacturacion() == EstadoFacturacion.LISTA_PARA_PRESENTAR)
                .filter(a -> a.getConvenioId().equals(convenioId))
                .collect(Collectors.toList());

        if (aIncluir.isEmpty()) {
            throw new RuntimeException("No hay atenciones pendientes para facturar en este convenio/periodo");
        }

        // 3. Crear cabecera del lote
        BigDecimal totalLote = aIncluir.stream()
                .map(AtencionFacturable::getImporteTotalSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LotePresentacion lote = LotePresentacion.builder()
                .financiadorId(financiadorId)
                .convenioId(convenioId)
                .periodo(periodo)
                .importeNetoPresentado(totalLote)
                .estadoLote(EstadoLote.BORRADOR)
                .build();

        LotePresentacion savedLote = loteRepository.save(lote);

        // 4. Crear ítems del lote y actualizar estados de atención
        for (AtencionFacturable atencion : aIncluir) {
            LotePresentacionItem item = LotePresentacionItem.builder()
                    .loteId(savedLote.getId())
                    .atencionFacturableId(atencion.getId())
                    .importePresentado(atencion.getImporteTotalSnapshot())
                    .estadoItem(EstadoLoteItem.INCLUIDO)
                    .build();
            
            loteItemRepository.save(item);

            // Actualizar estado de la atención
            atencion.setEstadoFacturacion(EstadoFacturacion.PRESENTADA);
            atencionFacturableRepository.save(atencion);
        }

        return savedLote;
    }
}
