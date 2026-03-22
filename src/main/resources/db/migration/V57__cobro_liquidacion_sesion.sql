-- Fase 3: Liquidación automática de sesiones clínicas
-- Motor económico central: calcula importe paciente / OS a partir del convenio vigente

CREATE TABLE cobro_liquidacion_sesion (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    sesion_id UUID NOT NULL REFERENCES historia_clinica_sesiones(id),
    paciente_id UUID NOT NULL,
    financiador_id UUID,
    plan_id UUID,
    convenio_id UUID,
    tipo_liquidacion VARCHAR(20) NOT NULL,        -- PARTICULAR, MIXTA, OS
    estado VARCHAR(40) NOT NULL,                  -- PENDIENTE_DE_LIQUIDAR, LIQUIDADA_PARTICULAR, LIQUIDADA_MIXTA, LIQUIDADA_OS, BLOQUEADA_POR_DOCUMENTACION, ANULADA
    motivo_bloqueo TEXT,
    valor_bruto DECIMAL(19,4) NOT NULL DEFAULT 0,
    descuento_importe DECIMAL(19,4) NOT NULL DEFAULT 0,
    descuento_porcentaje DECIMAL(5,2),
    copago_importe DECIMAL(19,4) NOT NULL DEFAULT 0,
    coseguro_importe DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_paciente DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_obra_social DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_total_liquidado DECIMAL(19,4) NOT NULL DEFAULT 0,
    documentacion_completa BOOLEAN NOT NULL DEFAULT FALSE,
    documentacion_obs TEXT,
    es_facturable_os BOOLEAN NOT NULL DEFAULT FALSE,
    requiere_revision_manual BOOLEAN NOT NULL DEFAULT FALSE,
    origen_tipo_cobro VARCHAR(30) NOT NULL,       -- AUTOMATICO, MANUAL_ADMINISTRATIVO, CONVERSION_PARTICULAR
    convenio_vigente_snapshot TEXT,               -- jsonb snapshot del convenio al liquidar
    recalculada_en TIMESTAMP,
    recalculada_por UUID,
    observaciones TEXT,
    liquidado_por UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Note: partial unique index not supported in H2; use plain index for dev.
-- In production (PostgreSQL), replace with: CREATE UNIQUE INDEX ... WHERE estado != 'ANULADA'
CREATE INDEX idx_liquidacion_sesion_id ON cobro_liquidacion_sesion(sesion_id);

CREATE INDEX idx_liquidacion_consultorio ON cobro_liquidacion_sesion(consultorio_id, estado);
CREATE INDEX idx_liquidacion_paciente ON cobro_liquidacion_sesion(paciente_id);

-- Vincular cobro_paciente con liquidacion_sesion (FK opcional: cobra puede existir sin liquidacion en flujo legacy)
ALTER TABLE cobro_cobro_paciente
    ADD COLUMN IF NOT EXISTS liquidacion_sesion_id UUID REFERENCES cobro_liquidacion_sesion(id);

CREATE INDEX IF NOT EXISTS idx_cobro_liquidacion ON cobro_cobro_paciente(liquidacion_sesion_id);
