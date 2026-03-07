package com.akine_api.interfaces.api.v1.paciente360;

import com.akine_api.application.dto.result.Paciente360AtencionesResult;
import com.akine_api.application.dto.result.Paciente360DiagnosticosResult;
import com.akine_api.application.dto.result.Paciente360HeaderResult;
import com.akine_api.application.dto.result.Paciente360HistoriaClinicaResult;
import com.akine_api.application.dto.result.Paciente360ObraSocialResult;
import com.akine_api.application.dto.result.Paciente360PagosResult;
import com.akine_api.application.dto.result.Paciente360SummaryResult;
import com.akine_api.application.dto.result.Paciente360TurnosResult;
import com.akine_api.application.service.Paciente360Service;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/360")
public class Paciente360Controller {

    private final Paciente360Service service;

    public Paciente360Controller(Paciente360Service service) {
        this.service = service;
    }

    @GetMapping("/header")
    public ResponseEntity<Paciente360HeaderResult> header(@PathVariable UUID consultorioId,
                                                          @PathVariable UUID pacienteId,
                                                          @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getHeader(consultorioId, pacienteId, principal.getUsername(), roles(principal)));
    }

    @GetMapping("/resumen")
    public ResponseEntity<Paciente360SummaryResult> resumen(@PathVariable UUID consultorioId,
                                                            @PathVariable UUID pacienteId,
                                                            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getSummary(consultorioId, pacienteId, principal.getUsername(), roles(principal)));
    }

    @GetMapping("/historia-clinica")
    public ResponseEntity<Paciente360HistoriaClinicaResult> historiaClinica(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getHistoriaClinica(
                consultorioId, pacienteId, tipo, profesionalId, from, to, page, size,
                principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/diagnosticos")
    public ResponseEntity<Paciente360DiagnosticosResult> diagnosticos(@PathVariable UUID consultorioId,
                                                                      @PathVariable UUID pacienteId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size,
                                                                      @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getDiagnosticos(
                consultorioId, pacienteId, page, size, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/atenciones")
    public ResponseEntity<Paciente360AtencionesResult> atenciones(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getAtenciones(
                consultorioId, pacienteId, profesionalId, from, to, page, size,
                principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/turnos")
    public ResponseEntity<Paciente360TurnosResult> turnos(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @RequestParam(defaultValue = "PROXIMOS") String scope,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getTurnos(
                consultorioId, pacienteId, scope, profesionalId, estado, from, to, page, size,
                principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/obra-social")
    public ResponseEntity<Paciente360ObraSocialResult> obraSocial(@PathVariable UUID consultorioId,
                                                                  @PathVariable UUID pacienteId,
                                                                  @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getObraSocial(
                consultorioId, pacienteId, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pagos")
    public ResponseEntity<Paciente360PagosResult> pagos(@PathVariable UUID consultorioId,
                                                        @PathVariable UUID pacienteId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getPagos(
                consultorioId, pacienteId, page, size, principal.getUsername(), roles(principal)
        ));
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
