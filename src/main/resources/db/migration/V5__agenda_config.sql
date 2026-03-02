-- 1. Box: capacidad
ALTER TABLE boxes ADD COLUMN IF NOT EXISTS capacity_type VARCHAR(20) NOT NULL DEFAULT 'UNLIMITED';
ALTER TABLE boxes ADD COLUMN IF NOT EXISTS capacity INTEGER;

-- 2. Profesional: consultorio_id ahora nullable (M2M)
ALTER TABLE profesionales ALTER COLUMN consultorio_id DROP NOT NULL;

-- 3. Junction table M2M
CREATE TABLE IF NOT EXISTS profesional_consultorios (
    id              UUID PRIMARY KEY,
    profesional_id  UUID NOT NULL REFERENCES profesionales(id),
    consultorio_id  UUID NOT NULL REFERENCES consultorios(id),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prof_consultorio UNIQUE (profesional_id, consultorio_id)
);

-- 4. Horarios del consultorio por dia
CREATE TABLE IF NOT EXISTS consultorio_horarios (
    id              UUID PRIMARY KEY,
    consultorio_id  UUID NOT NULL REFERENCES consultorios(id),
    dia_semana      VARCHAR(9) NOT NULL,
    hora_apertura   TIME NOT NULL,
    hora_cierre     TIME NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_consultorio_dia UNIQUE (consultorio_id, dia_semana)
);

-- 5. Duraciones de turno permitidas
CREATE TABLE IF NOT EXISTS consultorio_duracion_turno (
    id              UUID PRIMARY KEY,
    consultorio_id  UUID NOT NULL REFERENCES consultorios(id),
    minutos         INTEGER NOT NULL,
    CONSTRAINT uq_consultorio_minutos UNIQUE (consultorio_id, minutos)
);

-- 6. Disponibilidad por profesional
CREATE TABLE IF NOT EXISTS disponibilidad_profesional (
    id              UUID PRIMARY KEY,
    profesional_id  UUID NOT NULL REFERENCES profesionales(id),
    consultorio_id  UUID NOT NULL REFERENCES consultorios(id),
    dia_semana      VARCHAR(9) NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_prof_disp_dia UNIQUE (profesional_id, consultorio_id, dia_semana)
);

CREATE INDEX IF NOT EXISTS idx_prof_consultorios_prof ON profesional_consultorios(profesional_id);
CREATE INDEX IF NOT EXISTS idx_prof_consultorios_con ON profesional_consultorios(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_horarios_consultorio ON consultorio_horarios(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_duracion_consultorio ON consultorio_duracion_turno(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_disponibilidad_prof ON disponibilidad_profesional(profesional_id);
CREATE INDEX IF NOT EXISTS idx_disponibilidad_con ON disponibilidad_profesional(consultorio_id);
