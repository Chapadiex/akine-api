package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.convenio.PrestacionDTO;
import com.akine_api.application.mapper.convenio.ConvenioDTOMapper;
import com.akine_api.application.service.convenio.PrestacionConvenioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/nomenclador")
@RequiredArgsConstructor
public class PrestacionNomencladorController {

    private final PrestacionConvenioService prestacionService;
    private final ConvenioDTOMapper mapper;

    @GetMapping
    public ResponseEntity<List<PrestacionDTO>> findAll() {
        List<PrestacionDTO> dtos = prestacionService.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
