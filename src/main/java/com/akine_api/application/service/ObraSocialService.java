package com.akine_api.application.service;

import com.akine_api.application.dto.command.ChangeObraSocialEstadoCommand;
import com.akine_api.application.dto.command.PlanCommand;
import com.akine_api.application.dto.command.UpsertObraSocialCommand;
import com.akine_api.application.dto.result.*;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.exception.ObraSocialConflictException;
import com.akine_api.domain.exception.ObraSocialNotFoundException;
import com.akine_api.domain.exception.ObraSocialValidationException;
import com.akine_api.domain.model.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ObraSocialService {

    private final ObraSocialRepositoryPort obraSocialRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ObraSocialService(ObraSocialRepositoryPort obraSocialRepo,
                             ConsultorioRepositoryPort consultorioRepo,
                             UserRepositoryPort userRepo) {
        this.obraSocialRepo = obraSocialRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public PagedResult<ObraSocialListItemResult> list(UUID consultorioId,
                                                       String q,
                                                       ObraSocialEstado estado,
                                                       Boolean conPlanes,
                                                       int page,
                                                       int size,
                                                       String userEmail,
                                                       Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("nombreCompleto").ascending());

        var resultPage = obraSocialRepo.search(consultorioId, normalize(q), estado, conPlanes, pageable);
        return new PagedResult<>(resultPage.getContent(), safePage, safeSize, resultPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ObraSocialDetailResult getById(UUID consultorioId,
                                          UUID obraSocialId,
                                          String userEmail,
                                          Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);

        ObraSocial os = obraSocialRepo.findByIdAndConsultorioId(obraSocialId, consultorioId)
                .orElseThrow(() -> new ObraSocialNotFoundException("Obra social no encontrada"));
        return toDetailResult(os);
    }

    public ObraSocialDetailResult create(UpsertObraSocialCommand cmd,
                                         String userEmail,
                                         Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        validateForCreate(cmd);

        ObraSocial obraSocial = new ObraSocial(
                UUID.randomUUID(),
                cmd.consultorioId(),
                requireValue(cmd.acronimo(), "El acronimo es obligatorio"),
                requireValue(cmd.nombreCompleto(), "El nombre completo es obligatorio"),
                normalizeCuit(cmd.cuit()),
                normalize(cmd.email()),
                normalize(cmd.telefono()),
                normalize(cmd.telefonoAlternativo()),
                normalize(cmd.representante()),
                normalize(cmd.observacionesInternas()),
                normalize(cmd.direccionLinea()),
                cmd.estado() == null ? ObraSocialEstado.ACTIVE : cmd.estado(),
                Instant.now(),
                buildPlans(cmd.planes())
        );

        return toDetailResult(obraSocialRepo.save(obraSocial));
    }

    public ObraSocialDetailResult update(UpsertObraSocialCommand cmd,
                                         String userEmail,
                                         Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        UUID obraSocialId = Optional.ofNullable(cmd.id())
                .orElseThrow(() -> new ObraSocialValidationException("El id de obra social es obligatorio para actualizar"));

        ObraSocial current = obraSocialRepo.findByIdAndConsultorioId(obraSocialId, cmd.consultorioId())
                .orElseThrow(() -> new ObraSocialNotFoundException("Obra social no encontrada"));

        validateForUpdate(cmd, current.getId());

        current.update(
                requireValue(cmd.acronimo(), "El acronimo es obligatorio"),
                requireValue(cmd.nombreCompleto(), "El nombre completo es obligatorio"),
                normalizeCuit(cmd.cuit()),
                normalize(cmd.email()),
                normalize(cmd.telefono()),
                normalize(cmd.telefonoAlternativo()),
                normalize(cmd.representante()),
                normalize(cmd.observacionesInternas()),
                normalize(cmd.direccionLinea()),
                cmd.estado() == null ? current.getEstado() : cmd.estado(),
                buildPlans(cmd.planes())
        );

        return toDetailResult(obraSocialRepo.save(current));
    }

    public ObraSocialDetailResult changeEstado(ChangeObraSocialEstadoCommand cmd,
                                               String userEmail,
                                               Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);

        ObraSocial current = obraSocialRepo.findByIdAndConsultorioId(cmd.obraSocialId(), cmd.consultorioId())
                .orElseThrow(() -> new ObraSocialNotFoundException("Obra social no encontrada"));

        current.changeEstado(cmd.estado());
        return toDetailResult(obraSocialRepo.save(current));
    }

    private List<ObraSocialPlan> buildPlans(List<PlanCommand> inputPlans) {
        if (inputPlans == null || inputPlans.isEmpty()) {
            throw new ObraSocialValidationException("Debe cargar al menos un plan");
        }

        Set<String> names = new HashSet<>();
        List<ObraSocialPlan> out = new ArrayList<>();

        for (PlanCommand cmd : inputPlans) {
            String nombreCorto = requireValue(cmd.nombreCorto(), "El nombre corto del plan es obligatorio");
            String nombreCortoNorm = normalizePlanName(nombreCorto);
            if (!names.add(nombreCortoNorm)) {
                throw new ObraSocialConflictException("No se permiten planes duplicados por nombre corto");
            }

            TipoCobertura tipoCobertura = Optional.ofNullable(cmd.tipoCobertura())
                    .orElseThrow(() -> new ObraSocialValidationException("El tipo de cobertura es obligatorio"));
            TipoCoseguro tipoCoseguro = Optional.ofNullable(cmd.tipoCoseguro())
                    .orElseThrow(() -> new ObraSocialValidationException("El tipo de coseguro es obligatorio"));

            BigDecimal valorCobertura = scaleNonNegative(cmd.valorCobertura(), "El valor de cobertura no puede ser negativo");
            BigDecimal valorCoseguro = scaleNonNegative(cmd.valorCoseguro(), "El valor de coseguro no puede ser negativo");
            int prestaciones = Optional.ofNullable(cmd.prestacionesSinAutorizacion()).orElse(0);
            if (prestaciones < 0) {
                throw new ObraSocialValidationException("Prestaciones sin autorizacion no puede ser negativo");
            }

            validateCoverage(tipoCobertura, valorCobertura);
            valorCoseguro = validateCopago(tipoCoseguro, valorCoseguro);

            out.add(new ObraSocialPlan(
                    cmd.id() == null ? UUID.randomUUID() : cmd.id(),
                    nombreCorto,
                    nombreCortoNorm,
                    requireValue(cmd.nombreCompleto(), "El nombre completo del plan es obligatorio"),
                    tipoCobertura,
                    valorCobertura,
                    tipoCoseguro,
                    valorCoseguro,
                    prestaciones,
                    normalize(cmd.observaciones()),
                    cmd.activo() == null || cmd.activo(),
                    Instant.now()
            ));
        }

        return out;
    }

    private void validateCoverage(TipoCobertura tipoCobertura, BigDecimal valorCobertura) {
        if (tipoCobertura == TipoCobertura.PORCENTAJE || tipoCobertura == TipoCobertura.MIXTO) {
            if (valorCobertura.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ObraSocialValidationException("La cobertura porcentual no puede superar 100");
            }
        }
    }

    private BigDecimal validateCopago(TipoCoseguro tipoCoseguro, BigDecimal valorCoseguro) {
        if (tipoCoseguro == TipoCoseguro.SIN_COSEGURO) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (tipoCoseguro == TipoCoseguro.PORCENTAJE && valorCoseguro.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ObraSocialValidationException("El coseguro porcentual no puede superar 100");
        }
        return valorCoseguro;
    }

    private void validateForCreate(UpsertObraSocialCommand cmd) {
        validateCommon(cmd);
        String cuit = normalizeCuit(cmd.cuit());
        if (obraSocialRepo.existsByConsultorioIdAndCuit(cmd.consultorioId(), cuit)) {
            throw new ObraSocialConflictException("Ya existe una obra social con ese CUIT en el consultorio");
        }
    }

    private void validateForUpdate(UpsertObraSocialCommand cmd, UUID id) {
        validateCommon(cmd);
        String cuit = normalizeCuit(cmd.cuit());
        if (obraSocialRepo.existsByConsultorioIdAndCuitAndIdNot(cmd.consultorioId(), cuit, id)) {
            throw new ObraSocialConflictException("Ya existe una obra social con ese CUIT en el consultorio");
        }
    }

    private void validateCommon(UpsertObraSocialCommand cmd) {
        String email = normalize(cmd.email());
        String telefono = normalize(cmd.telefono());
        if (email == null && telefono == null) {
            throw new ObraSocialValidationException("Debe indicar email o telefono de contacto");
        }
        if (email != null && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ObraSocialValidationException("Formato de email invalido");
        }

        normalizeCuit(cmd.cuit());
        if (cmd.planes() == null || cmd.planes().isEmpty()) {
            throw new ObraSocialValidationException("Debe cargar al menos un plan");
        }
    }

    private String normalizeCuit(String cuitInput) {
        String onlyDigits = Optional.ofNullable(cuitInput)
                .map(v -> v.replaceAll("[^0-9]", ""))
                .orElse("");

        if (onlyDigits.length() != 11) {
            throw new ObraSocialValidationException("El CUIT debe tener 11 digitos");
        }

        int[] factors = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(onlyDigits.charAt(i)) * factors[i];
        }
        int mod = 11 - (sum % 11);
        int expected = switch (mod) {
            case 11 -> 0;
            case 10 -> 9;
            default -> mod;
        };

        int checkDigit = Character.getNumericValue(onlyDigits.charAt(10));
        if (checkDigit != expected) {
            throw new ObraSocialValidationException("CUIT invalido");
        }
        return onlyDigits;
    }

    private BigDecimal scaleNonNegative(BigDecimal value, String message) {
        BigDecimal safe = Optional.ofNullable(value)
                .orElseThrow(() -> new ObraSocialValidationException(message));
        if (safe.compareTo(BigDecimal.ZERO) < 0) {
            throw new ObraSocialValidationException(message);
        }
        return safe.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalize(String value) {
        if (value == null) return null;
        String out = value.trim();
        return out.isBlank() ? null : out;
    }

    private String requireValue(String value, String message) {
        String n = normalize(value);
        if (n == null) {
            throw new ObraSocialValidationException(message);
        }
        return n;
    }

    private String normalizePlanName(String name) {
        String raw = requireValue(name, "El nombre corto del plan es obligatorio");
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.isBlank()) {
            throw new ObraSocialValidationException("El nombre corto del plan es obligatorio");
        }
        return normalized;
    }

    private void assertConsultorioExists(UUID consultorioId) {
        consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado"));
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        if (!consultorioRepo.findConsultorioIdsByUserId(userId).contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        if (!roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        UUID userId = resolveUserId(userEmail);
        if (!consultorioRepo.findConsultorioIdsByUserId(userId).contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private ObraSocialDetailResult toDetailResult(ObraSocial obraSocial) {
        List<PlanResult> plans = obraSocial.getPlanes().stream().map(p -> new PlanResult(
                p.getId(),
                p.getNombreCorto(),
                p.getNombreCompleto(),
                p.getTipoCobertura(),
                p.getValorCobertura(),
                p.getTipoCoseguro(),
                p.getValorCoseguro(),
                p.getPrestacionesSinAutorizacion(),
                p.getObservaciones(),
                p.isActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )).toList();

        return new ObraSocialDetailResult(
                obraSocial.getId(),
                obraSocial.getConsultorioId(),
                obraSocial.getAcronimo(),
                obraSocial.getNombreCompleto(),
                obraSocial.getCuit(),
                obraSocial.getEmail(),
                obraSocial.getTelefono(),
                obraSocial.getTelefonoAlternativo(),
                obraSocial.getRepresentante(),
                obraSocial.getObservacionesInternas(),
                obraSocial.getDireccionLinea(),
                obraSocial.getEstado(),
                plans,
                obraSocial.getCreatedAt(),
                obraSocial.getUpdatedAt()
        );
    }
}

