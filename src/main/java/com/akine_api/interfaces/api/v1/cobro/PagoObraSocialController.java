package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.PagoObraSocialService;
import com.akine_api.domain.model.cobro.PagoObraSocial;
import com.akine_api.interfaces.api.v1.cobro.dto.ImputarPagoOsRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.PagoObraSocialResponse;
import com.akine_api.interfaces.api.v1.cobro.dto.RegistrarPagoOsRequest;
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
@RequestMapping("/api/v1/consultorios/{consultorioId}/pagos-os")
@RequiredArgsConstructor
public class PagoObraSocialController {

    private final PagoObraSocialService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoObraSocialResponse>> list(
            @PathVariable UUID consultorioId) {
        return ResponseEntity.ok(
                pagoService.findByConsultorioId(consultorioId).stream()
                        .map(this::toResponse)
                        .toList());
    }

    @GetMapping("/{pagoId}")
    public ResponseEntity<PagoObraSocialResponse> findById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pagoId) {
        return ResponseEntity.ok(toResponse(pagoService.findById(pagoId)));
    }

    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<PagoObraSocialResponse>> byLote(
            @PathVariable UUID consultorioId,
            @PathVariable UUID loteId) {
        return ResponseEntity.ok(
                pagoService.findByLoteId(loteId).stream()
                        .map(this::toResponse)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<PagoObraSocialResponse> registrar(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody RegistrarPagoOsRequest req,
            @AuthenticationPrincipal UserDetails user) {
        PagoObraSocial pago = pagoService.registrar(
                consultorioId, req.getLoteId(),
                req.getImporteRecibido(), req.getFechaNotificacion(),
                req.getObservaciones(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pago));
    }

    @PostMapping("/{pagoId}/imputar")
    public ResponseEntity<PagoObraSocialResponse> imputar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pagoId,
            @Valid @RequestBody ImputarPagoOsRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                toResponse(pagoService.imputar(pagoId, req.getCajaDiariaId(), user.getUsername())));
    }

    // ── mapping ───────────────────────────────────────────────────────────────

    private PagoObraSocialResponse toResponse(PagoObraSocial pago) {
        PagoObraSocialResponse r = new PagoObraSocialResponse();
        r.setId(pago.getId());
        r.setConsultorioId(pago.getConsultorioId());
        r.setLoteId(pago.getLoteId());
        r.setFinanciadorId(pago.getFinanciadorId());
        r.setImporteEsperado(pago.getImporteEsperado());
        r.setImporteRecibido(pago.getImporteRecibido());
        r.setDiferencia(pago.getDiferencia());
        r.setFechaNotificacion(pago.getFechaNotificacion());
        r.setFechaImputacion(pago.getFechaImputacion());
        r.setCajaDiariaId(pago.getCajaDiariaId());
        r.setImputadoPor(pago.getImputadoPor());
        r.setImputadoEn(pago.getImputadoEn());
        r.setObservaciones(pago.getObservaciones());
        r.setRegistradoPor(pago.getRegistradoPor());
        r.setCreatedAt(pago.getCreatedAt());
        r.setVersion(pago.getVersion());
        return r;
    }
}
