package com.akine_api.interfaces.api.v1.casoatencion;

import com.akine_api.application.dto.command.CambiarEstadoCasoAtencionCommand;
import com.akine_api.application.dto.command.CreateCasoAtencionCommand;
import com.akine_api.application.dto.command.UpdateCasoAtencionCommand;
import com.akine_api.application.dto.result.CasoAtencionResult;
import com.akine_api.application.dto.result.CasoAtencionSummaryResult;
import com.akine_api.application.service.CasoAtencionService;
import com.akine_api.interfaces.api.v1.casoatencion.dto.CambiarEstadoCasoAtencionRequest;
import com.akine_api.interfaces.api.v1.casoatencion.dto.CreateCasoAtencionRequest;
import com.akine_api.interfaces.api.v1.casoatencion.dto.UpdateCasoAtencionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class CasoAtencionController {

    private final CasoAtencionService service;

    public CasoAtencionController(CasoAtencionService service) {
        this.service = service;
    }

    // POST /api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos
    @PostMapping("/api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos")
    public ResponseEntity<CasoAtencionResult> create(
            @PathVariable UUID consultorioId,
            @PathVariable UUID legajoId,
            @Valid @RequestBody CreateCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.createCasoAtencion(
                consultorioId,
                new CreateCasoAtencionCommand(
                        legajoId,
                        req.pacienteId(),
                        req.profesionalResponsableId(),
                        req.tipoOrigen() != null ? req.tipoOrigen() : "CONSULTA_DIRECTA",
                        req.fechaApertura() != null ? req.fechaApertura() : LocalDateTime.now(),
                        req.motivoConsulta(),
                        req.diagnosticoMedico(),
                        req.diagnosticoFuncional(),
                        req.afeccionPrincipal(),
                        req.coberturaId(),
                        req.prioridad() != null ? req.prioridad() : "NORMAL"
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos
    @GetMapping("/api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> listByLegajo(
            @PathVariable UUID consultorioId,
            @PathVariable UUID legajoId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosPorLegajo(
                legajoId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/casos-atencion/{id}
    @GetMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}")
    public ResponseEntity<CasoAtencionResult> getById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.getCasoAtencion(
                id, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // PUT /api/v1/consultorios/{consultorioId}/casos-atencion/{id}
    @PutMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}")
    public ResponseEntity<CasoAtencionResult> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.updateCasoAtencion(
                id,
                consultorioId,
                new UpdateCasoAtencionCommand(
                        req.profesionalResponsableId(),
                        req.motivoConsulta(),
                        req.diagnosticoMedico(),
                        req.diagnosticoFuncional(),
                        req.afeccionPrincipal(),
                        req.prioridad()
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(result);
    }

    // PATCH /api/v1/consultorios/{consultorioId}/casos-atencion/{id}/estado
    @PatchMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}/estado")
    public ResponseEntity<CasoAtencionResult> cambiarEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.cambiarEstado(
                id,
                consultorioId,
                new CambiarEstadoCasoAtencionCommand(req.nuevoEstado()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos-activos
    @GetMapping("/api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos-activos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> casosActivosPorPaciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosActivosPorPaciente(
                pacienteId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos
    @GetMapping("/api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> casosPorPaciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosPorPaciente(
                pacienteId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
