package com.akine_api.interfaces.api.v1.feriado;

import com.akine_api.application.dto.command.CreateFeriadoCommand;
import com.akine_api.application.dto.result.ConsultorioFeriadoResult;
import com.akine_api.application.dto.result.FeriadoSyncResult;
import com.akine_api.application.service.ConsultorioFeriadoService;
import com.akine_api.interfaces.api.v1.feriado.dto.FeriadoRequest;
import com.akine_api.interfaces.api.v1.feriado.dto.FeriadoResponse;
import com.akine_api.interfaces.api.v1.feriado.dto.FeriadoSyncResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/feriados")
public class FeriadoController {

    private final ConsultorioFeriadoService service;

    public FeriadoController(ConsultorioFeriadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FeriadoResponse>> list(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails principal) {

        int yearValue = year != null ? year : LocalDate.now().getYear();
        List<FeriadoResponse> result = service
                .list(consultorioId, yearValue, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<FeriadoResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody FeriadoRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        ConsultorioFeriadoResult result = service.create(
                new CreateFeriadoCommand(consultorioId, req.fecha(), req.descripcion()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        service.delete(consultorioId, id, principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync-nacionales")
    public ResponseEntity<FeriadoSyncResponse> syncNacionales(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails principal) {

        int yearValue = year != null ? year : LocalDate.now().getYear();
        FeriadoSyncResult result = service.syncNacionales(
                consultorioId,
                yearValue,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toSyncResponse(result));
    }

    private FeriadoResponse toResponse(ConsultorioFeriadoResult r) {
        return new FeriadoResponse(r.id(), r.consultorioId(), r.fecha(), r.descripcion(), r.createdAt());
    }

    private FeriadoSyncResponse toSyncResponse(FeriadoSyncResult r) {
        return new FeriadoSyncResponse(r.year(), r.fetched(), r.created(), r.skippedExisting());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
