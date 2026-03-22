package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.LoteFacturacionOsService;
import com.akine_api.domain.model.cobro.LoteFacturacionOs;
import com.akine_api.domain.model.cobro.LoteFacturacionOsDetalle;
import com.akine_api.interfaces.api.v1.cobro.dto.GenerarLoteOsRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.LoteFacturacionOsDetalleResponse;
import com.akine_api.interfaces.api.v1.cobro.dto.LoteFacturacionOsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/lotes-os")
@RequiredArgsConstructor
public class LoteFacturacionOsController {

    private final LoteFacturacionOsService loteService;

    @GetMapping
    public ResponseEntity<List<LoteFacturacionOsResponse>> list(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(
                loteService.findByConsultorioId(consultorioId).stream()
                        .map(this::toResponse)
                        .toList());
    }

    @GetMapping("/{loteId}")
    public ResponseEntity<LoteFacturacionOsResponse> findById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID loteId) {
        return ResponseEntity.ok(toResponse(loteService.findById(loteId)));
    }

    @PostMapping
    public ResponseEntity<LoteFacturacionOsResponse> generar(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody GenerarLoteOsRequest req,
            @AuthenticationPrincipal UserDetails user) {
        LoteFacturacionOs lote = loteService.generarLote(
                consultorioId, req.getFinanciadorId(), req.getPlanId(),
                req.getPeriodo(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(lote));
    }

    @PostMapping("/{loteId}/cerrar")
    public ResponseEntity<LoteFacturacionOsResponse> cerrar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID loteId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(toResponse(loteService.cerrarLote(loteId, user.getUsername())));
    }

    @PostMapping("/{loteId}/presentar")
    public ResponseEntity<LoteFacturacionOsResponse> presentar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID loteId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(toResponse(loteService.marcarPresentado(loteId, user.getUsername())));
    }

    // ── mapping ───────────────────────────────────────────────────────────────

    private LoteFacturacionOsResponse toResponse(LoteFacturacionOs lote) {
        LoteFacturacionOsResponse r = new LoteFacturacionOsResponse();
        r.setId(lote.getId());
        r.setConsultorioId(lote.getConsultorioId());
        r.setFinanciadorId(lote.getFinanciadorId());
        r.setPlanId(lote.getPlanId());
        r.setPeriodo(lote.getPeriodo());
        r.setEstado(lote.getEstado() != null ? lote.getEstado().name() : null);
        r.setCantidadSesiones(lote.getCantidadSesiones());
        r.setImporteTotalOs(lote.getImporteTotalOs());
        r.setImporteNeto(lote.getImporteNeto());
        r.setObservaciones(lote.getObservaciones());
        r.setCerradoEn(lote.getCerradoEn());
        r.setCerradoPor(lote.getCerradoPor());
        r.setPresentadoEn(lote.getPresentadoEn());
        r.setCreatedAt(lote.getCreatedAt());
        r.setVersion(lote.getVersion());
        if (lote.getDetalles() != null) {
            r.setDetalles(lote.getDetalles().stream().map(this::toDetalleResponse).toList());
        }
        return r;
    }

    private LoteFacturacionOsDetalleResponse toDetalleResponse(LoteFacturacionOsDetalle d) {
        LoteFacturacionOsDetalleResponse r = new LoteFacturacionOsDetalleResponse();
        r.setId(d.getId());
        r.setLiquidacionSesionId(d.getLiquidacionSesionId());
        r.setSesionId(d.getSesionId());
        r.setPacienteId(d.getPacienteId());
        r.setImporteOs(d.getImporteOs());
        return r;
    }
}
