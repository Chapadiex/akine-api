package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.CobroPacienteService;
import com.akine_api.domain.model.cobro.CobroPaciente;
import com.akine_api.domain.model.cobro.CobroPacienteDetalle;
import com.akine_api.interfaces.api.v1.cobro.dto.AnularCobroRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.CobroPacienteDetalleRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.CobroPacienteRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.CobroPacienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/cobros")
@RequiredArgsConstructor
public class CobroPacienteController {

    private final CobroPacienteService cobroPacienteService;

    @PostMapping
    public ResponseEntity<CobroPacienteResponse> cobrar(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody CobroPacienteRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        List<CobroPacienteDetalle> detalles = req.getDetalles().stream()
                .map((CobroPacienteDetalleRequest d) -> CobroPacienteDetalle.builder()
                        .medioPago(d.getMedioPago())
                        .importe(d.getImporte())
                        .referenciaOperacion(d.getReferenciaOperacion())
                        .cuotas(d.getCuotas())
                        .banco(d.getBanco())
                        .marcaTarjeta(d.getMarcaTarjeta())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        CobroPaciente cobro = cobroPacienteService.cobrar(
                consultorioId,
                req.getCajaDiariaId(),
                req.getPacienteId(),
                req.getSesionId(),
                req.getImporteTotal(),
                detalles,
                req.getObservaciones(),
                principal.getUsername());

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}").buildAndExpand(cobro.getId()).toUri())
                .body(toResponse(cobro));
    }

    @GetMapping("/{cobroId}")
    public ResponseEntity<CobroPacienteResponse> findById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cobroId) {
        CobroPaciente cobro = cobroPacienteService.findById(cobroId);
        return ResponseEntity.ok(toResponse(cobro));
    }

    @GetMapping
    public ResponseEntity<List<CobroPacienteResponse>> findByPaciente(
            @PathVariable UUID consultorioId,
            @RequestParam UUID pacienteId) {
        List<CobroPaciente> cobros = cobroPacienteService.findByPacienteId(pacienteId, consultorioId);
        return ResponseEntity.ok(cobros.stream().map(this::toResponse).toList());
    }

    @GetMapping("/caja/{cajaDiariaId}")
    public ResponseEntity<List<CobroPacienteResponse>> findByCaja(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cajaDiariaId) {
        List<CobroPaciente> cobros = cobroPacienteService.findByCajaDiariaId(cajaDiariaId);
        return ResponseEntity.ok(cobros.stream().map(this::toResponse).toList());
    }

    @PostMapping("/{cobroId}/anular")
    public ResponseEntity<CobroPacienteResponse> anular(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cobroId,
            @Valid @RequestBody AnularCobroRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CobroPaciente cobro = cobroPacienteService.anular(cobroId, req.getMotivo(), principal.getUsername());
        return ResponseEntity.ok(toResponse(cobro));
    }

    private CobroPacienteResponse toResponse(CobroPaciente c) {
        CobroPacienteResponse r = new CobroPacienteResponse();
        r.setId(c.getId());
        r.setConsultorioId(c.getConsultorioId());
        r.setCajaDiariaId(c.getCajaDiariaId());
        r.setPacienteId(c.getPacienteId());
        r.setSesionId(c.getSesionId());
        r.setLiquidacionSesionId(c.getLiquidacionSesionId());
        r.setEstado(c.getEstado());
        r.setFechaCobro(c.getFechaCobro());
        r.setImporteTotal(c.getImporteTotal());
        r.setEsPagoMixto(c.getEsPagoMixto());
        r.setComprobanteNumero(c.getComprobanteNumero());
        r.setReciboEmitido(c.getReciboEmitido());
        r.setObservaciones(c.getObservaciones());
        r.setCobradoPor(c.getCobradoPor());
        r.setAnuladoPor(c.getAnuladoPor());
        r.setAnuladoEn(c.getAnuladoEn());
        r.setMotivoAnulacion(c.getMotivoAnulacion());
        r.setVersion(c.getVersion());
        if (c.getDetalles() != null) {
            r.setDetalles(c.getDetalles().stream().map(d -> {
                CobroPacienteResponse.DetalleItem item = new CobroPacienteResponse.DetalleItem();
                item.setId(d.getId());
                item.setMedioPago(d.getMedioPago());
                item.setImporte(d.getImporte());
                item.setReferenciaOperacion(d.getReferenciaOperacion());
                item.setCuotas(d.getCuotas());
                item.setBanco(d.getBanco());
                item.setMarcaTarjeta(d.getMarcaTarjeta());
                return item;
            }).toList());
        }
        return r;
    }
}
