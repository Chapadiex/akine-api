package com.akine_api.service;

import com.akine_api.application.dto.command.SetHorarioConsultorioCommand;
import com.akine_api.application.port.output.ConsultorioHorarioRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioHorarioService;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.Consultorio;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioHorarioServiceTest {

    @Mock ConsultorioHorarioRepositoryPort horarioRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    ConsultorioHorarioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "admin@test.com";

    @BeforeEach
    void setUp() {
        service = new ConsultorioHorarioService(horarioRepo, consultorioRepo, userRepo);
    }

    @Test
    void set_asAdmin_ok() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY)).thenReturn(List.of());
        when(horarioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.add(new SetHorarioConsultorioCommand(
                CONSULTORIO_ID, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)
        ), USER_EMAIL, Set.of("ROLE_ADMIN"));

        assertThat(result.diaSemana()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(result.horaApertura()).isEqualTo(LocalTime.of(8, 0));
    }

    @Test
    void set_horaInvalida_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));

        assertThatThrownBy(() -> service.set(new SetHorarioConsultorioCommand(
                CONSULTORIO_ID, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(9, 0)
        ), USER_EMAIL, Set.of("ROLE_ADMIN"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void list_consultorioNotFound_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ConsultorioNotFoundException.class);
    }
}
