package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.ConciliacionAtencionDTO;
import com.akine_api.application.service.facturacion.ConciliacionService;
import com.akine_api.domain.model.facturacion.ConciliacionAtencion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/conciliacion")
@RequiredArgsConstructor
public class ConciliacionController {

    private final ConciliacionService service;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConciliacionAtencionDTO>> getInformePaciente(@PathVariable UUID pacienteId) {
        List<ConciliacionAtencion> informe = service.generarInformePaciente(pacienteId);
        
        List<ConciliacionAtencionDTO> dtos = informe.stream().map(domain -> {
            ConciliacionAtencionDTO dto = new ConciliacionAtencionDTO();
            dto.setAtencionId(domain.getAtencionId());
            dto.setPacienteNombre(domain.getPacienteNombre());
            dto.setImporteSnapshot(domain.getImporteSnapshot());
            dto.setImportePresentado(domain.getImportePresentado());
            dto.setImporteLiquidado(domain.getImporteLiquidado());
            dto.setImportePagado(domain.getImportePagado());
            dto.setDiferencia(domain.getDiferencia());
            dto.setEstadoFinal(domain.getEstadoFinal());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
