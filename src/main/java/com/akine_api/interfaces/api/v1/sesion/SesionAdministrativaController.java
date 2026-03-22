package com.akine_api.interfaces.api.v1.sesion;

import com.akine_api.application.service.sesion.SesionAdministrativaService;
import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.interfaces.api.v1.sesion.dto.SesionAdministrativaRequest;
import com.akine_api.interfaces.api.v1.sesion.dto.SesionAdministrativaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/sesiones-administrativas")
@RequiredArgsConstructor
public class SesionAdministrativaController {

    private final SesionAdministrativaService service;

    @PostMapping
    public ResponseEntity<SesionAdministrativaResponse> registrar(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody SesionAdministrativaRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SesionAdministrativa saved = service.registrar(
                req.getSesionId(),
                req.getTurnoId(),
                consultorioId,
                req.getPacienteId(),
                req.getCoberturaTipo(),
                req.getFinanciadorId(),
                req.getPlanId(),
                req.getNumeroAfiliado(),
                req.getTienePedidoMedico(),
                req.getTieneOrden(),
                req.getTieneAutorizacion(),
                req.getNumeroAutorizacion(),
                principal.getUsername());
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<SesionAdministrativaResponse> findBySesionId(
            @PathVariable UUID consultorioId,
            @PathVariable UUID sesionId) {
        return service.findBySesionId(sesionId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<SesionAdministrativaResponse> findByTurnoId(
            @PathVariable UUID consultorioId,
            @PathVariable UUID turnoId) {
        return service.findByTurnoId(turnoId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sesion/{sesionId}/confirmar-asistencia")
    public ResponseEntity<SesionAdministrativaResponse> confirmarAsistencia(
            @PathVariable UUID consultorioId,
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal UserDetails principal) {
        SesionAdministrativa saved = service.confirmarAsistencia(sesionId, principal.getUsername());
        return ResponseEntity.ok(toResponse(saved));
    }

    private SesionAdministrativaResponse toResponse(SesionAdministrativa s) {
        SesionAdministrativaResponse r = new SesionAdministrativaResponse();
        r.setId(s.getId());
        r.setSesionId(s.getSesionId());
        r.setTurnoId(s.getTurnoId());
        r.setConsultorioId(s.getConsultorioId());
        r.setPacienteId(s.getPacienteId());
        r.setCoberturaTipo(s.getCoberturaTipo());
        r.setFinanciadorId(s.getFinanciadorId());
        r.setPlanId(s.getPlanId());
        r.setNumeroAfiliado(s.getNumeroAfiliado());
        r.setTienePedidoMedico(s.getTienePedidoMedico());
        r.setTieneOrden(s.getTieneOrden());
        r.setTieneAutorizacion(s.getTieneAutorizacion());
        r.setNumeroAutorizacion(s.getNumeroAutorizacion());
        r.setAsistenciaConfirmada(s.getAsistenciaConfirmada());
        r.setDocumentacionCompleta(s.getDocumentacionCompleta());
        r.setDocumentacionFaltante(s.getDocumentacionFaltante());
        r.setValidacionCoberturaEstado(s.getValidacionCoberturaEstado());
        r.setEsFacturableOs(s.getEsFacturableOs());
        r.setRegistradoPor(s.getRegistradoPor());
        r.setRegistradoEn(s.getRegistradoEn());
        r.setVersion(s.getVersion());
        return r;
    }
}
