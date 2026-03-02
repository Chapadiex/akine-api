package com.akine_api.domain.model;

import java.util.Set;
import java.util.UUID;

public class ConsultorioDuracionTurno {

    private static final Set<Integer> MINUTOS_VALIDOS = Set.of(15, 20, 30, 45, 60);

    private final UUID id;
    private final UUID consultorioId;
    private final int minutos;

    public ConsultorioDuracionTurno(UUID id, UUID consultorioId, int minutos) {
        if (!MINUTOS_VALIDOS.contains(minutos)) {
            throw new IllegalArgumentException("La duracion debe ser una de 15, 20, 30, 45 o 60 minutos");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.minutos = minutos;
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public int getMinutos() { return minutos; }
}
