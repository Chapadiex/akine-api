package com.akine_api.interfaces.api.v1.configuracion;

import com.akine_api.application.dto.ConfiguracionConsultorioDTO;
import com.akine_api.application.mapper.ConfiguracionConsultorioDTOMapper;
import com.akine_api.application.service.ConfiguracionConsultorioService;
import com.akine_api.domain.model.ConfiguracionConsultorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuracion/consultorio")
@RequiredArgsConstructor
public class ConfiguracionConsultorioController {

    private final ConfiguracionConsultorioService service;
    private final ConfiguracionConsultorioDTOMapper mapper;

    @GetMapping("/{consultorioId}")
    public ResponseEntity<ConfiguracionConsultorioDTO> findByConsultorioId(@PathVariable UUID consultorioId) {
        ConfiguracionConsultorio config = service.findByConsultorioId(consultorioId);
        return ResponseEntity.ok(mapper.toDto(config));
    }

    @PutMapping("/{consultorioId}")
    public ResponseEntity<ConfiguracionConsultorioDTO> upsert(
            @PathVariable UUID consultorioId,
            @RequestBody ConfiguracionConsultorioDTO dto) {
        ConfiguracionConsultorio config = mapper.toDomain(dto);
        ConfiguracionConsultorio saved = service.upsert(consultorioId, config);
        return ResponseEntity.ok(mapper.toDto(saved));
    }
}
