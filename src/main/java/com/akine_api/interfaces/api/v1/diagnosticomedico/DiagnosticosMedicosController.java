package com.akine_api.interfaces.api.v1.diagnosticomedico;

import com.akine_api.application.dto.result.DiagnosticosMedicosResult;
import com.akine_api.application.service.ConsultorioDiagnosticosMedicosService;
import com.akine_api.interfaces.api.v1.diagnosticomedico.dto.DiagnosticosMedicosResponse;
import com.akine_api.interfaces.api.v1.diagnosticomedico.dto.DiagnosticosMedicosUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/diagnosticos-medicos")
public class DiagnosticosMedicosController {

    private final ConsultorioDiagnosticosMedicosService service;

    public DiagnosticosMedicosController(ConsultorioDiagnosticosMedicosService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<DiagnosticosMedicosResponse> get(@PathVariable UUID consultorioId,
                                                           @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(service.get(consultorioId, principal.getUsername(), roles(principal))));
    }

    @PutMapping
    public ResponseEntity<DiagnosticosMedicosResponse> upsert(@PathVariable UUID consultorioId,
                                                              @Valid @RequestBody DiagnosticosMedicosUpsertRequest request,
                                                              @AuthenticationPrincipal UserDetails principal) {
        DiagnosticosMedicosResult result = service.upsert(
                consultorioId,
                request.version(),
                request.pais(),
                request.idioma(),
                request.tipos(),
                request.categorias(),
                request.diagnosticos(),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/defaults/restore")
    public ResponseEntity<DiagnosticosMedicosResponse> restoreDefaults(@PathVariable UUID consultorioId,
                                                                       @RequestParam(defaultValue = "ADD_MISSING") ConsultorioDiagnosticosMedicosService.DefaultsMode mode,
                                                                       @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(service.restoreDefaults(consultorioId, mode, principal.getUsername(), roles(principal))));
    }

    private DiagnosticosMedicosResponse toResponse(DiagnosticosMedicosResult result) {
        return new DiagnosticosMedicosResponse(
                result.consultorioId(),
                result.version(),
                result.pais(),
                result.idioma(),
                result.tipos(),
                result.categorias(),
                result.diagnosticos(),
                result.createdAt(),
                result.createdBy(),
                result.updatedAt(),
                result.updatedBy()
        );
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
    }
}
