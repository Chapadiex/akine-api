package com.akine_api.application.service.facturacion;

import com.akine_api.application.service.cobertura.PacienteCoberturaService;
import com.akine_api.domain.model.cobertura.PacienteCobertura;
import com.akine_api.domain.model.facturacion.*;
import com.akine_api.domain.repository.facturacion.AtencionFacturableRepositoryPort;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import com.akine_api.domain.repository.facturacion.ConvenioPrestacionValorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerarAtencionFacturableService {

    private final AtencionFacturableRepositoryPort atencionFacturableRepository;
    private final ConvenioFinanciadorRepositoryPort convenioRepository;
    private final ConvenioPrestacionValorRepositoryPort valorRepository;
    private final PacienteCoberturaService pacienteCoberturaService;

    @Transactional
    public AtencionFacturable generarSnapshot(UUID atencionId, UUID pacienteId, UUID prestacionId, LocalDate fechaAtencion) {
        
        // 1. Obtener cobertura principal del paciente
        PacienteCobertura cobertura = pacienteCoberturaService.findByPacienteId(pacienteId).stream()
                .filter(PacienteCobertura::getPrincipal)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Paciente sin cobertura principal configurada"));

        // 2. Resolver Convenio vigente para el financiador
        ConvenioFinanciador convenio = convenioRepository.findByFinanciadorId(cobertura.getFinanciadorId()).stream()
                .filter(c -> !fechaAtencion.isBefore(c.getVigenciaDesde()) && 
                            (c.getVigenciaHasta() == null || !fechaAtencion.isAfter(c.getVigenciaHasta())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró convenio vigente para el financiador"));

        // 3. Buscar Arancel (Valor) vigente
        List<ConvenioPrestacionValor> valores = valorRepository.findByConvenioId(convenio.getId());
        
        // Prioridad: 1. Valor por Plan específico, 2. Valor general por Convenio (planId null)
        ConvenioPrestacionValor valorVigente = valores.stream()
                .filter(v -> v.getPrestacionId().equals(prestacionId))
                .filter(v -> !fechaAtencion.isBefore(v.getVigenciaDesde()) && 
                            (v.getVigenciaHasta() == null || !fechaAtencion.isAfter(v.getVigenciaHasta())))
                .filter(v -> cobertura.getPlanId().equals(v.getPlanId())) // Coincidencia de plan
                .findFirst()
                .orElseGet(() -> valores.stream()
                        .filter(v -> v.getPrestacionId().equals(prestacionId))
                        .filter(v -> !fechaAtencion.isBefore(v.getVigenciaDesde()) && 
                                    (v.getVigenciaHasta() == null || !fechaAtencion.isAfter(v.getVigenciaHasta())))
                        .filter(v -> v.getPlanId() == null) // Valor general
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No se encontró arancel vigente para la prestación"))
                );

        // 4. Crear AtencionFacturable (Snapshot)
        AtencionFacturable snapshot = AtencionFacturable.builder()
                .atencionId(atencionId)
                .pacienteId(pacienteId)
                .convenioId(convenio.getId())
                .prestacionId(prestacionId)
                .importeUnitarioSnapshot(valorVigente.getImporteBase())
                .importeTotalSnapshot(valorVigente.getImporteBase()) // Por ahora asumimos 1 unidad
                .importeCopagoSnapshot(valorVigente.getImporteCopago())
                .estadoFacturacion(EstadoFacturacion.PENDIENTE)
                .facturable(true)
                .build();

        return atencionFacturableRepository.save(snapshot);
    }
}
