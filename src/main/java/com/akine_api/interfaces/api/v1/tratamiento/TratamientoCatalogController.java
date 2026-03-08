package com.akine_api.interfaces.api.v1.tratamiento;

import com.akine_api.application.dto.result.TratamientoCatalogResult;
import com.akine_api.application.service.ConsultorioTratamientoCatalogService;
import com.akine_api.interfaces.api.v1.tratamiento.dto.TratamientoCatalogResponse;
import com.akine_api.interfaces.api.v1.tratamiento.dto.TratamientoCatalogUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/tratamientos-catalogo")
public class TratamientoCatalogController {

    private final ConsultorioTratamientoCatalogService service;

    public TratamientoCatalogController(ConsultorioTratamientoCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<TratamientoCatalogResponse> get(@PathVariable UUID consultorioId,
                                                          @AuthenticationPrincipal UserDetails principal) {
        TratamientoCatalogResult result = service.get(consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toResponse(result));
    }

    @PutMapping
    public ResponseEntity<TratamientoCatalogResponse> upsert(@PathVariable UUID consultorioId,
                                                             @Valid @RequestBody TratamientoCatalogUpsertRequest request,
                                                             @AuthenticationPrincipal UserDetails principal) {
        TratamientoCatalogResult result = service.upsert(
                consultorioId,
                request.version(),
                request.items(),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    private TratamientoCatalogResponse toResponse(TratamientoCatalogResult result) {
        return new TratamientoCatalogResponse(
                result.consultorioId(),
                result.version(),
                result.items(),
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
