package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.AtencionFacturableDTO;
import com.akine_api.application.mapper.facturacion.AtencionFacturableDTOMapper;
import com.akine_api.application.service.facturacion.AtencionFacturableService;
import com.akine_api.application.service.facturacion.GenerarAtencionFacturableService;
import com.akine_api.domain.model.facturacion.AtencionFacturable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/atenciones")
@RequiredArgsConstructor
public class AtencionFacturableController {

    private final GenerarAtencionFacturableService generarService;
    private final AtencionFacturableService atencionFacturableService;
    private final AtencionFacturableDTOMapper mapper;

    @PostMapping("/generar-snapshot")
    public ResponseEntity<AtencionFacturableDTO> generarSnapshot(
            @RequestParam UUID atencionId,
            @RequestParam UUID pacienteId,
            @RequestParam UUID prestacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaAtencion) {
        
        AtencionFacturable domain = generarService.generarSnapshot(atencionId, pacienteId, prestacionId, fechaAtencion);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(domain));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AtencionFacturableDTO>> findByPacienteId(@PathVariable UUID pacienteId) {
        List<AtencionFacturableDTO> dtos = atencionFacturableService.findByPacienteId(pacienteId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
