package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.ReporteCobroService;
import com.akine_api.interfaces.api.v1.cobro.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/reportes")
@RequiredArgsConstructor
public class ReportesCobroController {

    private final ReporteCobroService reporteService;

    /** Reporte 1: resumen de caja + movimientos detallados. */
    @GetMapping("/caja-dia/{cajaId}")
    public ResponseEntity<ReporteCajaDiaResponse> cajaDia(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cajaId) {
        return ResponseEntity.ok(reporteService.reporteCajaDia(consultorioId, cajaId));
    }

    /** Reporte 2: facturado vs cobrado por lote OS. */
    @GetMapping("/facturado-vs-cobrado")
    public ResponseEntity<List<ReporteFacturadoVsCobradoResponse>> facturadoVsCobrado(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(reporteService.reporteFacturadoVsCobrado(consultorioId));
    }

    /** Reporte 3: sesiones bloqueadas por documentación incompleta. */
    @GetMapping("/sesiones-bloqueadas")
    public ResponseEntity<List<ReporteSesionesBloqueadasResponse>> sesionesBloqueadas(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(reporteService.reporteSesionesBloqueadas(consultorioId));
    }

    /** Reporte 4: copagos OS (tipo MIXTA) pendientes de liquidación. */
    @GetMapping("/copagos-pendientes")
    public ResponseEntity<List<ReporteCopagosOsPendientesResponse>> copagosPendientes(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(reporteService.reporteCopagosPendientes(consultorioId));
    }

    /** Reporte 5: productividad por profesional (sesiones + montos liquidados). */
    @GetMapping("/productividad")
    public ResponseEntity<List<ReporteProductividadProfesionalResponse>> productividad(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(reporteService.reporteProductividad(consultorioId));
    }
}
