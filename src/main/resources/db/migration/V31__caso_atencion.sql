-- V31: Introduce caso_atencion como entidad episódica del dominio clínico
-- Reemplaza el rol que AtencionInicial cumplía de forma parcial e incorrecta.
-- Los AtencionInicial existentes se migran a CasoAtencion conservando sus datos.
-- Las columnas caso_atencion_id en sesiones/diagnosticos/turnos se agregan nullable
-- y se hacen NOT NULL en una migración posterior (V32) una vez que todo el código esté ajustado.

-- ─── 1. Tabla principal ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS caso_atencion (
    id                          UUID         NOT NULL PRIMARY KEY,
    legajo_id                   UUID         NOT NULL REFERENCES historia_clinica_legajos(id),
    consultorio_id              UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id                 UUID         NOT NULL REFERENCES pacientes(id),
    profesional_responsable_id  UUID         REFERENCES profesionales(id),
    tipo_origen                 VARCHAR(40)  NOT NULL DEFAULT 'CONSULTA_DIRECTA',
    fecha_apertura              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo_consulta             VARCHAR(500),
    diagnostico_medico          VARCHAR(500),
    diagnostico_funcional       VARCHAR(500),
    afeccion_principal          VARCHAR(255),
    cobertura_id                UUID,
    estado                      VARCHAR(40)  NOT NULL DEFAULT 'BORRADOR',
    prioridad                   VARCHAR(20)  NOT NULL DEFAULT 'NORMAL',
    atencion_inicial_id         UUID         REFERENCES historia_clinica_atenciones_iniciales(id),
    created_by_user_id          UUID,
    updated_by_user_id          UUID,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_caso_atencion_legajo
    ON caso_atencion (legajo_id, created_at DESC);
CREATE INDEX idx_caso_atencion_paciente
    ON caso_atencion (consultorio_id, paciente_id, estado);
CREATE INDEX idx_caso_atencion_estado
    ON caso_atencion (consultorio_id, estado);

-- ─── 2. Migrar AtencionInicial existente → un CasoAtencion por cada registro ──
-- gen_random_uuid() compatible con PostgreSQL y H2 2.x en MODE=PostgreSQL
INSERT INTO caso_atencion (
    id,
    legajo_id,
    consultorio_id,
    paciente_id,
    profesional_responsable_id,
    tipo_origen,
    fecha_apertura,
    motivo_consulta,
    diagnostico_medico,
    estado,
    atencion_inicial_id,
    created_by_user_id,
    updated_by_user_id,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    ai.legajo_id,
    ai.consultorio_id,
    ai.paciente_id,
    ai.profesional_id,
    ai.tipo_ingreso,
    ai.fecha_hora,
    ai.motivo_consulta_breve,
    ai.diagnostico_nombre,
    'ACTIVO',
    ai.id,
    ai.created_by_user_id,
    ai.updated_by_user_id,
    ai.created_at,
    ai.updated_at
FROM historia_clinica_atenciones_iniciales ai;

-- ─── 3. FK nullable en sesiones ───────────────────────────────────────────────
ALTER TABLE historia_clinica_sesiones
    ADD COLUMN IF NOT EXISTS caso_atencion_id UUID REFERENCES caso_atencion(id);

CREATE INDEX idx_hc_sesiones_caso
    ON historia_clinica_sesiones (caso_atencion_id);

-- ─── 4. FK nullable en diagnosticos ──────────────────────────────────────────
ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS caso_atencion_id UUID REFERENCES caso_atencion(id);

CREATE INDEX idx_hc_diagnosticos_caso
    ON historia_clinica_diagnosticos (caso_atencion_id);

-- ─── 5. FK nullable en turnos ────────────────────────────────────────────────
ALTER TABLE turnos
    ADD COLUMN IF NOT EXISTS caso_atencion_id UUID REFERENCES caso_atencion(id);

CREATE INDEX idx_turnos_caso_atencion
    ON turnos (caso_atencion_id);

-- ─── 6. FK caso_atencion_id en plan terapeutico + migración de datos ─────────
ALTER TABLE historia_clinica_planes_terapeuticos
    ADD COLUMN IF NOT EXISTS caso_atencion_id UUID REFERENCES caso_atencion(id);

UPDATE historia_clinica_planes_terapeuticos pt
SET caso_atencion_id = ca.id
FROM caso_atencion ca
WHERE ca.atencion_inicial_id = pt.atencion_inicial_id;

CREATE INDEX idx_hc_plan_caso_atencion
    ON historia_clinica_planes_terapeuticos (caso_atencion_id);
