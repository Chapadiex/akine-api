package com.akine_api.application.service.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.model.cobro.OrigenTipoCobro;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.domain.model.ConfiguracionConsultorio;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.repository.ConfiguracionConsultorioRepositoryPort;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import com.akine_api.domain.repository.facturacion.ConvenioPrestacionValorRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * STATELESS service that executes the 9-step liquidation algorithm (§5.3).
 * No side effects — receives domain objects, returns a LiquidacionSesion ready to save.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionCalculadorService {

    private final ConvenioFinanciadorRepositoryPort convenioRepo;
    private final ConvenioPrestacionValorRepositoryPort convenioPrestacionRepo;
    private final ConfiguracionConsultorioRepositoryPort configuracionRepo;

    /**
     * Executes the 9-step algorithm and returns an unsaved LiquidacionSesion.
     *
     * @param sesion             the SesionClinica (must be cerrada clinicamente)
     * @param sesionAdm          the SesionAdministrativa (may be null → no coverage data)
     * @param liquidadoPorUserId actor user
     * @return fully calculated LiquidacionSesion (not yet persisted)
     */
    public LiquidacionSesion calcular(SesionClinica sesion,
                                       SesionAdministrativa sesionAdm,
                                       UUID liquidadoPorUserId) {
        LocalDate fechaSesion = sesion.getFechaAtencion() != null
                ? sesion.getFechaAtencion().toLocalDate()
                : LocalDate.now();

        // Step 1 & 2: Find active convenio
        Optional<ConvenioFinanciador> convenioOpt = Optional.empty();
        if (sesionAdm != null && sesionAdm.getFinanciadorId() != null) {
            convenioOpt = convenioRepo.findVigenteByFinanciadorPlanConsultorio(
                    sesionAdm.getFinanciadorId(),
                    sesionAdm.getPlanId(),
                    sesion.getConsultorioId(),
                    fechaSesion);
        }

        boolean documentacionCompleta = sesionAdm != null
                && Boolean.TRUE.equals(sesionAdm.getDocumentacionCompleta());
        boolean esFacturableOs = sesionAdm != null
                && Boolean.TRUE.equals(sesionAdm.getEsFacturableOs());

        if (convenioOpt.isEmpty()) {
            // Step 2: no active convenio → particular
            return buildParticular(sesion, sesionAdm, documentacionCompleta,
                    liquidadoPorUserId, OrigenTipoCobro.AUTOMATICO);
        }

        ConvenioFinanciador convenio = convenioOpt.get();

        // Step 3: find convenio_prestacion
        List<ConvenioPrestacionValor> prestaciones = convenioPrestacionRepo.findByConvenioId(convenio.getId());
        if (prestaciones.isEmpty()) {
            // No prestacion configured → fallback to particular
            return buildParticular(sesion, sesionAdm, documentacionCompleta,
                    liquidadoPorUserId, OrigenTipoCobro.AUTOMATICO);
        }

        // Use first active prestacion (Fase 3: no prestacion code filter, use first available)
        Optional<ConvenioPrestacionValor> prestacionOpt = prestaciones.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .findFirst();

        if (prestacionOpt.isEmpty()) {
            return buildParticular(sesion, sesionAdm, documentacionCompleta,
                    liquidadoPorUserId, OrigenTipoCobro.AUTOMATICO);
        }

        ConvenioPrestacionValor prestacion = prestacionOpt.get();

        // Step 4: valor_bruto
        BigDecimal valorBruto = prestacion.getImporteBase() != null
                ? prestacion.getImporteBase()
                : BigDecimal.ZERO;

        // Step 5: copago_importe
        BigDecimal copagoImporte = BigDecimal.ZERO;
        if (prestacion.getImporteCopago() != null && prestacion.getImporteCopago().compareTo(BigDecimal.ZERO) > 0) {
            copagoImporte = prestacion.getImporteCopago();
        } else if (prestacion.getCopajoPorcentaje() != null && prestacion.getCopajoPorcentaje().compareTo(BigDecimal.ZERO) > 0) {
            copagoImporte = valorBruto.multiply(prestacion.getCopajoPorcentaje())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }

        BigDecimal coseguroImporte = prestacion.getCoseguroImporte() != null
                ? prestacion.getCoseguroImporte()
                : BigDecimal.ZERO;

        // Determine tipo_liquidacion
        TipoLiquidacion tipo;
        if (copagoImporte.compareTo(BigDecimal.ZERO) > 0 || coseguroImporte.compareTo(BigDecimal.ZERO) > 0) {
            tipo = TipoLiquidacion.MIXTA;
        } else {
            // OS sin copago → full to OS
            tipo = TipoLiquidacion.OS;
        }

        // Step 6: importe_paciente
        BigDecimal importePaciente;
        switch (tipo) {
            case MIXTA -> importePaciente = copagoImporte.add(coseguroImporte);
            case OS -> importePaciente = BigDecimal.ZERO;
            default -> importePaciente = valorBruto;
        }

        BigDecimal descuentoImporte = BigDecimal.ZERO;

        // Step 7: importe_obra_social = valor_bruto - descuento - importe_paciente
        BigDecimal importeOs = valorBruto.subtract(descuentoImporte).subtract(importePaciente);
        if (importeOs.compareTo(BigDecimal.ZERO) < 0) importeOs = BigDecimal.ZERO;

        // Step 8: importe_total_liquidado
        BigDecimal importeTotal = importePaciente.add(importeOs);

        // Build snapshot
        String snapshot = buildConvenioSnapshot(convenio, prestacion);

        EstadoLiquidacion estado = resolveEstado(tipo, documentacionCompleta);

        return LiquidacionSesion.builder()
                .consultorioId(sesion.getConsultorioId())
                .sesionId(sesion.getId())
                .pacienteId(sesion.getPacienteId())
                .financiadorId(sesionAdm != null ? sesionAdm.getFinanciadorId() : null)
                .planId(sesionAdm != null ? sesionAdm.getPlanId() : null)
                .convenioId(convenio.getId())
                .tipoLiquidacion(tipo)
                .estado(estado)
                .motivoBloqueo(estado == EstadoLiquidacion.BLOQUEADA_POR_DOCUMENTACION
                        ? "Documentación incompleta" : null)
                .valorBruto(valorBruto)
                .descuentoImporte(descuentoImporte)
                .copagoImporte(copagoImporte)
                .coseguroImporte(coseguroImporte)
                .importePaciente(importePaciente)
                .importeObraSocial(importeOs)
                .importeTotalLiquidado(importeTotal)
                .documentacionCompleta(documentacionCompleta)
                .esFacturableOs(esFacturableOs)
                .requiereRevisionManual(false)
                .origenTipoCobro(OrigenTipoCobro.AUTOMATICO)
                .convenioVigenteSnapshot(snapshot)
                .liquidadoPor(liquidadoPorUserId)
                .build();
    }

    // ─── private helpers ─────────────────────────────────────────────────────

    private LiquidacionSesion buildParticular(SesionClinica sesion,
                                               SesionAdministrativa sesionAdm,
                                               boolean documentacionCompleta,
                                               UUID liquidadoPorUserId,
                                               OrigenTipoCobro origen) {
        BigDecimal valorBruto = configuracionRepo.findByConsultorioId(sesion.getConsultorioId())
                .map(ConfiguracionConsultorio::getArancelParticularPorSesion)
                .filter(a -> a != null && a.compareTo(BigDecimal.ZERO) > 0)
                .orElse(BigDecimal.ZERO);
        EstadoLiquidacion estado = documentacionCompleta
                ? EstadoLiquidacion.LIQUIDADA_PARTICULAR
                : EstadoLiquidacion.LIQUIDADA_PARTICULAR; // particular always valid even without docs

        return LiquidacionSesion.builder()
                .consultorioId(sesion.getConsultorioId())
                .sesionId(sesion.getId())
                .pacienteId(sesion.getPacienteId())
                .financiadorId(sesionAdm != null ? sesionAdm.getFinanciadorId() : null)
                .planId(null)
                .convenioId(null)
                .tipoLiquidacion(TipoLiquidacion.PARTICULAR)
                .estado(estado)
                .valorBruto(valorBruto)
                .descuentoImporte(BigDecimal.ZERO)
                .copagoImporte(BigDecimal.ZERO)
                .coseguroImporte(BigDecimal.ZERO)
                .importePaciente(valorBruto)
                .importeObraSocial(BigDecimal.ZERO)
                .importeTotalLiquidado(valorBruto)
                .documentacionCompleta(documentacionCompleta)
                .esFacturableOs(false)
                .requiereRevisionManual(false)
                .origenTipoCobro(origen)
                .convenioVigenteSnapshot(null)
                .liquidadoPor(liquidadoPorUserId)
                .build();
    }

    /** Step 9: if documentacion_completa = false AND tipo != PARTICULAR → BLOQUEADA */
    private EstadoLiquidacion resolveEstado(TipoLiquidacion tipo, boolean documentacionCompleta) {
        if (!documentacionCompleta && tipo != TipoLiquidacion.PARTICULAR) {
            return EstadoLiquidacion.BLOQUEADA_POR_DOCUMENTACION;
        }
        return switch (tipo) {
            case PARTICULAR -> EstadoLiquidacion.LIQUIDADA_PARTICULAR;
            case MIXTA -> EstadoLiquidacion.LIQUIDADA_MIXTA;
            case OS -> EstadoLiquidacion.LIQUIDADA_OS;
        };
    }

    private String buildConvenioSnapshot(ConvenioFinanciador convenio, ConvenioPrestacionValor prestacion) {
        try {
            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            var snap = new java.util.LinkedHashMap<String, Object>();
            snap.put("convenioId", convenio.getId());
            snap.put("convenioNombre", convenio.getNombre());
            snap.put("financiadorId", convenio.getFinanciadorId());
            snap.put("planId", convenio.getPlanId());
            snap.put("vigenciaDesde", convenio.getVigenciaDesde());
            snap.put("vigenciaHasta", convenio.getVigenciaHasta());
            snap.put("prestacionId", prestacion.getId());
            snap.put("importeBase", prestacion.getImporteBase());
            snap.put("importeCopago", prestacion.getImporteCopago());
            snap.put("copajoPorcentaje", prestacion.getCopajoPorcentaje());
            snap.put("coseguroImporte", prestacion.getCoseguroImporte());
            return om.writeValueAsString(snap);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize convenio snapshot", e);
            return null;
        }
    }
}
