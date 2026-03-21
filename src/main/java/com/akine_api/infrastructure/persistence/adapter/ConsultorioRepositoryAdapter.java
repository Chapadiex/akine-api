package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.mapper.ConsultorioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.akine_api.infrastructure.persistence.repository.MembershipJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsultorioRepositoryAdapter implements ConsultorioRepositoryPort {

    private static final ZoneId OPERATIVE_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final ConsultorioJpaRepository consultorioRepo;
    private final MembershipJpaRepository membershipRepo;
    private final ConsultorioEntityMapper mapper;

    public ConsultorioRepositoryAdapter(ConsultorioJpaRepository consultorioRepo,
                                        MembershipJpaRepository membershipRepo,
                                        ConsultorioEntityMapper mapper) {
        this.consultorioRepo = consultorioRepo;
        this.membershipRepo = membershipRepo;
        this.mapper = mapper;
    }

    @Override
    public Consultorio save(Consultorio consultorio) {
        return mapper.toDomain(consultorioRepo.save(mapper.toEntity(consultorio)));
    }

    @Override
    public Optional<Consultorio> findById(UUID id) {
        return consultorioRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Consultorio> findAll() {
        return consultorioRepo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Consultorio> findByIds(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        return consultorioRepo.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<UUID> findConsultorioIdsByUserId(UUID userId) {
        return membershipRepo.findConsultorioIdsByUserId(userId, LocalDate.now(OPERATIVE_ZONE));
    }

    @Override
    public long countByEmpresaId(UUID empresaId) {
        return consultorioRepo.countByEmpresaId(empresaId);
    }

    @Override
    public String generateNroConsultorio() {
        long seq = consultorioRepo.nextNroConsultorioSequence();
        return String.format("AKN-%06d", seq);
    }
}
