-- Fase 0: Configuración por consultorio para operatoria de cobro
-- Permite personalizar políticas de no-show, alertas, numeración de recibos

CREATE TABLE cobro_configuracion_consultorio (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL UNIQUE REFERENCES consultorios(id),

    -- Política para turnos no presentados
    politica_no_show VARCHAR(30) NOT NULL DEFAULT 'NO_COBRAR',
    no_show_horas_aviso INTEGER,

    -- Alertas operativas
    alerta_sesion_sin_cierre_horas INTEGER NOT NULL DEFAULT 24,

    -- Numeración de recibos (con soporte de variables: {year}, {month}, {seq:N})
    formato_numeracion_recibo VARCHAR(50) NOT NULL DEFAULT 'REC-{year}-{seq:06}',

    -- Caja múltiple (por turno)
    habilitar_multiples_cajas BOOLEAN NOT NULL DEFAULT FALSE,

    -- Moneda operativa
    moneda_default VARCHAR(5) NOT NULL DEFAULT 'ARS',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_cobro_config_consultorio ON cobro_configuracion_consultorio(consultorio_id);
