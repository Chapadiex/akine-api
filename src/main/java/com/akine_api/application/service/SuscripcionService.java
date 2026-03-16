package com.akine_api.application.service;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.PagedResult;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.*;
import com.akine_api.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class SuscripcionService {

    private static final ZoneId OPERATIVE_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo;
    private final PasswordEncoderPort passwordEncoder;
    private final EmpresaRepositoryPort empresaRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final MembershipRepositoryPort membershipRepo;
    private final SuscripcionRepositoryPort suscripcionRepo;
    private final SuscripcionAuditoriaRepositoryPort auditoriaRepo;
    private final EmailPort emailPort;

    public SuscripcionService(UserRepositoryPort userRepo,
                              RoleRepositoryPort roleRepo,
                              PasswordEncoderPort passwordEncoder,
                              EmpresaRepositoryPort empresaRepo,
                              ConsultorioRepositoryPort consultorioRepo,
                              MembershipRepositoryPort membershipRepo,
                              SuscripcionRepositoryPort suscripcionRepo,
                              SuscripcionAuditoriaRepositoryPort auditoriaRepo,
                              EmailPort emailPort) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.empresaRepo = empresaRepo;
        this.consultorioRepo = consultorioRepo;
        this.membershipRepo = membershipRepo;
        this.suscripcionRepo = suscripcionRepo;
        this.auditoriaRepo = auditoriaRepo;
        this.emailPort = emailPort;
    }

    /**
     * Backward-compatible single-shot signup.
     */
    public SubscriptionSummaryResult createSubscription(CreateSubscriptionCommand cmd) {
        SubscriptionSummaryResult draft = createDraft(new CreateSubscriptionDraftCommand(
                normalize(cmd.planCode()),
                normalize(cmd.billingCycle()),
                cmd.ownerEmail(),
                cmd.ownerPassword()
        ));

        UUID id = draft.id();
        updateOwner(new UpdateSubscriptionOwnerCommand(
                id,
                cmd.ownerFirstName(),
                cmd.ownerLastName(),
                cmd.ownerDocumentoFiscal(),
                cmd.ownerEmail(),
                cmd.ownerPhone(),
                cmd.ownerPassword()
        ));
        updateCompany(new UpdateSubscriptionCompanyCommand(
                id,
                cmd.companyName(),
                cmd.companyCuit(),
                cmd.companyAddress(),
                cmd.companyCity(),
                cmd.companyProvince()
        ));
        updateClinic(new UpdateSubscriptionClinicCommand(
                id,
                cmd.consultorioName(),
                cmd.consultorioAddress(),
                cmd.consultorioPhone(),
                cmd.consultorioEmail()
        ));
        simulatePayment(new SimulateSubscriptionPaymentCommand(
                id,
                "SIM-" + id.toString().substring(0, 8).toUpperCase(Locale.ROOT)
        ));
        return submitForApproval(new SubmitSubscriptionForApprovalCommand(id));
    }

    public SubscriptionSummaryResult createDraft(CreateSubscriptionDraftCommand cmd) {
        String email = normalize(cmd.ownerEmail());
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        Role role = roleRepo.findByName(RoleName.PROFESIONAL_ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(RoleName.PROFESIONAL_ADMIN.name()));

        User owner = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(normalize(cmd.ownerPassword())),
                "Pendiente",
                "Pendiente",
                null,
                UserStatus.PENDING,
                Instant.now()
        );
        owner.addRole(role);
        User savedOwner = userRepo.save(owner);

        Empresa empresa = new Empresa(
                UUID.randomUUID(),
                "Empresa pendiente",
                "PENDING-" + randomShortId(),
                "Pendiente",
                "Pendiente",
                "Pendiente",
                Instant.now()
        );
        Empresa savedEmpresa = empresaRepo.save(empresa);

        Consultorio consultorio = new Consultorio(
                UUID.randomUUID(),
                "Consultorio pendiente",
                null,
                "Pendiente",
                "Pendiente",
                email,
                "INACTIVE",
                savedEmpresa.getId(),
                Instant.now()
        );
        Consultorio savedConsultorio = consultorioRepo.save(consultorio);

        Membership membership = new Membership(
                UUID.randomUUID(),
                savedOwner.getId(),
                savedConsultorio.getId(),
                MembershipRole.PROFESIONAL_ADMIN,
                MembershipStatus.INACTIVE,
                Instant.now()
        );
        membershipRepo.save(membership);

        Suscripcion suscripcion = new Suscripcion(
                UUID.randomUUID(),
                savedOwner.getId(),
                savedEmpresa.getId(),
                savedConsultorio.getId(),
                normalizeOrDefault(cmd.planCode(), "STARTER"),
                normalizeOrDefault(cmd.billingCycle(), "TRIAL"),
                "DRAFT",
                null,
                UUID.randomUUID().toString(),
                SuscripcionStatus.DRAFT,
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        saveAudit(saved, "SUBSCRIPTION_DRAFT_CREATED", null, saved.getStatus().name(), null, null);
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult updateOwner(UpdateSubscriptionOwnerCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        ensureStatus(suscripcion, SuscripcionStatus.DRAFT, SuscripcionStatus.EMAIL_PENDING);

        User owner = userRepo.findById(suscripcion.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(suscripcion.getOwnerUserId().toString()));
        owner.updateProfile(normalize(cmd.firstName()), normalize(cmd.lastName()), normalize(cmd.phone()));
        owner.updateDocumentoFiscal(normalize(cmd.documentoFiscal()));
        owner.changePassword(passwordEncoder.encode(normalize(cmd.password())));
        userRepo.save(owner);

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.markEmailPending();
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        saveAudit(saved, "SUBSCRIPTION_OWNER_UPDATED", previousStatus, saved.getStatus().name(), null, null);
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult updateCompany(UpdateSubscriptionCompanyCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        ensureStatus(suscripcion, SuscripcionStatus.EMAIL_PENDING, SuscripcionStatus.SETUP_PENDING, SuscripcionStatus.PAYMENT_PENDING);

        Empresa empresa = empresaRepo.findById(suscripcion.getEmpresaId())
                .orElseThrow(() -> new SubscriptionStateException("Empresa de suscripción no encontrada."));
        empresa.update(
                normalize(cmd.name()),
                normalize(cmd.cuit()),
                normalize(cmd.address()),
                normalize(cmd.city()),
                normalize(cmd.province())
        );
        empresaRepo.save(empresa);

        saveAudit(suscripcion, "SUBSCRIPTION_COMPANY_UPDATED", suscripcion.getStatus().name(), suscripcion.getStatus().name(), null, null);
        return buildSummary(suscripcion);
    }

    public SubscriptionSummaryResult updateClinic(UpdateSubscriptionClinicCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        ensureStatus(suscripcion, SuscripcionStatus.EMAIL_PENDING, SuscripcionStatus.PAYMENT_PENDING, SuscripcionStatus.SETUP_PENDING);

        Consultorio consultorio = consultorioRepo.findById(suscripcion.getConsultorioBaseId())
                .orElseThrow(() -> new SubscriptionStateException("Consultorio base de suscripción no encontrado."));
        consultorio.update(
                normalize(cmd.name()),
                consultorio.getDescription(),
                consultorio.getLogoUrl(),
                consultorio.getCuit(),
                consultorio.getLegalName(),
                normalize(cmd.address()),
                consultorio.getGeoAddress(),
                consultorio.getAccessReference(),
                consultorio.getFloorUnit(),
                normalize(cmd.phone()),
                normalize(cmd.email()),
                consultorio.getAdministrativeContact(),
                consultorio.getInternalNotes(),
                consultorio.getMapLatitude(),
                consultorio.getMapLongitude(),
                consultorio.getGoogleMapsUrl(),
                consultorio.getDocumentDisplayName(),
                consultorio.getDocumentSubtitle(),
                consultorio.getDocumentLogoUrl(),
                consultorio.getDocumentFooter(),
                consultorio.getDocumentShowAddress(),
                consultorio.getDocumentShowPhone(),
                consultorio.getDocumentShowEmail(),
                consultorio.getDocumentShowCuit(),
                consultorio.getDocumentShowLegalName(),
                consultorio.getDocumentShowLogo(),
                consultorio.getLicenseNumber(),
                consultorio.getLicenseType(),
                consultorio.getLicenseExpirationDate(),
                consultorio.getProfessionalDirectorName(),
                consultorio.getProfessionalDirectorLicense(),
                consultorio.getLegalDocumentSummary(),
                consultorio.getLegalNotes(),
                consultorio.getStatus()
        );
        consultorioRepo.save(consultorio);

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.markSetupPending();
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        saveAudit(saved, "SUBSCRIPTION_CLINIC_UPDATED", previousStatus, saved.getStatus().name(), null, null);
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult simulatePayment(SimulateSubscriptionPaymentCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        ensureStatus(suscripcion, SuscripcionStatus.EMAIL_PENDING, SuscripcionStatus.SETUP_PENDING, SuscripcionStatus.PAYMENT_PENDING);

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.markPaymentPending(normalize(cmd.paymentReference()));
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        saveAudit(saved, "SUBSCRIPTION_PAYMENT_SIMULATED", previousStatus, saved.getStatus().name(), null, normalize(cmd.paymentReference()));
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult submitForApproval(SubmitSubscriptionForApprovalCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        ensureStatus(suscripcion, SuscripcionStatus.PAYMENT_PENDING, SuscripcionStatus.SETUP_PENDING, SuscripcionStatus.EMAIL_PENDING);

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.submitForApproval();
        Suscripcion saved = suscripcionRepo.save(suscripcion);

        User owner = userRepo.findById(saved.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getOwnerUserId().toString()));
        owner.markPending();
        userRepo.save(owner);

        saveAudit(saved, "SUBSCRIPTION_SUBMITTED_FOR_APPROVAL", previousStatus, saved.getStatus().name(), null, null);
        emailPort.sendSubscriptionReceived(owner.getEmail(), owner.getFirstName(), saved.getId().toString());
        return buildSummary(saved);
    }

    @Transactional(readOnly = true)
    public Optional<SubscriptionSummaryResult> getByTrackingToken(String trackingToken) {
        return suscripcionRepo.findByTrackingToken(normalize(trackingToken)).map(this::buildSummary);
    }

    @Transactional(readOnly = true)
    public Optional<SubscriptionSummaryResult> findLatestByOwnerUserId(UUID ownerUserId) {
        return suscripcionRepo.findTopByOwnerUserId(ownerUserId).map(this::buildSummary);
    }

    @Transactional(readOnly = true)
    public Optional<SubscriptionSummaryResult> getById(UUID id) {
        return suscripcionRepo.findById(id).map(this::buildSummary);
    }

    @Transactional(readOnly = true)
    public List<SuscripcionAuditoria> getAuditTrail(UUID subscriptionId) {
        return auditoriaRepo.findBySuscripcionId(subscriptionId);
    }

    @Transactional(readOnly = true)
    public PagedResult<SubscriptionSummaryResult> listSubscriptions(String status, int page, int size) {
        SuscripcionStatus parsedStatus = parseStatus(status);
        List<Suscripcion> subscriptions = parsedStatus == null
                ? suscripcionRepo.findAll(page, size)
                : suscripcionRepo.findByStatus(parsedStatus, page, size);
        long total = parsedStatus == null
                ? suscripcionRepo.countAll()
                : suscripcionRepo.countByStatus(parsedStatus);

        List<SubscriptionSummaryResult> content = subscriptions.stream().map(this::buildSummary).toList();
        return new PagedResult<>(content, page, size, total);
    }

    public SubscriptionSummaryResult approve(ApproveSubscriptionCommand cmd) {
        if (cmd.startDate() == null || cmd.endDate() == null) {
            throw new SubscriptionStateException("Debe informar fecha de inicio y vencimiento.");
        }
        if (cmd.startDate().isAfter(cmd.endDate())) {
            throw new SubscriptionStateException("La fecha de inicio no puede ser posterior a la fecha de vencimiento.");
        }

        expireDueSubscriptions();
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        if (suscripcion.getStatus() != SuscripcionStatus.PENDING_APPROVAL) {
            throw new SubscriptionStateException("Solo se pueden aprobar suscripciones en estado PENDING_APPROVAL.");
        }

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.approve(cmd.startDate(), cmd.endDate(), cmd.actorUserId());
        Suscripcion saved = suscripcionRepo.save(suscripcion);

        User owner = userRepo.findById(saved.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getOwnerUserId().toString()));
        owner.activate();
        userRepo.save(owner);

        Consultorio consultorio = ConsultorioStateGuardService.requireExists(consultorioRepo, saved.getConsultorioBaseId());
        if (!consultorio.isActive()) {
            consultorio.activate();
            consultorioRepo.save(consultorio);
        }

        Membership membership = membershipRepo.findByUserIdAndConsultorioId(saved.getOwnerUserId(), saved.getConsultorioBaseId())
                .orElseThrow(() -> new SubscriptionStateException("No se encontró la membresía base del dueño."));
        membership.activate();
        membershipRepo.save(membership);

        saveAudit(saved, "SUBSCRIPTION_APPROVED", previousStatus, saved.getStatus().name(), cmd.actorUserId(), null);
        emailPort.sendSubscriptionApproved(owner.getEmail(), owner.getFirstName(), saved.getStartDate(), saved.getEndDate());
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult reject(RejectSubscriptionCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        if (suscripcion.getStatus() != SuscripcionStatus.PENDING_APPROVAL) {
            throw new SubscriptionStateException("Solo se pueden rechazar suscripciones en estado PENDING_APPROVAL.");
        }

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.reject(normalize(cmd.reason()), cmd.actorUserId());
        Suscripcion saved = suscripcionRepo.save(suscripcion);

        User owner = userRepo.findById(saved.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getOwnerUserId().toString()));
        owner.rejectActivation();
        userRepo.save(owner);

        membershipRepo.findByUserIdAndConsultorioId(saved.getOwnerUserId(), saved.getConsultorioBaseId())
                .ifPresent(m -> {
                    m.inactivate();
                    membershipRepo.save(m);
                });

        saveAudit(saved, "SUBSCRIPTION_REJECTED", previousStatus, saved.getStatus().name(), cmd.actorUserId(), normalize(cmd.reason()));
        emailPort.sendSubscriptionRejected(owner.getEmail(), owner.getFirstName(), normalize(cmd.reason()));
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult requestInfo(RequestSubscriptionInfoCommand cmd) {
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        if (suscripcion.getStatus() != SuscripcionStatus.PENDING_APPROVAL) {
            throw new SubscriptionStateException("Solo se puede pedir información adicional en estado PENDING_APPROVAL.");
        }
        String previousStatus = suscripcion.getStatus().name();
        suscripcion.markSetupPending();
        Suscripcion saved = suscripcionRepo.save(suscripcion);
        saveAudit(saved, "SUBSCRIPTION_INFO_REQUESTED", previousStatus, saved.getStatus().name(), cmd.actorUserId(), normalize(cmd.reason()));
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult suspend(SuspendSubscriptionCommand cmd) {
        expireDueSubscriptions();
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        if (suscripcion.getStatus() != SuscripcionStatus.ACTIVE) {
            throw new SubscriptionStateException("Solo se pueden suspender suscripciones en estado ACTIVE.");
        }

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.suspend(normalize(cmd.reason()), cmd.actorUserId());
        Suscripcion saved = suscripcionRepo.save(suscripcion);

        User owner = userRepo.findById(saved.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getOwnerUserId().toString()));
        saveAudit(saved, "SUBSCRIPTION_SUSPENDED", previousStatus, saved.getStatus().name(), cmd.actorUserId(), normalize(cmd.reason()));
        emailPort.sendSubscriptionSuspended(owner.getEmail(), owner.getFirstName(), normalize(cmd.reason()));
        return buildSummary(saved);
    }

    public SubscriptionSummaryResult reactivate(ReactivateSubscriptionCommand cmd) {
        expireDueSubscriptions();
        Suscripcion suscripcion = findSubscription(cmd.subscriptionId());
        if (suscripcion.getStatus() != SuscripcionStatus.SUSPENDED) {
            throw new SubscriptionStateException("Solo se pueden reactivar suscripciones en estado SUSPENDED.");
        }
        if (suscripcion.getEndDate() != null && suscripcion.getEndDate().isBefore(LocalDate.now(OPERATIVE_ZONE))) {
            throw new SubscriptionStateException("No se puede reactivar una suscripción vencida.");
        }

        String previousStatus = suscripcion.getStatus().name();
        suscripcion.reactivate(cmd.actorUserId());
        Suscripcion saved = suscripcionRepo.save(suscripcion);

        User owner = userRepo.findById(saved.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getOwnerUserId().toString()));
        saveAudit(saved, "SUBSCRIPTION_REACTIVATED", previousStatus, saved.getStatus().name(), cmd.actorUserId(), null);
        emailPort.sendSubscriptionReactivated(owner.getEmail(), owner.getFirstName(), saved.getEndDate());
        return buildSummary(saved);
    }

    public void expireDueSubscriptions() {
        suscripcionRepo.expireActiveDue(LocalDate.now(OPERATIVE_ZONE));
    }

    private Suscripcion findSubscription(UUID id) {
        return suscripcionRepo.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(id.toString()));
    }

    private void ensureStatus(Suscripcion suscripcion, SuscripcionStatus... expected) {
        Set<SuscripcionStatus> allowed = EnumSet.noneOf(SuscripcionStatus.class);
        allowed.addAll(Arrays.asList(expected));
        if (!allowed.contains(suscripcion.getStatus())) {
            throw new SubscriptionStateException(
                    "Estado inválido para esta operación: " + suscripcion.getStatus().name()
            );
        }
    }

    private void saveAudit(Suscripcion suscripcion,
                           String action,
                           String fromStatus,
                           String toStatus,
                           UUID actorUserId,
                           String reason) {
        auditoriaRepo.save(new SuscripcionAuditoria(
                UUID.randomUUID(),
                suscripcion.getId(),
                action,
                fromStatus,
                toStatus,
                actorUserId,
                reason,
                Instant.now()
        ));
    }

    private SubscriptionSummaryResult buildSummary(Suscripcion subscription) {
        User owner = userRepo.findById(subscription.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(subscription.getOwnerUserId().toString()));
        Empresa empresa = empresaRepo.findById(subscription.getEmpresaId())
                .orElseThrow(() -> new SubscriptionStateException("Empresa de suscripción no encontrada."));
        Consultorio consultorio = consultorioRepo.findById(subscription.getConsultorioBaseId())
                .orElseThrow(() -> new SubscriptionStateException("Consultorio base de suscripción no encontrado."));

        return new SubscriptionSummaryResult(
                subscription.getId(),
                subscription.getStatus().name(),
                subscription.getPlanCode(),
                subscription.getBillingCycle(),
                subscription.getOnboardingStep(),
                subscription.getTrackingToken(),
                subscription.getSubmittedForApprovalAt(),
                subscription.getRequestedAt(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getReviewedAt(),
                subscription.getReviewedByUserId(),
                subscription.getRejectionReason(),
                owner.getId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getEmail(),
                empresa.getId(),
                empresa.getName(),
                empresa.getCuit(),
                empresa.getCity(),
                empresa.getProvince(),
                consultorio.getId(),
                consultorio.getName(),
                consultorio.getAddress()
        );
    }

    private SuscripcionStatus parseStatus(String status) {
        String normalized = normalize(status);
        if (normalized == null) return null;
        try {
            return SuscripcionStatus.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new SubscriptionStateException("Estado de suscripción inválido: " + status);
        }
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeOrDefault(String value, String defaultValue) {
        String normalized = normalize(value);
        return normalized == null ? defaultValue : normalized.toUpperCase(Locale.ROOT);
    }

    private String randomShortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
