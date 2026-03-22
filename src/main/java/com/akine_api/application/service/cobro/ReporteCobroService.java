package com.akine_api.application.service.cobro;

import com.akine_api.domain.exception.CajaNotFoundException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.repository.cobro.*;
import com.akine_api.interfaces.api.v1.cobro.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteCobroService {

    private final CajaDiariaRepositoryPort cajaRepo;
    private final MovimientoCajaRepositoryPort movimientoRepo;
    private final LiquidacionSesionRepositoryPort liquidacionRepo;
    private final LoteFacturacionOsRepositoryPort loteRepo;
    private final PagoObraSocialRepositoryPort pagoRepo;

    // ─── 1. Caja del día ──────────────────────────────────────────────────────

    public ReporteCajaDiaResponse reporteCajaDia(UUID consultorioId, UUID cajaId) {
        CajaDiaria caja = cajaRepo.findById(cajaId)
                .orElseThrow(() -> new CajaNotFoundException("Caja no encontrada: " + cajaId));

        List<MovimientoCaja> movimientos = movimientoRepo.findByCajaDiariaId(cajaId);

        BigDecimal totalIngresosPaciente = nullSafe(caja.getTotalIngresosPaciente());
        BigDecimal totalIngresosOs = nullSafe(caja.getTotalIngresosOs());
        BigDecimal totalIngresos = totalIngresosPaciente.add(totalIngresosOs);
        BigDecimal totalEgresos = nullSafe(caja.getTotalEgresos());

        ReporteCajaDiaResponse r = new ReporteCajaDiaResponse();
        r.setCajaId(caja.getId());
        r.setConsultorioId(caja.getConsultorioId());
        r.setFechaOperativa(caja.getFechaOperativa());
        r.setTurnoCaja(caja.getTurnoCaja());
        r.setEstado(caja.getEstado() != null ? caja.getEstado().name() : null);
        r.setSaldoInicial(nullSafe(caja.getSaldoInicial()));
        r.setTotalIngresosPaciente(totalIngresosPaciente);
        r.setTotalIngresosOs(totalIngresosOs);
        r.setTotalIngresos(totalIngresos);
        r.setTotalEgresos(totalEgresos);
        r.setSaldoTeorico(caja.getSaldoTeoricoCierre());
        r.setSaldoReal(caja.getSaldoRealCierre());
        r.setDiferencia(caja.getDiferenciaCierre());
        r.setMovimientos(movimientos.stream().map(this::toMovimientoResponse).toList());
        return r;
    }

    // ─── 2. Facturado vs cobrado ──────────────────────────────────────────────

    public List<ReporteFacturadoVsCobradoResponse> reporteFacturadoVsCobrado(UUID consultorioId) {
        List<LoteFacturacionOs> lotes = loteRepo.findByConsultorioId(consultorioId);
        return lotes.stream()
                .filter(l -> l.getEstado() != EstadoLoteOs.ANULADO)
                .map(lote -> {
                    List<PagoObraSocial> pagos = pagoRepo.findByLoteId(lote.getId());
                    BigDecimal importeCobrado = pagos.stream()
                            .map(PagoObraSocial::getImporteRecibido)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal importeFacturado = nullSafe(lote.getImporteNeto());
                    ReporteFacturadoVsCobradoResponse r = new ReporteFacturadoVsCobradoResponse();
                    r.setLoteId(lote.getId());
                    r.setFinanciadorId(lote.getFinanciadorId());
                    r.setPeriodo(lote.getPeriodo());
                    r.setEstadoLote(lote.getEstado().name());
                    r.setImporteFacturado(importeFacturado);
                    r.setImporteCobrado(importeCobrado);
                    r.setDiferencia(importeCobrado.subtract(importeFacturado));
                    r.setCantidadPagos(pagos.size());
                    return r;
                })
                .toList();
    }

    // ─── 3. Sesiones bloqueadas ───────────────────────────────────────────────

    public List<ReporteSesionesBloqueadasResponse> reporteSesionesBloqueadas(UUID consultorioId) {
        return liquidacionRepo
                .findByConsultorioIdAndEstado(consultorioId, EstadoLiquidacion.BLOQUEADA_POR_DOCUMENTACION)
                .stream()
                .map(l -> {
                    ReporteSesionesBloqueadasResponse r = new ReporteSesionesBloqueadasResponse();
                    r.setLiquidacionId(l.getId());
                    r.setSesionId(l.getSesionId());
                    r.setPacienteId(l.getPacienteId());
                    r.setMotivoBloqueo(l.getMotivoBloqueo());
                    r.setCreatedAt(l.getCreatedAt());
                    return r;
                })
                .toList();
    }

    // ─── 4. Copagos OS pendientes ─────────────────────────────────────────────

    public List<ReporteCopagosOsPendientesResponse> reporteCopagosPendientes(UUID consultorioId) {
        return liquidacionRepo
                .findByConsultorioIdAndTipoLiquidacion(consultorioId, TipoLiquidacion.MIXTA)
                .stream()
                .filter(l -> l.isEsFacturableOs()
                        && l.getEstado() != EstadoLiquidacion.ANULADA)
                .map(l -> {
                    ReporteCopagosOsPendientesResponse r = new ReporteCopagosOsPendientesResponse();
                    r.setLiquidacionId(l.getId());
                    r.setSesionId(l.getSesionId());
                    r.setPacienteId(l.getPacienteId());
                    r.setFinanciadorId(l.getFinanciadorId());
                    r.setCopagoImporte(nullSafe(l.getCopagoImporte()));
                    r.setImporteObraSocial(nullSafe(l.getImporteObraSocial()));
                    r.setEstado(l.getEstado().name());
                    r.setCreatedAt(l.getCreatedAt());
                    return r;
                })
                .toList();
    }

    // ─── 5. Productividad profesional ─────────────────────────────────────────

    public List<ReporteProductividadProfesionalResponse> reporteProductividad(UUID consultorioId) {
        Map<UUID, List<LiquidacionSesion>> byProfesional = liquidacionRepo
                .findByConsultorioId(consultorioId)
                .stream()
                .filter(l -> l.getEstado() != EstadoLiquidacion.ANULADA && l.getLiquidadoPor() != null)
                .collect(Collectors.groupingBy(LiquidacionSesion::getLiquidadoPor));

        return byProfesional.entrySet().stream()
                .map(entry -> {
                    List<LiquidacionSesion> sesiones = entry.getValue();
                    ReporteProductividadProfesionalResponse r = new ReporteProductividadProfesionalResponse();
                    r.setProfesionalId(entry.getKey());
                    r.setCantidadSesiones(sesiones.size());
                    r.setImporteTotalLiquidado(sesiones.stream()
                            .map(l -> nullSafe(l.getImporteTotalLiquidado()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    r.setImporteObraSocial(sesiones.stream()
                            .map(l -> nullSafe(l.getImporteObraSocial()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    r.setImportePaciente(sesiones.stream()
                            .map(l -> nullSafe(l.getImportePaciente()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    return r;
                })
                .toList();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private MovimientoCajaResponse toMovimientoResponse(MovimientoCaja m) {
        MovimientoCajaResponse r = new MovimientoCajaResponse();
        r.setId(m.getId());
        r.setCajaDiariaId(m.getCajaDiariaId());
        r.setTipoMovimiento(m.getTipoMovimiento());
        r.setOrigenMovimiento(m.getOrigenMovimiento());
        r.setOrigenId(m.getOrigenId());
        r.setFechaHora(m.getFechaHora());
        r.setDescripcion(m.getDescripcion());
        r.setImporte(m.getImporte());
        r.setSigno(m.getSigno());
        r.setMedioPago(m.getMedioPago());
        r.setAnulado(m.getAnulado());
        return r;
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
