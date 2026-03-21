package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;
import com.akine_api.infrastructure.persistence.mapper.SuscripcionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.SuscripcionJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class SuscripcionRepositoryAdapter implements SuscripcionRepositoryPort {

    private final SuscripcionJpaRepository repo;
    private final SuscripcionEntityMapper mapper;

    public SuscripcionRepositoryAdapter(SuscripcionJpaRepository repo, SuscripcionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Suscripcion save(Suscripcion suscripcion) {
        return mapper.toDomain(repo.save(mapper.toEntity(suscripcion)));
    }

    @Override
    public Optional<Suscripcion> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Suscripcion> findByConsultorioBaseId(UUID consultorioBaseId) {
        return repo.findByConsultorioBaseId(consultorioBaseId).map(mapper::toDomain);
    }

    @Override
    public Optional<Suscripcion> findByEmpresaId(UUID empresaId) {
        return repo.findByEmpresaId(empresaId).map(mapper::toDomain);
    }

    @Override
    public Optional<Suscripcion> findTopByOwnerUserId(UUID ownerUserId) {
        return repo.findTopByOwnerUserIdOrderByCreatedAtDesc(ownerUserId).map(mapper::toDomain);
    }

    @Override
    public Optional<Suscripcion> findByTrackingToken(String trackingToken) {
        return repo.findByTrackingToken(trackingToken).map(mapper::toDomain);
    }

    @Override
    public List<Suscripcion> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).getContent().stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countAll() {
        return repo.count();
    }

    @Override
    public List<Suscripcion> findByStatus(SuscripcionStatus status, int page, int size) {
        return repo.findByStatus(status.name(), PageRequest.of(page, size)).getContent().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByStatus(SuscripcionStatus status) {
        return repo.countByStatus(status.name());
    }

    @Override
    public int expireActiveDue(LocalDate today) {
        return repo.expireActiveDue(today, Instant.now());
    }

    @Override
    public int markPendingRenewalDue(LocalDate today, LocalDate cutoff) {
        return repo.markPendingRenewalDue(today, cutoff, Instant.now());
    }

    @Override
    public int expirePendingRenewalDue(LocalDate today) {
        return repo.expirePendingRenewalDue(today, Instant.now());
    }

    @Override
    public List<Suscripcion> findByStatusAndEndDate(SuscripcionStatus status, LocalDate targetDate) {
        return repo.findByStatusAndEndDate(status.name(), targetDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<String, Long> countGroupByStatus() {
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : repo.countGroupByStatus()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public Map<String, Long> countActiveGroupByPlanCode() {
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : repo.countActiveGroupByPlanCode()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public List<Suscripcion> findExpiringBetween(LocalDate from, LocalDate to) {
        return repo.findExpiringBetween(from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countCreatedAfter(Instant since) {
        return repo.countByCreatedAtAfter(since);
    }

    @Override
    public long countExpiredSince(Instant since) {
        return repo.countByStatusAndUpdatedAtAfter("EXPIRED", since);
    }
}
