package com.akine_api.interfaces.api.v1.cobro;

import com.akine_api.application.service.cobro.LiquidacionSesionService;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.interfaces.api.v1.cobro.dto.ConvertirParticularRequest;
import com.akine_api.interfaces.api.v1.cobro.dto.LiquidacionSesionResponse;
import com.akine_api.interfaces.api.v1.cobro.dto.ReliquidarRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionSesionController {

    private final LiquidacionSesionService liquidacionService;

    @GetMapping("/{liquidacionId}")
    public ResponseEntity<LiquidacionSesionResponse> findById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID liquidacionId) {
        return ResponseEntity.ok(toResponse(liquidacionService.findById(liquidacionId)));
    }

    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<LiquidacionSesionResponse> findBySesionId(
            @PathVariable UUID consultorioId,
            @PathVariable UUID sesionId) {
        return liquidacionService.findBySesionId(sesionId)
                .map(l -> ResponseEntity.ok(toResponse(l)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionSesionResponse>> findByConsultorio(
            @PathVariable UUID consultorioId) {
        List<LiquidacionSesionResponse> list = liquidacionService.findByConsultorioId(consultorioId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<LiquidacionSesionResponse>> findByPaciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId) {
        List<LiquidacionSesionResponse> list = liquidacionService.findByPaciente(consultorioId, pacienteId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{liquidacionId}/reliquidar")
    public ResponseEntity<LiquidacionSesionResponse> reliquidar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID liquidacionId,
            @Valid @RequestBody ReliquidarRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        LiquidacionSesion result = liquidacionService.reliquidar(
                liquidacionId, req.getMotivo(), principal.getUsername());
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/{liquidacionId}/convertir-particular")
    public ResponseEntity<LiquidacionSesionResponse> convertirParticular(
            @PathVariable UUID consultorioId,
            @PathVariable UUID liquidacionId,
            @Valid @RequestBody ConvertirParticularRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        LiquidacionSesion result = liquidacionService.convertirAParticular(
                liquidacionId, req.getMotivo(), principal.getUsername());
        return ResponseEntity.ok(toResponse(result));
    }

    // ─── mapping ─────────────────────────────────────────────────────────────

    private LiquidacionSesionResponse toResponse(LiquidacionSesion l) {
        LiquidacionSesionResponse r = new LiquidacionSesionResponse();
        r.setId(l.getId());
        r.setConsultorioId(l.getConsultorioId());
        r.setSesionId(l.getSesionId());
        r.setPacienteId(l.getPacienteId());
        r.setFinanciadorId(l.getFinanciadorId());
        r.setPlanId(l.getPlanId());
        r.setConvenioId(l.getConvenioId());
        r.setTipoLiquidacion(l.getTipoLiquidacion());
        r.setEstado(l.getEstado());
        r.setMotivoBloqueo(l.getMotivoBloqueo());
        r.setValorBruto(l.getValorBruto());
        r.setDescuentoImporte(l.getDescuentoImporte());
        r.setCopagoImporte(l.getCopagoImporte());
        r.setCoseguroImporte(l.getCoseguroImporte());
        r.setImportePaciente(l.getImportePaciente());
        r.setImporteObraSocial(l.getImporteObraSocial());
        r.setImporteTotalLiquidado(l.getImporteTotalLiquidado());
        r.setDocumentacionCompleta(l.isDocumentacionCompleta());
        r.setDocumentacionObs(l.getDocumentacionObs());
        r.setEsFacturableOs(l.isEsFacturableOs());
        r.setRequiereRevisionManual(l.isRequiereRevisionManual());
        r.setOrigenTipoCobro(l.getOrigenTipoCobro());
        r.setRecalculadaEn(l.getRecalculadaEn());
        r.setRecalculadaPor(l.getRecalculadaPor());
        r.setObservaciones(l.getObservaciones());
        r.setLiquidadoPor(l.getLiquidadoPor());
        r.setCreatedAt(l.getCreatedAt());
        r.setUpdatedAt(l.getUpdatedAt());
        r.setVersion(l.getVersion());
        return r;
    }
}
