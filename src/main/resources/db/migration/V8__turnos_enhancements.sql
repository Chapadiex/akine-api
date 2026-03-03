-- =============================================
-- V8: Mejoras al módulo de turnos
-- - Nuevos campos en turnos (tipo consulta, tracking, cancelación)
-- - Historial de estados
-- - Feriados por consultorio
-- =============================================

-- 1. Nuevos campos en tabla turnos
ALTER TABLE turnos ADD COLUMN tipo_consulta VARCHAR(20) DEFAULT 'PARTICULAR';
ALTER TABLE turnos ADD COLUMN telefono_contacto VARCHAR(50);
ALTER TABLE turnos ADD COLUMN creado_por_user_id UUID;
ALTER TABLE turnos ADD COLUMN motivo_cancelacion VARCHAR(500);
ALTER TABLE turnos ADD COLUMN cancelado_por_user_id UUID;

-- 2. Historial de estados de turno
CREATE TABLE IF NOT EXISTS historial_estado_turno (
    id                    UUID         NOT NULL PRIMARY KEY,
    turno_id              UUID         NOT NULL REFERENCES turnos(id),
    estado_anterior       VARCHAR(20),
    estado_nuevo          VARCHAR(20)  NOT NULL,
    cambiado_por_user_id  UUID,
    motivo                VARCHAR(500),
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_historial_turno_id ON historial_estado_turno(turno_id, created_at);

-- 3. Feriados por consultorio
CREATE TABLE IF NOT EXISTS consultorio_feriados (
    id              UUID         NOT NULL PRIMARY KEY,
    consultorio_id  UUID         NOT NULL REFERENCES consultorios(id),
    fecha           DATE         NOT NULL,
    descripcion     VARCHAR(200),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (consultorio_id, fecha)
);

CREATE INDEX idx_feriados_consultorio ON consultorio_feriados(consultorio_id, fecha);
