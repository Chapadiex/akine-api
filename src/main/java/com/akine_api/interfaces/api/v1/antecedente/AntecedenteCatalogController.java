package com.akine_api.interfaces.api.v1.antecedente;

import com.akine_api.application.dto.result.AntecedenteCatalogResult;
import com.akine_api.application.service.ConsultorioAntecedenteCatalogService;
import com.akine_api.interfaces.api.v1.antecedente.dto.AntecedenteCatalogResponse;
import com.akine_api.interfaces.api.v1.antecedente.dto.AntecedenteCatalogUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/antecedentes-catalogo")
public class AntecedenteCatalogController {

    private final ConsultorioAntecedenteCatalogService service;

    public AntecedenteCatalogController(ConsultorioAntecedenteCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<AntecedenteCatalogResponse> get(
            @PathVariable UUID consultorioId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        AntecedenteCatalogResult result = service.get(
                consultorioId,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PutMapping
    public ResponseEntity<AntecedenteCatalogResponse> upsert(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody AntecedenteCatalogUpsertRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        AntecedenteCatalogResult result = service.upsert(
                consultorioId,
                request.version(),
                request.categories(),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/defaults/restore")
    public ResponseEntity<AntecedenteCatalogResponse> restoreDefaults(
            @PathVariable UUID consultorioId,
            @RequestParam(defaultValue = "ADD_MISSING") ConsultorioAntecedenteCatalogService.DefaultsMode mode,
            @AuthenticationPrincipal UserDetails principal
    ) {
        AntecedenteCatalogResult result = service.restoreDefaults(
                consultorioId,
                mode,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    private AntecedenteCatalogResponse toResponse(AntecedenteCatalogResult r) {
        return new AntecedenteCatalogResponse(
                r.consultorioId(),
                r.version(),
                r.categories(),
                r.createdAt(),
                r.createdBy(),
                r.updatedAt(),
                r.updatedBy()
        );
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
