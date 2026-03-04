package com.akine_api.interfaces.api.v1.colaborador;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.ColaboradorEmpleadoResult;
import com.akine_api.application.dto.result.ColaboradorProfesionalResult;
import com.akine_api.application.service.ColaboradorService;
import com.akine_api.interfaces.api.v1.colaborador.dto.*;
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
@RequestMapping("/api/v1/consultorios/{consultorioId}/colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;

    public ColaboradorController(ColaboradorService service) {
        this.service = service;
    }

    @GetMapping("/profesionales")
    public ResponseEntity<List<ColaboradorProfesionalResponse>> listProfesionales(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) List<String> especialidades,
            @RequestParam(required = false) Boolean activo,
            @AuthenticationPrincipal UserDetails principal) {
        List<ColaboradorProfesionalResponse> rows = service
                .listProfesionales(consultorioId, principal.getUsername(), roles(principal), q, matricula, especialidades, activo)
                .stream().map(this::toProfesionalResponse).toList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/profesionales/{id}")
    public ResponseEntity<ColaboradorProfesionalResponse> getProfesional(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        ColaboradorProfesionalResult result = service.getProfesional(consultorioId, id, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toProfesionalResponse(result));
    }

    @PostMapping("/profesionales")
    public ResponseEntity<ColaboradorProfesionalResponse> createProfesional(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody ProfesionalColaboradorRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CreateColaboradorProfesionalCommand cmd = new CreateColaboradorProfesionalCommand(
                consultorioId,
                req.modoAlta(),
                req.nombre(),
                req.apellido(),
                req.nroDocumento(),
                req.matricula(),
                req.especialidades(),
                req.email(),
                req.telefono(),
                req.domicilio(),
                req.fotoPerfilUrl()
        );
        ColaboradorProfesionalResult result = service.createProfesional(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.status(HttpStatus.CREATED).body(toProfesionalResponse(result));
    }

    @PutMapping("/profesionales/{id}")
    public ResponseEntity<ColaboradorProfesionalResponse> updateProfesional(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ProfesionalColaboradorRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateColaboradorProfesionalCommand cmd = new UpdateColaboradorProfesionalCommand(
                consultorioId,
                id,
                req.nombre(),
                req.apellido(),
                req.nroDocumento(),
                req.matricula(),
                req.especialidades(),
                req.email(),
                req.telefono(),
                req.domicilio(),
                req.fotoPerfilUrl()
        );
        ColaboradorProfesionalResult result = service.updateProfesional(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toProfesionalResponse(result));
    }

    @PatchMapping("/profesionales/{id}/estado")
    public ResponseEntity<ColaboradorProfesionalResponse> changeProfesionalEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ColaboradorEstadoRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        ChangeColaboradorEstadoCommand cmd = new ChangeColaboradorEstadoCommand(
                consultorioId,
                id,
                req.activo(),
                req.fechaDeBaja(),
                req.motivoDeBaja()
        );
        ColaboradorProfesionalResult result = service.changeProfesionalEstado(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toProfesionalResponse(result));
    }

    @PostMapping("/profesionales/{id}/crear-cuenta")
    public ResponseEntity<ColaboradorProfesionalResponse> crearCuentaProfesional(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @RequestBody(required = false) CrearCuentaProfesionalRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        String email = req != null ? req.email() : null;
        ColaboradorProfesionalResult result = service.crearCuentaProfesional(
                consultorioId,
                id,
                email,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toProfesionalResponse(result));
    }

    @PostMapping("/profesionales/{id}/reenviar-activacion")
    public ResponseEntity<ColaboradorProfesionalResponse> reenviarActivacionProfesional(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        ColaboradorProfesionalResult result = service.reenviarActivacionProfesional(
                consultorioId,
                id,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toProfesionalResponse(result));
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<ColaboradorEmpleadoResponse>> listEmpleados(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) Boolean activo,
            @AuthenticationPrincipal UserDetails principal) {
        List<ColaboradorEmpleadoResponse> rows = service
                .listEmpleados(consultorioId, principal.getUsername(), roles(principal), q, cargo, activo)
                .stream().map(this::toEmpleadoResponse).toList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/empleados/{id}")
    public ResponseEntity<ColaboradorEmpleadoResponse> getEmpleado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        ColaboradorEmpleadoResult result = service.getEmpleado(consultorioId, id, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toEmpleadoResponse(result));
    }

    @PostMapping("/empleados")
    public ResponseEntity<ColaboradorEmpleadoResponse> createEmpleado(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody EmpleadoColaboradorRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CreateEmpleadoCommand cmd = new CreateEmpleadoCommand(
                consultorioId,
                req.nombre(),
                req.apellido(),
                req.dni(),
                req.cargo(),
                req.nroLegajo(),
                req.email(),
                req.telefono(),
                req.notasInternas()
        );
        ColaboradorEmpleadoResult result = service.createEmpleado(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.status(HttpStatus.CREATED).body(toEmpleadoResponse(result));
    }

    @PutMapping("/empleados/{id}")
    public ResponseEntity<ColaboradorEmpleadoResponse> updateEmpleado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody EmpleadoColaboradorRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateEmpleadoCommand cmd = new UpdateEmpleadoCommand(
                consultorioId,
                id,
                req.nombre(),
                req.apellido(),
                req.dni(),
                req.cargo(),
                req.nroLegajo(),
                req.email(),
                req.telefono(),
                req.notasInternas()
        );
        ColaboradorEmpleadoResult result = service.updateEmpleado(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toEmpleadoResponse(result));
    }

    @PatchMapping("/empleados/{id}/estado")
    public ResponseEntity<ColaboradorEmpleadoResponse> changeEmpleadoEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ColaboradorEstadoRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        ChangeColaboradorEstadoCommand cmd = new ChangeColaboradorEstadoCommand(
                consultorioId,
                id,
                req.activo(),
                req.fechaDeBaja(),
                req.motivoDeBaja()
        );
        ColaboradorEmpleadoResult result = service.changeEmpleadoEstado(cmd, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toEmpleadoResponse(result));
    }

    @PostMapping("/empleados/{id}/reenviar-activacion")
    public ResponseEntity<ColaboradorEmpleadoResponse> reenviarActivacionEmpleado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        ColaboradorEmpleadoResult result = service.reenviarActivacionEmpleado(
                consultorioId,
                id,
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toEmpleadoResponse(result));
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }

    private ColaboradorProfesionalResponse toProfesionalResponse(ColaboradorProfesionalResult r) {
        return new ColaboradorProfesionalResponse(
                r.id(),
                r.consultorioId(),
                r.userId(),
                r.nombre(),
                r.apellido(),
                r.nroDocumento(),
                r.matricula(),
                r.especialidades(),
                r.email(),
                r.telefono(),
                r.domicilio(),
                r.fotoPerfilUrl(),
                r.fechaAlta(),
                r.fechaBaja(),
                r.motivoBaja(),
                r.activo(),
                r.estadoColaborador(),
                r.cuentaStatus(),
                r.ultimoEnvioActivacionAt(),
                r.createdAt(),
                r.updatedAt()
        );
    }

    private ColaboradorEmpleadoResponse toEmpleadoResponse(ColaboradorEmpleadoResult r) {
        return new ColaboradorEmpleadoResponse(
                r.id(),
                r.consultorioId(),
                r.userId(),
                r.nombre(),
                r.apellido(),
                r.dni(),
                r.cargo(),
                r.nroLegajo(),
                r.email(),
                r.telefono(),
                r.notasInternas(),
                r.fechaAlta(),
                r.fechaBaja(),
                r.motivoBaja(),
                r.activo(),
                r.estadoColaborador(),
                r.cuentaStatus(),
                r.ultimoEnvioActivacionAt(),
                r.createdAt(),
                r.updatedAt()
        );
    }
}
