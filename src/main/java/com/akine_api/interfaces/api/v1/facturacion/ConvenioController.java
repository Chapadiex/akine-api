package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.convenio.ActualizarArancelesRequest;
import com.akine_api.application.dto.convenio.ActualizarConvenioRequest;
import com.akine_api.application.dto.convenio.ConvenioDTO;
import com.akine_api.application.dto.convenio.ConvenioVersionDTO;
import com.akine_api.application.dto.convenio.NuevoArancelRequest;
import com.akine_api.application.dto.convenio.NuevoConvenioRequest;
import com.akine_api.application.dto.convenio.RenovarConvenioRequest;
import com.akine_api.application.service.convenio.ConvenioService;
import com.akine_api.application.service.convenio.PrestacionConvenioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facturacion/convenios")
@RequiredArgsConstructor
public class ConvenioController {

    private final ConvenioService service;
    private final PrestacionConvenioService prestacionService;

    @GetMapping("/consultorio/{consultorioId}")
    public ResponseEntity<List<ConvenioDTO>> byConsultorio(@PathVariable UUID consultorioId) {
        return ResponseEntity.ok(service.findByConsultorio(consultorioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvenioDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/versiones")
    public ResponseEntity<List<ConvenioVersionDTO>> versiones(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findVersiones(id));
    }

    @PostMapping
    public ResponseEntity<ConvenioDTO> create(
            @RequestParam UUID consultorioId,
            @RequestBody NuevoConvenioRequest req) {
        ConvenioDTO created = service.create(consultorioId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<ConvenioDTO> renovar(
            @PathVariable UUID id,
            @RequestBody RenovarConvenioRequest req) {
        return ResponseEntity.ok(service.renovar(id, req));
    }

    @PostMapping("/{id}/aranceles/bulk-update")
    public ResponseEntity<ConvenioDTO> actualizarAranceles(
            @PathVariable UUID id,
            @RequestBody ActualizarArancelesRequest req) {
        return ResponseEntity.ok(service.actualizarAranceles(id, req));
    }

    @PostMapping("/{id}/aranceles")
    public ResponseEntity<ConvenioDTO> agregarArancel(
            @PathVariable UUID id,
            @RequestBody NuevoArancelRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarArancel(id, req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConvenioDTO> updateConvenio(
            @PathVariable UUID id,
            @RequestBody ActualizarConvenioRequest req) {
        return ResponseEntity.ok(service.updateConvenio(id, req));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ConvenioDTO> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String estadoStr = body.get("estado");
        String motivo = body.get("motivo");
        return ResponseEntity.ok(service.cambiarEstadoVersion(id, estadoStr, motivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
