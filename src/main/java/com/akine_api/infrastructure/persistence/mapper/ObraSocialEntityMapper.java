package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ObraSocial;
import com.akine_api.domain.model.ObraSocialPlan;
import com.akine_api.infrastructure.persistence.entity.ObraSocialEntity;
import com.akine_api.infrastructure.persistence.entity.ObraSocialPlanEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObraSocialEntityMapper {

    public ObraSocial toDomain(ObraSocialEntity entity) {
        if (entity == null) return null;
        return new ObraSocial(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getAcronimo(),
                entity.getNombreCompleto(),
                entity.getCuit(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getTelefonoAlternativo(),
                entity.getRepresentante(),
                entity.getObservacionesInternas(),
                entity.getDireccionLinea(),
                entity.getEstado(),
                entity.getCreatedAt(),
                entity.getPlanes().stream().map(this::toDomainPlan).toList()
        );
    }

    public ObraSocialEntity toEntity(ObraSocial domain) {
        if (domain == null) return null;

        ObraSocialEntity e = new ObraSocialEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setAcronimo(domain.getAcronimo());
        e.setNombreCompleto(domain.getNombreCompleto());
        e.setCuit(domain.getCuit());
        e.setEmail(domain.getEmail());
        e.setTelefono(domain.getTelefono());
        e.setTelefonoAlternativo(domain.getTelefonoAlternativo());
        e.setRepresentante(domain.getRepresentante());
        e.setObservacionesInternas(domain.getObservacionesInternas());
        e.setDireccionLinea(domain.getDireccionLinea());
        e.setEstado(domain.getEstado());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());

        List<ObraSocialPlanEntity> plans = domain.getPlanes().stream().map(p -> toEntityPlan(p, e)).collect(Collectors.toList());
        e.getPlanes().clear();
        e.getPlanes().addAll(plans);
        return e;
    }

    private ObraSocialPlan toDomainPlan(ObraSocialPlanEntity entity) {
        return new ObraSocialPlan(
                entity.getId(),
                entity.getNombreCorto(),
                entity.getNombreCortoNorm(),
                entity.getNombreCompleto(),
                entity.getTipoCobertura(),
                entity.getValorCobertura(),
                entity.getTipoCoseguro(),
                entity.getValorCoseguro(),
                entity.getPrestacionesSinAutorizacion(),
                entity.getObservaciones(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    private ObraSocialPlanEntity toEntityPlan(ObraSocialPlan plan, ObraSocialEntity parent) {
        ObraSocialPlanEntity e = new ObraSocialPlanEntity();
        e.setId(plan.getId());
        e.setObraSocial(parent);
        e.setNombreCorto(plan.getNombreCorto());
        e.setNombreCortoNorm(plan.getNombreCortoNorm());
        e.setNombreCompleto(plan.getNombreCompleto());
        e.setTipoCobertura(plan.getTipoCobertura());
        e.setValorCobertura(plan.getValorCobertura());
        e.setTipoCoseguro(plan.getTipoCoseguro());
        e.setValorCoseguro(plan.getValorCoseguro());
        e.setPrestacionesSinAutorizacion(plan.getPrestacionesSinAutorizacion());
        e.setObservaciones(plan.getObservaciones());
        e.setActivo(plan.isActivo());
        e.setCreatedAt(plan.getCreatedAt());
        e.setUpdatedAt(plan.getUpdatedAt());
        return e;
    }
}

