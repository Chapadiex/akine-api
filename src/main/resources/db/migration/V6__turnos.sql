-- Tabla de turnos (citas)
CREATE TABLE IF NOT EXISTS turnos (
    id                  UUID         NOT NULL PRIMARY KEY,
    consultorio_id      UUID         NOT NULL REFERENCES consultorios(id),
    profesional_id      UUID         NOT NULL REFERENCES profesionales(id),
    box_id              UUID                  REFERENCES boxes(id),
    paciente_id         UUID,
    fecha_hora_inicio   TIMESTAMP    NOT NULL,
    duracion_minutos    INTEGER      NOT NULL,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'PROGRAMADO',
    motivo_consulta     VARCHAR(500),
    notas               VARCHAR(1000),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_turnos_consultorio_fecha ON turnos(consultorio_id, fecha_hora_inicio);
CREATE INDEX idx_turnos_profesional_fecha ON turnos(profesional_id, fecha_hora_inicio);
CREATE INDEX idx_turnos_box_fecha         ON turnos(box_id, fecha_hora_inicio);
CREATE INDEX idx_turnos_estado            ON turnos(consultorio_id, estado);
