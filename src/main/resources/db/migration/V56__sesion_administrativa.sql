-- Fase 2: Sesión administrativa — datos de cobertura, documentación y check-in
-- Separación entre la sesión clínica (profesional) y la administrativa (cobertura, autorización)

CREATE TABLE sesion_administrativa (
    id UUID PRIMARY KEY,
    sesion_id UUID NOT NULL UNIQUE REFERENCES historia_clinica_sesiones(id),
    turno_id UUID REFERENCES turnos(id),

    -- Cobertura
    cobertura_tipo VARCHAR(30) NOT NULL DEFAULT 'PARTICULAR',  -- PARTICULAR, OBRA_SOCIAL, PREPAGA, MUTUAL
    financiador_id UUID REFERENCES coverage_financiador_salud(id),
    plan_id UUID REFERENCES coverage_plan_financiador(id),
    numero_afiliado VARCHAR(100),

    -- Primera consulta
    es_primera_consulta BOOLEAN NOT NULL DEFAULT FALSE,

    -- Pedido médico
    tiene_pedido_medico BOOLEAN NOT NULL DEFAULT FALSE,
    pedido_medico_numero VARCHAR(50),
    pedido_medico_fecha DATE,

    -- Orden
    tiene_orden BOOLEAN NOT NULL DEFAULT FALSE,
    orden_numero VARCHAR(50),
    orden_fecha DATE,

    -- Autorización
    tiene_autorizacion BOOLEAN NOT NULL DEFAULT FALSE,
    autorizacion_numero VARCHAR(50),
    autorizacion_vencimiento DATE,

    -- Estado de asistencia y documentación
    asistencia_confirmada BOOLEAN NOT NULL DEFAULT FALSE,
    documentacion_completa BOOLEAN NOT NULL DEFAULT FALSE,
    documentacion_faltante VARCHAR(2000),               -- JSON array como texto: ["pedido_medico","orden"]

    -- Validación de cobertura
    validacion_cobertura_estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',  -- PENDIENTE, VALIDADA, RECHAZADA, NO_APLICA
    validacion_cobertura_obs TEXT,

    -- Flags de facturación (calculados en Fase 3)
    es_facturable_os BOOLEAN NOT NULL DEFAULT FALSE,
    requiere_regularizacion BOOLEAN NOT NULL DEFAULT FALSE,
    observaciones_admin TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_sesion_adm_sesion ON sesion_administrativa(sesion_id);
CREATE INDEX IF NOT EXISTS idx_sesion_adm_turno ON sesion_administrativa(turno_id);
