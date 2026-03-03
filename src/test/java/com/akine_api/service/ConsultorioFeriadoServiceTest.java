package com.akine_api.service;

import com.akine_api.application.dto.command.CreateFeriadoCommand;
import com.akine_api.application.dto.result.ConsultorioFeriadoResult;
import com.akine_api.application.port.output.ConsultorioFeriadoRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioFeriadoService;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.ConsultorioFeriado;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioFeriadoServiceTest {

    @Mock ConsultorioFeriadoRepositoryPort feriadoRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    ConsultorioFeriadoService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        service = new ConsultorioFeriadoService(feriadoRepo, consultorioRepo, userRepo);
    }

    private void stubConsultorioExists() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    @Test
    void create_asAdmin_succeeds() {
        stubConsultorioExists();
        when(feriadoRepo.existsByConsultorioIdAndFecha(any(), any())).thenReturn(false);
        when(feriadoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDate fecha = LocalDate.of(2026, 5, 1);
        ConsultorioFeriadoResult result = service.create(
                new CreateFeriadoCommand(CONSULTORIO_ID, fecha, "Día del Trabajador"),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result).isNotNull();
        assertThat(result.fecha()).isEqualTo(fecha);
        assertThat(result.descripcion()).isEqualTo("Día del Trabajador");
    }

    @Test
    void create_duplicateFecha_throws() {
        stubConsultorioExists();
        when(feriadoRepo.existsByConsultorioIdAndFecha(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.create(
                new CreateFeriadoCommand(CONSULTORIO_ID, LocalDate.of(2026, 5, 1), "Dup"),
                "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe");
    }

    @Test
    void create_asProfesional_throws() {
        stubConsultorioExists();

        assertThatThrownBy(() -> service.create(
                new CreateFeriadoCommand(CONSULTORIO_ID, LocalDate.of(2026, 5, 1), "Test"),
                "prof@test.com", Set.of("ROLE_PROFESIONAL")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void list_returnsResults() {
        stubConsultorioExists();
        ConsultorioFeriado feriado = new ConsultorioFeriado(UUID.randomUUID(), CONSULTORIO_ID,
                LocalDate.of(2026, 5, 1), "Test", Instant.now());
        when(feriadoRepo.findByConsultorioIdAndYear(CONSULTORIO_ID, 2026))
                .thenReturn(List.of(feriado));

        List<ConsultorioFeriadoResult> results = service.list(CONSULTORIO_ID, 2026,
                "admin@test.com", ADMIN_ROLES);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).fecha()).isEqualTo(LocalDate.of(2026, 5, 1));
    }

    @Test
    void delete_asAdmin_succeeds() {
        stubConsultorioExists();
        UUID feriadoId = UUID.randomUUID();
        when(feriadoRepo.findById(feriadoId))
                .thenReturn(Optional.of(new ConsultorioFeriado(feriadoId, CONSULTORIO_ID,
                        LocalDate.of(2026, 5, 1), "Test", Instant.now())));

        service.delete(CONSULTORIO_ID, feriadoId, "admin@test.com", ADMIN_ROLES);

        verify(feriadoRepo).deleteById(feriadoId);
    }
}
