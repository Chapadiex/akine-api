package com.akine_api.application.service.sesion;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.SesionClinicaNotFoundException;
import com.akine_api.domain.model.sesion.CoberturaTipo;
import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.domain.model.sesion.ValidacionCoberturaEstado;
import com.akine_api.domain.repository.sesion.SesionAdministrativaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SesionAdministrativaService {

    private final SesionAdministrativaRepositoryPort repositoryPort;
    private final UserRepositoryPort userRepo;

    @Transactional
    public SesionAdministrativa registrar(UUID sesionId, UUID turnoId, UUID consultorioId,
                                           UUID pacienteId, CoberturaTipo coberturaTipo,
                                           UUID financiadorId, UUID planId, String numeroAfiliado,
                                           Boolean tienePedidoMedico, Boolean tieneOrden,
                                           Boolean tieneAutorizacion, String numeroAutorizacion,
                                           String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);

        // Upsert: if sesion already has an administrative record, update it
        Optional<SesionAdministrativa> existing = repositoryPort.findBySesionId(sesionId);

        boolean documentacionCompleta = evaluarDocumentacionCompleta(
                coberturaTipo, tienePedidoMedico, tieneOrden, tieneAutorizacion);
        String documentacionFaltante = documentacionCompleta ? null
                : buildDocumentacionFaltante(coberturaTipo, tienePedidoMedico, tieneOrden, tieneAutorizacion);
        boolean esFacturableOs = CoberturaTipo.OBRA_SOCIAL.equals(coberturaTipo)
                || CoberturaTipo.MIXTA.equals(coberturaTipo);

        SesionAdministrativa sesion;
        if (existing.isPresent()) {
            sesion = existing.get();
            sesion.setTurnoId(turnoId);
            sesion.setCoberturaTipo(coberturaTipo);
            sesion.setFinanciadorId(financiadorId);
            sesion.setPlanId(planId);
            sesion.setNumeroAfiliado(numeroAfiliado);
            sesion.setTienePedidoMedico(tienePedidoMedico != null ? tienePedidoMedico : false);
            sesion.setTieneOrden(tieneOrden != null ? tieneOrden : false);
            sesion.setTieneAutorizacion(tieneAutorizacion != null ? tieneAutorizacion : false);
            sesion.setNumeroAutorizacion(numeroAutorizacion);
            sesion.setDocumentacionCompleta(documentacionCompleta);
            sesion.setDocumentacionFaltante(documentacionFaltante);
            sesion.setEsFacturableOs(esFacturableOs);
            sesion.setActualizadoPor(usuarioId);
            sesion.setActualizadoEn(Instant.now());
        } else {
            sesion = SesionAdministrativa.builder()
                    .sesionId(sesionId)
                    .turnoId(turnoId)
                    .consultorioId(consultorioId)
                    .pacienteId(pacienteId)
                    .coberturaTipo(coberturaTipo)
                    .financiadorId(financiadorId)
                    .planId(planId)
                    .numeroAfiliado(numeroAfiliado)
                    .tienePedidoMedico(tienePedidoMedico != null ? tienePedidoMedico : false)
                    .tieneOrden(tieneOrden != null ? tieneOrden : false)
                    .tieneAutorizacion(tieneAutorizacion != null ? tieneAutorizacion : false)
                    .numeroAutorizacion(numeroAutorizacion)
                    .asistenciaConfirmada(false)
                    .documentacionCompleta(documentacionCompleta)
                    .documentacionFaltante(documentacionFaltante)
                    .validacionCoberturaEstado(ValidacionCoberturaEstado.PENDIENTE)
                    .esFacturableOs(esFacturableOs)
                    .registradoPor(usuarioId)
                    .registradoEn(Instant.now())
                    .build();
        }

        return repositoryPort.save(sesion);
    }

    @Transactional
    public SesionAdministrativa confirmarAsistencia(UUID sesionId, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        SesionAdministrativa sesion = findBySesionIdOrThrow(sesionId);
        sesion.setAsistenciaConfirmada(true);
        sesion.setActualizadoPor(usuarioId);
        sesion.setActualizadoEn(Instant.now());
        return repositoryPort.save(sesion);
    }

    @Transactional(readOnly = true)
    public Optional<SesionAdministrativa> findBySesionId(UUID sesionId) {
        return repositoryPort.findBySesionId(sesionId);
    }

    @Transactional(readOnly = true)
    public Optional<SesionAdministrativa> findByTurnoId(UUID turnoId) {
        return repositoryPort.findByTurnoId(turnoId);
    }

    @Transactional(readOnly = true)
    public List<SesionAdministrativa> findByConsultorioId(UUID consultorioId) {
        return repositoryPort.findByConsultorioId(consultorioId);
    }

    private SesionAdministrativa findBySesionIdOrThrow(UUID sesionId) {
        return repositoryPort.findBySesionId(sesionId)
                .orElseThrow(() -> new SesionClinicaNotFoundException(
                        "No hay sesión administrativa para sesión " + sesionId));
    }

    private boolean evaluarDocumentacionCompleta(CoberturaTipo coberturaTipo,
                                                   Boolean tienePedidoMedico,
                                                   Boolean tieneOrden,
                                                   Boolean tieneAutorizacion) {
        if (CoberturaTipo.PARTICULAR.equals(coberturaTipo)) {
            return true;
        }
        // OS or MIXTA requires at least one of pedido/orden
        return Boolean.TRUE.equals(tienePedidoMedico) || Boolean.TRUE.equals(tieneOrden);
    }

    private String buildDocumentacionFaltante(CoberturaTipo coberturaTipo,
                                               Boolean tienePedidoMedico,
                                               Boolean tieneOrden,
                                               Boolean tieneAutorizacion) {
        if (CoberturaTipo.PARTICULAR.equals(coberturaTipo)) return null;
        StringBuilder sb = new StringBuilder();
        if (!Boolean.TRUE.equals(tienePedidoMedico) && !Boolean.TRUE.equals(tieneOrden)) {
            sb.append("pedido_medico_u_orden");
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
