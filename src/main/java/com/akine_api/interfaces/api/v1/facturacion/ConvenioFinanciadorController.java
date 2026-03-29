package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.ConvenioFinanciadorDTO;
import com.akine_api.application.dto.facturacion.EstadoConvenioRequest;
import com.akine_api.application.mapper.facturacion.ConvenioFinanciadorDTOMapper;
import com.akine_api.application.service.facturacion.ConvenioFinanciadorService;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/convenios/legacy")
@RequiredArgsConstructor
public class ConvenioFinanciadorController {

    private final ConvenioFinanciadorService service;
    private final ConvenioFinanciadorDTOMapper mapper;

    @PostMapping
    public ResponseEntity<ConvenioFinanciadorDTO> create(@RequestBody ConvenioFinanciadorDTO dto) {
        ConvenioFinanciador domain = mapper.toDomain(dto);
        ConvenioFinanciador saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping("/financiador/{financiadorId}")
    public ResponseEntity<List<ConvenioFinanciadorDTO>> findByFinanciadorId(@PathVariable UUID financiadorId) {
        List<ConvenioFinanciadorDTO> dtos = service.findByFinanciadorId(financiadorId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/consultorio/{consultorioId}")
    public ResponseEntity<List<ConvenioFinanciadorDTO>> findByConsultorioId(@PathVariable UUID consultorioId) {
        List<ConvenioFinanciadorDTO> dtos = service.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvenioFinanciadorDTO> findById(@PathVariable UUID id) {
        ConvenioFinanciador domain = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(domain));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConvenioFinanciadorDTO> update(
            @PathVariable UUID id,
            @RequestBody ConvenioFinanciadorDTO dto) {
        ConvenioFinanciador domain = mapper.toDomain(dto);
        ConvenioFinanciador updated = service.update(id, domain);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ConvenioFinanciadorDTO> updateEstado(
            @PathVariable UUID id,
            @RequestBody EstadoConvenioRequest request) {
        ConvenioFinanciador existing = service.findById(id);
        existing.setActivo(request.getActivo());
        ConvenioFinanciador updated = service.update(id, existing);
        return ResponseEntity.ok(mapper.toDto(updated));
    }
}
