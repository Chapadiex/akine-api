CREATE TABLE IF NOT EXISTS historia_clinica_sesiones (
    id                  UUID         NOT NULL PRIMARY KEY,
    consultorio_id      UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id         UUID         NOT NULL REFERENCES pacientes(id),
    profesional_id      UUID         NOT NULL REFERENCES profesionales(id),
    turno_id            UUID                  REFERENCES turnos(id),
    box_id              UUID                  REFERENCES boxes(id),
    fecha_atencion      TIMESTAMP    NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    tipo_atencion       VARCHAR(30)  NOT NULL,
    motivo_consulta     VARCHAR(500),
    resumen_clinico     VARCHAR(1000),
    subjetivo           VARCHAR(2000),
    objetivo            VARCHAR(2000),
    evaluacion          VARCHAR(2000),
    plan                VARCHAR(2000),
    origen_registro     VARCHAR(30)  NOT NULL,
    created_by_user_id  UUID,
    updated_by_user_id  UUID,
    closed_by_user_id   UUID,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    CONSTRAINT uq_historia_clinica_sesiones_turno UNIQUE (turno_id)
);

CREATE INDEX idx_hc_sesiones_consultorio_fecha
    ON historia_clinica_sesiones (consultorio_id, fecha_atencion DESC);
CREATE INDEX idx_hc_sesiones_paciente_fecha
    ON historia_clinica_sesiones (paciente_id, fecha_atencion DESC);
CREATE INDEX idx_hc_sesiones_profesional_fecha
    ON historia_clinica_sesiones (profesional_id, fecha_atencion DESC);
CREATE INDEX idx_hc_sesiones_estado
    ON historia_clinica_sesiones (consultorio_id, estado);

CREATE TABLE IF NOT EXISTS historia_clinica_diagnosticos (
    id                  UUID         NOT NULL PRIMARY KEY,
    consultorio_id      UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id         UUID         NOT NULL REFERENCES pacientes(id),
    profesional_id      UUID         NOT NULL REFERENCES profesionales(id),
    sesion_id           UUID                  REFERENCES historia_clinica_sesiones(id),
    codigo              VARCHAR(100),
    descripcion         VARCHAR(500) NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    fecha_inicio        DATE         NOT NULL,
    fecha_fin           DATE,
    notas               VARCHAR(1000),
    created_by_user_id  UUID,
    updated_by_user_id  UUID,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hc_diagnosticos_consultorio_paciente
    ON historia_clinica_diagnosticos (consultorio_id, paciente_id);
CREATE INDEX idx_hc_diagnosticos_estado
    ON historia_clinica_diagnosticos (consultorio_id, estado);
CREATE INDEX idx_hc_diagnosticos_sesion
    ON historia_clinica_diagnosticos (sesion_id);

CREATE TABLE IF NOT EXISTS historia_clinica_adjuntos (
    id                  UUID         NOT NULL PRIMARY KEY,
    consultorio_id      UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id         UUID         NOT NULL REFERENCES pacientes(id),
    sesion_id           UUID         NOT NULL REFERENCES historia_clinica_sesiones(id),
    storage_key         VARCHAR(500) NOT NULL,
    original_filename   VARCHAR(255) NOT NULL,
    content_type        VARCHAR(120) NOT NULL,
    size_bytes          BIGINT       NOT NULL,
    created_by_user_id  UUID,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_adjuntos_storage_key UNIQUE (storage_key)
);

CREATE INDEX idx_hc_adjuntos_sesion
    ON historia_clinica_adjuntos (sesion_id, created_at ASC);
