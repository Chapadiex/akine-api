package com.akine_api.service;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.CargoEmpleadoCatalogoBootstrapService;
import com.akine_api.application.service.ConsultorioAntecedenteBootstrapService;
import com.akine_api.application.service.ConsultorioEspecialidadBootstrapService;
import com.akine_api.application.service.ConsultorioService;
import com.akine_api.domain.exception.ConsultorioInactiveException;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioServiceTest {

    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock ConsultorioEspecialidadBootstrapService especialidadBootstrapService;
    @Mock ConsultorioAntecedenteBootstrapService antecedenteBootstrapService;
    @Mock CargoEmpleadoCatalogoBootstrapService cargoBootstrapService;

    ConsultorioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROF_ADMIN_ROLES = Set.of("ROLE_PROFESIONAL_ADMIN");
    private static final Set<String> PROF_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new ConsultorioService(
                consultorioRepo,
                userRepo,
                especialidadBootstrapService,
                antecedenteBootstrapService,
                cargoBootstrapService
        );
    }

    private Consultorio activeConsultorio() {
        return new Consultorio(
                CONSULTORIO_ID,
                "Consultorio Test",
                "Descripcion",
                null,
                "20123456789",
                "Razon Social",
                "Av. Siempreviva 123",
                "Avenida Siempreviva 123, Springfield, Buenos Aires, Argentina",
                null,
                null,
                "1155551234",
                "test@mail.com",
                "Laura Admin",
                "Notas",
                new BigDecimal("-34.603722"),
                new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592",
                "Consultorio Test",
                "Kinesiologia",
                null,
                null,
                true,
                true,
                true,
                false,
                true,
                true,
                "HAB-01",
                "Municipal",
                LocalDate.of(2027, 6, 30),
                "Dra. Perez",
                "MP-123",
                "Contrato base",
                "Notas legales",
                "ACTIVE",
                null,
                Instant.now()
        );
    }

    private Consultorio inactiveConsultorio() {
        Consultorio consultorio = activeConsultorio();
        consultorio.inactivate();
        return consultorio;
    }

    private User activeUser() {
        return new User(USER_ID, ADMIN_EMAIL, "hash", "Admin", "User",
                null, UserStatus.ACTIVE, Instant.now());
    }

    @Test
    void list_asAdmin_returnsAll() {
        when(consultorioRepo.findAll()).thenReturn(List.of(activeConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Consultorio Test");
        verify(consultorioRepo).findAll();
        verify(consultorioRepo, never()).findConsultorioIdsByUserId(any());
    }

    @Test
    void list_asProfAdmin_returnsOwn() {
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(consultorioRepo.findByIds(List.of(CONSULTORIO_ID))).thenReturn(List.of(activeConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, PROF_ADMIN_ROLES);

        assertThat(result).hasSize(1);
    }

    @Test
    void list_asProfAdmin_excludesInactive() {
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(consultorioRepo.findByIds(List.of(CONSULTORIO_ID))).thenReturn(List.of(inactiveConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, PROF_ADMIN_ROLES);

        assertThat(result).isEmpty();
    }

    @Test
    void getById_asAdmin_returnsConsultorio() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));

        ConsultorioResult result = service.getById(CONSULTORIO_ID, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.id()).isEqualTo(CONSULTORIO_ID);
    }

    @Test
    void getById_notFound_throwsConsultorioNotFoundException() {
        when(consultorioRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(UUID.randomUUID(), ADMIN_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioNotFoundException.class);
    }

    @Test
    void getById_noMembership_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getById(CONSULTORIO_ID, ADMIN_EMAIL, PROF_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void create_asAdmin_succeeds() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateConsultorioCommand cmd = quickCreateCommand();
        ConsultorioResult result = service.create(cmd, ADMIN_ROLES);

        assertThat(result.name()).isEqualTo("Nuevo");
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(cargoBootstrapService).ensureDefaults();
        verify(especialidadBootstrapService).enableDefaultsForConsultorio(result.id());
        verify(antecedenteBootstrapService).ensureDefaults(result.id(), "system");
    }

    @Test
    void create_withExtendedData_persistsCoordinatesAndDocumentSetup() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultorioResult result = service.create(completeCreateCommand(), ADMIN_ROLES);

        assertThat(result.mapLatitude()).isEqualByComparingTo("-34.603722");
        assertThat(result.mapLongitude()).isEqualByComparingTo("-58.381592");
        assertThat(result.geoAddress()).isEqualTo("Av. 123, Buenos Aires, Argentina");
        assertThat(result.documentShowAddress()).isTrue();
        assertThat(result.licenseNumber()).isEqualTo("HAB-01");
        assertThat(result.professionalDirectorName()).isEqualTo("Dra. Perez");
    }

    @Test
    void create_whenAntecedenteBootstrapFails_throwsAndStopsFlow() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new IllegalStateException("bootstrap fail"))
                .when(antecedenteBootstrapService)
                .ensureDefaults(any(), any());

        assertThatThrownBy(() -> service.create(quickCreateCommand(), ADMIN_ROLES))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bootstrap fail");
    }

    @Test
    void create_asProfAdmin_throwsAccessDenied() {
        assertThatThrownBy(() -> service.create(quickCreateCommand(), PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_asAdmin_succeeds() {
        Consultorio consultorio = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultorioResult result = service.update(updateCommand("ACTIVE"), ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.name()).isEqualTo("Actualizado");
        assertThat(result.description()).isEqualTo("Nueva descripcion");
    }

    @Test
    void update_withExtendedData_updatesStatusAndDocumentFlags() {
        Consultorio consultorio = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultorioResult result = service.update(updateCommand("INACTIVE"), ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.status()).isEqualTo("INACTIVE");
        assertThat(result.geoAddress()).isEqualTo("Nueva direccion georreferenciada");
        assertThat(result.documentShowCuit()).isTrue();
        assertThat(result.licenseType()).isEqualTo("Provincial");
    }

    @Test
    void update_inactive_throwsConsultorioInactiveException() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(inactiveConsultorio()));

        assertThatThrownBy(() -> service.update(updateCommand("ACTIVE"), ADMIN_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioInactiveException.class);
    }

    @Test
    void inactivate_asAdmin_setsInactive() {
        Consultorio consultorio = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.inactivate(CONSULTORIO_ID, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(consultorio.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void inactivate_asProfAdmin_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));

        assertThatThrownBy(() -> service.inactivate(CONSULTORIO_ID, ADMIN_EMAIL, PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void activate_asAdmin_setsActive() {
        Consultorio consultorio = inactiveConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultorioResult result = service.activate(CONSULTORIO_ID, ADMIN_ROLES);

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void activate_asProfAdmin_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(inactiveConsultorio()));

        assertThatThrownBy(() -> service.activate(CONSULTORIO_ID, PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    private CreateConsultorioCommand quickCreateCommand() {
        return new CreateConsultorioCommand(
                "Nuevo", null, null, null, null, "Av. 123", null, null, null, "1155550000", "nuevo@mail.com",
                null, null, new BigDecimal("-34.603722"), new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592",
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, "ACTIVE"
        );
    }

    private CreateConsultorioCommand completeCreateCommand() {
        return new CreateConsultorioCommand(
                "Nuevo", "Descripcion", "https://cdn.test/logo.png", "20123456789", "Clinica Nuevo SA",
                "Av. 123", "Av. 123, Buenos Aires, Argentina", "Puerta lateral", "Piso 2", "1155550000", "nuevo@mail.com", "Laura Admin",
                "Observaciones internas", new BigDecimal("-34.603722"), new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592", "Clinica Nuevo", "Kinesiologia",
                "https://cdn.test/doc-logo.png", "Pie documental", true, true, true, false, true,
                true, "HAB-01", "Municipal", LocalDate.of(2027, 3, 1), "Dra. Perez", "MP-123",
                "Contrato base", "Notas legales", "ACTIVE"
        );
    }

    private UpdateConsultorioCommand updateCommand(String status) {
        return new UpdateConsultorioCommand(
                CONSULTORIO_ID, "Actualizado", "Nueva descripcion", null, "20999999999",
                "Clinica Actualizada SA", "Nueva direccion", "Nueva direccion georreferenciada", "Ingreso principal", "Unidad B",
                "1155559999", "actualizado@mail.com", "Nuevo responsable", "Notas internas",
                new BigDecimal("-34.603722"), new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592", "Nombre documentos", "Subtitulo",
                null, "Pie actualizado", true, true, true, true, true, false, "HAB-02",
                "Provincial", LocalDate.of(2028, 1, 10), "Dr. Lopez", "MP-222", "Comodato",
                "Notas legales", status
        );
    }
}
