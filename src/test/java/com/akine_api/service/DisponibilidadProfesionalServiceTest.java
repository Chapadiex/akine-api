package com.akine_api.service;

import com.akine_api.application.dto.command.CreateDisponibilidadCommand;
import com.akine_api.application.port.output.ConsultorioHorarioRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DisponibilidadProfesionalRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.DisponibilidadProfesionalService;
import com.akine_api.domain.exception.DisponibilidadFueraDeHorarioException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.ConsultorioHorario;
import com.akine_api.domain.model.DisponibilidadProfesional;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisponibilidadProfesionalServiceTest {

    @Mock DisponibilidadProfesionalRepositoryPort disponibilidadRepo;
    @Mock ConsultorioHorarioRepositoryPort horarioRepo;
    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    DisponibilidadProfesionalService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new DisponibilidadProfesionalService(
                disponibilidadRepo, horarioRepo, profesionalConsultorioRepo, profesionalRepo, consultorioRepo, userRepo);
    }

    @Test
    void create_fueraHorario_throws422Domain() {
        when(profesionalRepo.findById(PROFESIONAL_ID))
                .thenReturn(Optional.of(new Profesional(PROFESIONAL_ID, CONSULTORIO_ID, "N", "A", "M", null, null, null, true, Instant.now())));
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(Optional.of(new ProfesionalConsultorio(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, true, Instant.now())));
        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new ConsultorioHorario(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(8, 0), LocalTime.of(18, 0), true)));

        assertThatThrownBy(() -> service.create(new CreateDisponibilidadCommand(
                        PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(9, 0)),
                "a", Set.of("ROLE_ADMIN")))
                .isInstanceOf(DisponibilidadFueraDeHorarioException.class);
    }

    @Test
    void create_solapado_throws() {
        when(profesionalRepo.findById(PROFESIONAL_ID))
                .thenReturn(Optional.of(new Profesional(PROFESIONAL_ID, CONSULTORIO_ID, "N", "A", "M", null, null, null, true, Instant.now())));
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(Optional.of(new ProfesionalConsultorio(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, true, Instant.now())));
        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new ConsultorioHorario(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(8, 0), LocalTime.of(18, 0), true)));
        when(disponibilidadRepo.findByProfesionalIdAndConsultorioIdAndDiaSemana(PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new DisponibilidadProfesional(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(9, 0), LocalTime.of(13, 0), true)));

        assertThatThrownBy(() -> service.create(new CreateDisponibilidadCommand(
                        PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(14, 0)),
                "a", Set.of("ROLE_ADMIN")))
                .isInstanceOf(com.akine_api.domain.exception.DisponibilidadSolapamientoException.class);
    }
}
