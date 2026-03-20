package com.akine_api.interfaces.api.v1.cobertura;

import com.akine_api.application.dto.cobertura.PlanFinanciadorDTO;
import com.akine_api.application.mapper.cobertura.PlanFinanciadorDTOMapper;
import com.akine_api.application.service.cobertura.PlanFinanciadorService;
import com.akine_api.domain.model.cobertura.PlanFinanciador;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cobertura/planes")
@RequiredArgsConstructor
public class PlanFinanciadorController {

    private final PlanFinanciadorService service;
    private final PlanFinanciadorDTOMapper mapper;

    @PostMapping
    public ResponseEntity<PlanFinanciadorDTO> create(@RequestBody PlanFinanciadorDTO dto) {
        PlanFinanciador domain = mapper.toDomain(dto);
        PlanFinanciador saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping("/financiador/{financiadorId}")
    public ResponseEntity<List<PlanFinanciadorDTO>> findByFinanciadorId(@PathVariable UUID financiadorId) {
        List<PlanFinanciadorDTO> dtos = service.findByFinanciadorId(financiadorId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanFinanciadorDTO> update(@PathVariable UUID id, @RequestBody PlanFinanciadorDTO dto) {
        PlanFinanciador domain = mapper.toDomain(dto);
        PlanFinanciador updated = service.update(id, domain);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanFinanciadorDTO> findById(@PathVariable UUID id) {
        PlanFinanciador domain = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(domain));
    }
}
