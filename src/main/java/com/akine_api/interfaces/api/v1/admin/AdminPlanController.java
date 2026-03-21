package com.akine_api.interfaces.api.v1.admin;

import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.interfaces.api.v1.admin.dto.PlanDefinicionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/admin/planes")
public class AdminPlanController {

    private final PlanDefinicionRepositoryPort planRepo;

    public AdminPlanController(PlanDefinicionRepositoryPort planRepo) {
        this.planRepo = planRepo;
    }

    @GetMapping
    public ResponseEntity<List<PlanDefinicionResponse>> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("Solo ADMIN puede consultar planes");
        }
        List<PlanDefinicionResponse> result = planRepo.findAllActivos().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    private PlanDefinicionResponse toResponse(PlanDefinicion p) {
        return new PlanDefinicionResponse(
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecioMensual(),
                p.getPrecioAnual(),
                p.getMaxConsultorios(),
                p.getMaxProfesionales(),
                p.getMaxPacientes(),
                p.isModuloFacturacion(),
                p.isModuloHistoriaClinica(),
                p.isModuloObrasSociales(),
                p.isModuloColaboradores(),
                p.getOrden()
        );
    }
}
