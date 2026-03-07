package com.akine_api.service;

import com.akine_api.application.dto.result.EspecialidadResult;
import com.akine_api.application.port.output.ConsultorioEspecialidadRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.EspecialidadCatalogoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioEspecialidadService;
import com.akine_api.domain.exception.ConsultorioInactiveException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.ConsultorioEspecialidad;
import com.akine_api.domain.model.EspecialidadCatalogo;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioEspecialidadServiceTest {

    @Mock ConsultorioEspecialidadRepositoryPort consultorioEspecialidadRepo;
    @Mock EspecialidadCatalogoRepositoryPort especialidadCatalogoRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    private ConsultorioEspecialidadService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "admin@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROF_ADMIN_ROLES = Set.of("ROLE_PROFESIONAL_ADMIN");
    private static final Set<String> PROF_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new ConsultorioEspecialidadService(
                consultorioEspecialidadRepo,
                especialidadCatalogoRepo,
                consultorioRepo,
                userRepo
        );
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    @Test
    void createOrLink_whenCatalogDoesNotExist_createsAndLinks() {
        when(especialidadCatalogoRepo.findBySlug("rehabilitacion-vestibular")).thenReturn(Optional.empty());
        when(especialidadCatalogoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(any(), any())).thenReturn(Optional.empty());
        when(consultorioEspecialidadRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EspecialidadResult result = service.createOrLink(
                CONSULTORIO_ID,
                "Rehabilitacion vestibular",
                USER_EMAIL,
                ADMIN_ROLES
        );

        assertThat(result.nombre()).isEqualTo("Rehabilitacion vestibular");
        assertThat(result.slug()).isEqualTo("rehabilitacion-vestibular");
        assertThat(result.activo()).isTrue();
    }

    @Test
    void createOrLink_whenLinkExistsInactive_reactivates() {
        UUID especialidadId = UUID.randomUUID();
        EspecialidadCatalogo catalog = new EspecialidadCatalogo(especialidadId, "Kinesiologia", "kinesiologia", true, Instant.now());
        ConsultorioEspecialidad relation = new ConsultorioEspecialidad(UUID.randomUUID(), CONSULTORIO_ID, especialidadId, false, Instant.now());

        when(especialidadCatalogoRepo.findBySlug("kinesiologia")).thenReturn(Optional.of(catalog));
        when(consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(CONSULTORIO_ID, especialidadId))
                .thenReturn(Optional.of(relation));
        when(consultorioEspecialidadRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EspecialidadResult result = service.createOrLink(
                CONSULTORIO_ID,
                "Kinesiologia",
                USER_EMAIL,
                ADMIN_ROLES
        );

        assertThat(result.activo()).isTrue();
    }

    @Test
    void list_withSearch_returnsFilteredRows() {
        UUID especialidadId = UUID.randomUUID();
        ConsultorioEspecialidad relation = new ConsultorioEspecialidad(UUID.randomUUID(), CONSULTORIO_ID, especialidadId, true, Instant.now());
        EspecialidadCatalogo catalog = new EspecialidadCatalogo(especialidadId, "Kinesiologia", "kinesiologia", true, Instant.now());

        when(consultorioEspecialidadRepo.findByConsultorioIdAndNombreContaining(CONSULTORIO_ID, "kine", false))
                .thenReturn(List.of(relation));
        when(especialidadCatalogoRepo.findByIds(List.of(especialidadId))).thenReturn(List.of(catalog));

        List<EspecialidadResult> result = service.list(
                CONSULTORIO_ID,
                "kine",
                false,
                USER_EMAIL,
                ADMIN_ROLES
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Kinesiologia");
    }

    @Test
    void update_whenNameChanges_updatesCatalogNameAndSlug() {
        UUID especialidadId = UUID.randomUUID();
        ConsultorioEspecialidad relation = new ConsultorioEspecialidad(UUID.randomUUID(), CONSULTORIO_ID, especialidadId, true, Instant.now());
        EspecialidadCatalogo catalog = new EspecialidadCatalogo(especialidadId, "Kinesiologia", "kinesiologia", true, Instant.now());

        when(consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(CONSULTORIO_ID, especialidadId))
                .thenReturn(Optional.of(relation));
        when(especialidadCatalogoRepo.findById(especialidadId)).thenReturn(Optional.of(catalog));
        when(especialidadCatalogoRepo.findBySlug("fisiatria")).thenReturn(Optional.empty());
        when(especialidadCatalogoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EspecialidadResult result = service.update(
                CONSULTORIO_ID,
                especialidadId,
                "Fisiatria",
                USER_EMAIL,
                ADMIN_ROLES
        );

        assertThat(result.nombre()).isEqualTo("Fisiatria");
        assertThat(result.slug()).isEqualTo("fisiatria");
    }

    @Test
    void update_whenSlugAlreadyExists_throwsIllegalArgumentException() {
        UUID especialidadId = UUID.randomUUID();
        UUID existingId = UUID.randomUUID();
        ConsultorioEspecialidad relation = new ConsultorioEspecialidad(UUID.randomUUID(), CONSULTORIO_ID, especialidadId, true, Instant.now());
        EspecialidadCatalogo catalog = new EspecialidadCatalogo(especialidadId, "Kinesiologia", "kinesiologia", true, Instant.now());
        EspecialidadCatalogo existing = new EspecialidadCatalogo(existingId, "Fisiatria", "fisiatria", true, Instant.now());

        when(consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(CONSULTORIO_ID, especialidadId))
                .thenReturn(Optional.of(relation));
        when(especialidadCatalogoRepo.findById(especialidadId)).thenReturn(Optional.of(catalog));
        when(especialidadCatalogoRepo.findBySlug("fisiatria")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(
                CONSULTORIO_ID,
                especialidadId,
                "Fisiatria",
                USER_EMAIL,
                ADMIN_ROLES
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe una especialidad");
    }

    @Test
    void list_withoutMembership_throwsAccessDenied() {
        when(userRepo.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(new User(USER_ID, USER_EMAIL, "x", "A", "B", null, UserStatus.ACTIVE, Instant.now())));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, null, false, USER_EMAIL, PROF_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void createOrLink_asProfesional_withoutAdminRole_throwsAccessDenied() {
        when(userRepo.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(new User(USER_ID, USER_EMAIL, "x", "A", "B", null, UserStatus.ACTIVE, Instant.now())));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));

        assertThatThrownBy(() -> service.createOrLink(CONSULTORIO_ID, "Kinesiologia", USER_EMAIL, PROF_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void createOrLink_asProfesionalAdmin_withMembership_succeeds() {
        when(userRepo.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(new User(USER_ID, USER_EMAIL, "x", "A", "B", null, UserStatus.ACTIVE, Instant.now())));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(especialidadCatalogoRepo.findBySlug("fisioterapia")).thenReturn(Optional.empty());
        when(especialidadCatalogoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consultorioEspecialidadRepo.findByConsultorioIdAndEspecialidadId(any(), any())).thenReturn(Optional.empty());
        when(consultorioEspecialidadRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EspecialidadResult result = service.createOrLink(CONSULTORIO_ID, "Fisioterapia", USER_EMAIL, PROF_ADMIN_ROLES);
        assertThat(result.nombre()).isEqualTo("Fisioterapia");
    }

    @Test
    void list_whenConsultorioInactive_throwsConsultorioInactiveException() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "INACTIVE", Instant.now())));

        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, null, false, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioInactiveException.class);
    }
}
