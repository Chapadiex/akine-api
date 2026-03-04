package com.akine_api.interfaces.api.v1.especialidad;

import com.akine_api.application.dto.result.EspecialidadResult;
import com.akine_api.application.service.ConsultorioEspecialidadService;
import com.akine_api.interfaces.api.v1.especialidad.dto.EspecialidadCreateRequest;
import com.akine_api.interfaces.api.v1.especialidad.dto.EspecialidadResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/especialidades")
public class EspecialidadController {

    private final ConsultorioEspecialidadService service;

    public EspecialidadController(ConsultorioEspecialidadService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadResponse>> list(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @AuthenticationPrincipal UserDetails principal
    ) {
        List<EspecialidadResponse> response = service
                .list(consultorioId, search, includeInactive, principal.getUsername(), roles(principal))
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EspecialidadResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody EspecialidadCreateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        EspecialidadResult created = service.createOrLink(
                consultorioId,
                request.nombre(),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PatchMapping("/{especialidadId}/activar")
    public ResponseEntity<EspecialidadResponse> activate(
            @PathVariable UUID consultorioId,
            @PathVariable UUID especialidadId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        EspecialidadResult updated = service.activate(
                consultorioId,
                especialidadId,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @PatchMapping("/{especialidadId}/desactivar")
    public ResponseEntity<EspecialidadResponse> deactivate(
            @PathVariable UUID consultorioId,
            @PathVariable UUID especialidadId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        EspecialidadResult updated = service.deactivate(
                consultorioId,
                especialidadId,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    private EspecialidadResponse toResponse(EspecialidadResult r) {
        return new EspecialidadResponse(
                r.id(),
                r.consultorioId(),
                r.nombre(),
                r.slug(),
                r.activo(),
                r.createdAt(),
                r.updatedAt()
        );
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
