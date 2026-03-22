package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.CajaDiariaService;
import com.akine_api.application.service.cobro.MovimientoCajaService;
import com.akine_api.domain.model.cobro.CajaDiaria;
import com.akine_api.domain.model.cobro.MovimientoCaja;
import com.akine_api.interfaces.api.v1.cobro.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/caja")
@RequiredArgsConstructor
public class CajaDiariaController {

    private final CajaDiariaService cajaDiariaService;
    private final MovimientoCajaService movimientoCajaService;

    @PostMapping
    public ResponseEntity<CajaDiariaResponse> abrir(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody AperturaCajaRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CajaDiaria caja = cajaDiariaService.abrir(
                consultorioId,
                req.getFechaOperativa(),
                req.getTurnoCaja(),
                req.getSaldoInicial(),
                req.getObservaciones(),
                principal.getUsername());
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}").buildAndExpand(caja.getId()).toUri())
                .body(toResponse(caja));
    }

    @GetMapping("/{cajaId}")
    public ResponseEntity<CajaDiariaResponse> findById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cajaId) {
        CajaDiaria caja = cajaDiariaService.findById(cajaId);
        return ResponseEntity.ok(toResponse(caja));
    }

    @GetMapping
    public ResponseEntity<List<CajaDiariaResponse>> findByFecha(
            @PathVariable UUID consultorioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<CajaDiaria> cajas = cajaDiariaService.findByConsultorioIdAndFecha(consultorioId, fecha);
        return ResponseEntity.ok(cajas.stream().map(this::toResponse).toList());
    }

    @PostMapping("/{cajaId}/cerrar")
    public ResponseEntity<CajaDiariaResponse> cerrar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cajaId,
            @Valid @RequestBody CierreCajaRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CajaDiaria caja = cajaDiariaService.cerrar(
                cajaId,
                req.getSaldoReal(),
                req.getObservaciones(),
                principal.getUsername());
        return ResponseEntity.ok(toResponse(caja));
    }

    @GetMapping("/{cajaId}/movimientos")
    public ResponseEntity<List<MovimientoCajaResponse>> movimientos(
            @PathVariable UUID consultorioId,
            @PathVariable UUID cajaId) {
        List<MovimientoCaja> movimientos = movimientoCajaService.findByCajaDiariaId(cajaId);
        return ResponseEntity.ok(movimientos.stream().map(this::toMovResponse).toList());
    }

    private CajaDiariaResponse toResponse(CajaDiaria c) {
        CajaDiariaResponse r = new CajaDiariaResponse();
        r.setId(c.getId());
        r.setConsultorioId(c.getConsultorioId());
        r.setFechaOperativa(c.getFechaOperativa());
        r.setTurnoCaja(c.getTurnoCaja());
        r.setEstado(c.getEstado());
        r.setSaldoInicial(c.getSaldoInicial());
        r.setTotalIngresosPaciente(c.getTotalIngresosPaciente());
        r.setTotalIngresosOs(c.getTotalIngresosOs());
        r.setTotalEgresos(c.getTotalEgresos());
        r.setSaldoTeoricoCierre(c.getSaldoTeoricoCierre());
        r.setSaldoRealCierre(c.getSaldoRealCierre());
        r.setDiferenciaCierre(c.getDiferenciaCierre());
        r.setObservacionesApertura(c.getObservacionesApertura());
        r.setObservacionesCierre(c.getObservacionesCierre());
        r.setAbiertaPor(c.getAbiertaPor());
        r.setAbiertaEn(c.getAbiertaEn());
        r.setCerradaPor(c.getCerradaPor());
        r.setCerradaEn(c.getCerradaEn());
        r.setVersion(c.getVersion());
        return r;
    }

    private MovimientoCajaResponse toMovResponse(MovimientoCaja m) {
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
}
