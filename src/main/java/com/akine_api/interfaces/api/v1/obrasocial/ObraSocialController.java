package com.akine_api.interfaces.api.v1.obrasocial;

import com.akine_api.application.dto.command.ChangeObraSocialEstadoCommand;
import com.akine_api.application.dto.command.PlanCommand;
import com.akine_api.application.dto.command.UpsertObraSocialCommand;
import com.akine_api.application.dto.result.*;
import com.akine_api.application.service.ObraSocialService;
import com.akine_api.domain.model.ObraSocialEstado;
import com.akine_api.interfaces.api.v1.obrasocial.dto.*;
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
@RequestMapping("/api/v1/consultorios/{consultorioId}/obras-sociales")
public class ObraSocialController {

    private final ObraSocialService service;

    public ObraSocialController(ObraSocialService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedObraSocialListResponse> list(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ObraSocialEstado estado,
            @RequestParam(required = false) Boolean conPlanes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {

        PagedResult<ObraSocialListItemResult> result = service.list(
                consultorioId, q, estado, conPlanes, page, size, principal.getUsername(), roles(principal));

        List<ObraSocialListItemResponse> content = result.content().stream().map(this::toListResponse).toList();
        return ResponseEntity.ok(new PagedObraSocialListResponse(content, result.page(), result.size(), result.total()));
    }

    @GetMapping("/{obraSocialId}")
    public ResponseEntity<ObraSocialDetailResponse> getById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID obraSocialId,
            @AuthenticationPrincipal UserDetails principal) {
        ObraSocialDetailResult result = service.getById(consultorioId, obraSocialId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toDetailResponse(result));
    }

    @PostMapping
    public ResponseEntity<ObraSocialDetailResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody ObraSocialUpsertRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        UpsertObraSocialCommand cmd = toUpsertCommand(request, consultorioId, null);
        ObraSocialDetailResult result = service.create(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetailResponse(result));
    }

    @PutMapping("/{obraSocialId}")
    public ResponseEntity<ObraSocialDetailResponse> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID obraSocialId,
            @Valid @RequestBody ObraSocialUpsertRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        UpsertObraSocialCommand cmd = toUpsertCommand(request, consultorioId, obraSocialId);
        ObraSocialDetailResult result = service.update(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toDetailResponse(result));
    }

    @PatchMapping("/{obraSocialId}/estado")
    public ResponseEntity<ObraSocialDetailResponse> changeEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID obraSocialId,
            @Valid @RequestBody ChangeEstadoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        ObraSocialDetailResult result = service.changeEstado(
                new ChangeObraSocialEstadoCommand(consultorioId, obraSocialId, request.estado()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toDetailResponse(result));
    }

    private UpsertObraSocialCommand toUpsertCommand(ObraSocialUpsertRequest request,
                                                    UUID consultorioId,
                                                    UUID obraSocialId) {
        List<PlanCommand> planes = request.planes().stream().map(p -> new PlanCommand(
                p.id(),
                p.nombreCorto(),
                p.nombreCompleto(),
                p.tipoCobertura(),
                p.valorCobertura(),
                p.tipoCoseguro(),
                p.valorCoseguro(),
                p.prestacionesSinAutorizacion(),
                p.observaciones(),
                p.activo()
        )).toList();

        return new UpsertObraSocialCommand(
                obraSocialId,
                consultorioId,
                request.acronimo(),
                request.nombreCompleto(),
                request.cuit(),
                request.email(),
                request.telefono(),
                request.telefonoAlternativo(),
                request.representante(),
                request.observacionesInternas(),
                request.direccionLinea(),
                request.estado(),
                planes
        );
    }

    private ObraSocialListItemResponse toListResponse(ObraSocialListItemResult r) {
        return new ObraSocialListItemResponse(
                r.id(),
                r.acronimo(),
                r.nombreCompleto(),
                r.cuit(),
                r.email(),
                r.telefono(),
                r.representante(),
                r.estado(),
                r.planesCount(),
                r.hasPlanes(),
                r.createdAt(),
                r.updatedAt()
        );
    }

    private ObraSocialDetailResponse toDetailResponse(ObraSocialDetailResult r) {
        List<PlanResponse> plans = r.planes().stream().map(p -> new PlanResponse(
                p.id(),
                p.nombreCorto(),
                p.nombreCompleto(),
                p.tipoCobertura(),
                p.valorCobertura(),
                p.tipoCoseguro(),
                p.valorCoseguro(),
                p.prestacionesSinAutorizacion(),
                p.observaciones(),
                p.activo(),
                p.createdAt(),
                p.updatedAt()
        )).toList();

        return new ObraSocialDetailResponse(
                r.id(),
                r.consultorioId(),
                r.acronimo(),
                r.nombreCompleto(),
                r.cuit(),
                r.email(),
                r.telefono(),
                r.telefonoAlternativo(),
                r.representante(),
                r.observacionesInternas(),
                r.direccionLinea(),
                r.estado(),
                plans,
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

