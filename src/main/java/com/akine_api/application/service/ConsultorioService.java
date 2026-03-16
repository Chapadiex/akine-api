package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioInactiveException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioService {

    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService;
    private final ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService;
    private final CargoEmpleadoCatalogoBootstrapService cargoEmpleadoCatalogoBootstrapService;

    public ConsultorioService(ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo,
                              ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService,
                              ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService,
                              CargoEmpleadoCatalogoBootstrapService cargoEmpleadoCatalogoBootstrapService) {
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.consultorioEspecialidadBootstrapService = consultorioEspecialidadBootstrapService;
        this.consultorioAntecedenteBootstrapService = consultorioAntecedenteBootstrapService;
        this.cargoEmpleadoCatalogoBootstrapService = cargoEmpleadoCatalogoBootstrapService;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioResult> list(String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return consultorioRepo.findAll().stream().map(this::toResult).toList();
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        return consultorioRepo.findByIds(ids).stream()
                .filter(Consultorio::isActive)
                .map(this::toResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultorioResult getById(UUID id, String userEmail, Set<String> roles) {
        Consultorio consultorio = findOrThrow(id);
        if (roles.contains("ROLE_ADMIN")) {
            return toResult(consultorio);
        }
        assertMember(id, resolveUserId(userEmail));
        assertActive(consultorio);
        return toResult(consultorio);
    }

    public ConsultorioResult create(CreateConsultorioCommand cmd, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede crear consultorios");
        }
        String status = normalizeStatus(cmd.status(), "ACTIVE");
        Consultorio consultorio = new Consultorio(
                UUID.randomUUID(),
                cmd.name(),
                cmd.description(),
                cmd.logoUrl(),
                cmd.cuit(),
                cmd.legalName(),
                cmd.address(),
                cmd.geoAddress(),
                cmd.accessReference(),
                cmd.floorUnit(),
                cmd.phone(),
                cmd.email(),
                cmd.administrativeContact(),
                cmd.internalNotes(),
                cmd.mapLatitude(),
                cmd.mapLongitude(),
                cmd.googleMapsUrl(),
                cmd.documentDisplayName(),
                cmd.documentSubtitle(),
                cmd.documentLogoUrl(),
                cmd.documentFooter(),
                cmd.documentShowAddress(),
                cmd.documentShowPhone(),
                cmd.documentShowEmail(),
                cmd.documentShowCuit(),
                cmd.documentShowLegalName(),
                cmd.documentShowLogo(),
                cmd.licenseNumber(),
                cmd.licenseType(),
                cmd.licenseExpirationDate(),
                cmd.professionalDirectorName(),
                cmd.professionalDirectorLicense(),
                cmd.legalDocumentSummary(),
                cmd.legalNotes(),
                status,
                null,
                Instant.now()
        );
        Consultorio saved = consultorioRepo.save(consultorio);
        cargoEmpleadoCatalogoBootstrapService.ensureDefaults();
        consultorioEspecialidadBootstrapService.enableDefaultsForConsultorio(saved.getId());
        consultorioAntecedenteBootstrapService.ensureDefaults(saved.getId(), "system");
        return toResult(saved);
    }

    public ConsultorioResult update(UpdateConsultorioCommand cmd, String userEmail, Set<String> roles) {
        Consultorio consultorio = findOrThrow(cmd.id());
        if (!roles.contains("ROLE_ADMIN")) {
            assertAdminMember(cmd.id(), resolveUserId(userEmail), roles);
        }
        assertActive(consultorio);
        String status = normalizeStatus(cmd.status(), consultorio.getStatus());
        consultorio.update(
                cmd.name(),
                cmd.description(),
                cmd.logoUrl(),
                cmd.cuit(),
                cmd.legalName(),
                cmd.address(),
                cmd.geoAddress(),
                cmd.accessReference(),
                cmd.floorUnit(),
                cmd.phone(),
                cmd.email(),
                cmd.administrativeContact(),
                cmd.internalNotes(),
                cmd.mapLatitude(),
                cmd.mapLongitude(),
                cmd.googleMapsUrl(),
                cmd.documentDisplayName(),
                cmd.documentSubtitle(),
                cmd.documentLogoUrl(),
                cmd.documentFooter(),
                cmd.documentShowAddress(),
                cmd.documentShowPhone(),
                cmd.documentShowEmail(),
                cmd.documentShowCuit(),
                cmd.documentShowLegalName(),
                cmd.documentShowLogo(),
                cmd.licenseNumber(),
                cmd.licenseType(),
                cmd.licenseExpirationDate(),
                cmd.professionalDirectorName(),
                cmd.professionalDirectorLicense(),
                cmd.legalDocumentSummary(),
                cmd.legalNotes(),
                status
        );
        return toResult(consultorioRepo.save(consultorio));
    }

    public void inactivate(UUID id, String userEmail, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede dar de baja consultorios");
        }
        Consultorio consultorio = findOrThrow(id);
        if (consultorio.isActive()) {
            consultorio.inactivate();
            consultorioRepo.save(consultorio);
        }
    }

    public ConsultorioResult activate(UUID id, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede reactivar consultorios");
        }
        Consultorio consultorio = findOrThrow(id);
        if (!consultorio.isActive()) {
            consultorio.activate();
            consultorio = consultorioRepo.save(consultorio);
        }
        return toResult(consultorio);
    }

    private Consultorio findOrThrow(UUID id) {
        return ConsultorioStateGuardService.requireExists(consultorioRepo, id);
    }

    private String normalizeStatus(String rawStatus, String fallback) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return fallback;
        }
        return rawStatus.trim();
    }

    private void assertActive(Consultorio consultorio) {
        if (!consultorio.isActive()) {
            throw new ConsultorioInactiveException("Consultorio inactivo. Solo ADMIN puede reactivarlo.");
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private void assertMember(UUID consultorioId, UUID userId) {
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertAdminMember(UUID consultorioId, UUID userId, Set<String> roles) {
        if (!roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        assertMember(consultorioId, userId);
    }

    private ConsultorioResult toResult(Consultorio c) {
        return new ConsultorioResult(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getLogoUrl(),
                c.getCuit(),
                c.getLegalName(),
                c.getAddress(),
                c.getGeoAddress(),
                c.getAccessReference(),
                c.getFloorUnit(),
                c.getPhone(),
                c.getEmail(),
                c.getAdministrativeContact(),
                c.getInternalNotes(),
                c.getMapLatitude(),
                c.getMapLongitude(),
                c.getGoogleMapsUrl(),
                c.getDocumentDisplayName(),
                c.getDocumentSubtitle(),
                c.getDocumentLogoUrl(),
                c.getDocumentFooter(),
                c.getDocumentShowAddress(),
                c.getDocumentShowPhone(),
                c.getDocumentShowEmail(),
                c.getDocumentShowCuit(),
                c.getDocumentShowLegalName(),
                c.getDocumentShowLogo(),
                c.getLicenseNumber(),
                c.getLicenseType(),
                c.getLicenseExpirationDate(),
                c.getProfessionalDirectorName(),
                c.getProfessionalDirectorLicense(),
                c.getLegalDocumentSummary(),
                c.getLegalNotes(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
