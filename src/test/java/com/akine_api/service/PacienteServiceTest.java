package com.akine_api.service;

import com.akine_api.application.dto.command.CreateMyPacienteCommand;
import com.akine_api.application.dto.command.CreatePacienteAdminCommand;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.PacienteService;
import com.akine_api.application.service.PlanGateService;
import com.akine_api.domain.exception.PacienteDuplicadoException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.PacienteConsultorio;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock PacienteRepositoryPort pacienteRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock PlanGateService planGateService;

    PacienteService service;

    private static final String EMAIL = "administrativo@test.com";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new PacienteService(pacienteRepo, pacienteConsultorioRepo, userRepo, consultorioRepo, planGateService);
    }

    @Test
    void createMe_ok() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(pacienteRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(pacienteRepo.findByDni("30111222")).thenReturn(Optional.empty());
        when(pacienteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.createMe(
                new CreateMyPacienteCommand(
                        "30111222", "Ana", "Perez", "1155554444",
                        "ana@mail.com", LocalDate.of(1990, 1, 1),
                        null, null, null, null, null,
                        null, null, null
                ),
                EMAIL,
                Set.of("ROLE_PACIENTE")
        );

        assertThat(result.dni()).isEqualTo("30111222");
        assertThat(result.userId()).isEqualTo(USER_ID);
    }

    @Test
    void createMe_duplicateDni_throws409() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(pacienteRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(pacienteRepo.findByDni("30111222")).thenReturn(Optional.of(samplePaciente()));

        assertThatThrownBy(() -> service.createMe(
                new CreateMyPacienteCommand(
                        "30111222", "Ana", "Perez", "1155554444",
                        null, null, null, null, null, null, null, null, null, null
                ),
                EMAIL,
                Set.of("ROLE_PACIENTE")
        )).isInstanceOf(PacienteDuplicadoException.class);
    }

    @Test
    void createAdmin_profesionalRole_forbidden() {
        assertThatThrownBy(() -> service.createAdmin(
                CONSULTORIO_ID,
                new CreatePacienteAdminCommand(
                        "30111222", "Ana", "Perez", "1155554444",
                        null, null, null, null, null, null, null, null, null, null
                ),
                EMAIL,
                Set.of("ROLE_PROFESIONAL")
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void createAdmin_existingDni_linksConsultorio() {
        Paciente existing = samplePaciente();

        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(sampleConsultorio()));
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(pacienteRepo.findByDni("30111222")).thenReturn(Optional.of(existing));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(existing.getId(), CONSULTORIO_ID))
                .thenReturn(false);
        when(pacienteConsultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.createAdmin(
                CONSULTORIO_ID,
                new CreatePacienteAdminCommand(
                        "30111222", "Ana", "Perez", "1155554444",
                        null, null, null, null, null, null, null, null, null, null
                ),
                EMAIL,
                Set.of("ROLE_ADMINISTRATIVO")
        );

        assertThat(result.id()).isEqualTo(existing.getId());
        verify(pacienteConsultorioRepo).save(any(PacienteConsultorio.class));
    }

    @Test
    void search_profesionalRole_canSearchByDniWithinConsultorio() {
        Paciente existing = samplePaciente();

        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(sampleConsultorio()));
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(pacienteRepo.findByDni("30111222")).thenReturn(Optional.of(existing));
        when(pacienteConsultorioRepo.findPacienteIdsByConsultorioIdAndPacienteIds(CONSULTORIO_ID, List.of(existing.getId())))
                .thenReturn(List.of(existing.getId()));

        var result = service.search(
                CONSULTORIO_ID,
                "30111222",
                null,
                EMAIL,
                Set.of("ROLE_PROFESIONAL")
        );

        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.id()).isEqualTo(existing.getId());
            assertThat(item.linkedToConsultorio()).isTrue();
        });
    }

    private User activeUser() {
        return new User(USER_ID, EMAIL, "hash", "Admin", "Istrativo", null, UserStatus.ACTIVE, Instant.now());
    }

    private Consultorio sampleConsultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Paciente samplePaciente() {
        Instant now = Instant.now();
        return new Paciente(
                UUID.randomUUID(),
                "30111222",
                "Ana",
                "Perez",
                "1155554444",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                now,
                now
        );
    }
}
