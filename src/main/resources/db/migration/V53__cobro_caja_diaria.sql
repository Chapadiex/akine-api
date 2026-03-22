-- Fase 1: Caja diaria, movimientos y auditoría de cobros
-- Separación operativa entre caja (control de efectivo) y cobros al paciente

CREATE TABLE cobro_caja_diaria (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    fecha_operativa DATE NOT NULL,
    turno_caja VARCHAR(20),                              -- MANANA, TARDE, NOCHE (null = turno único)
    numero_caja INTEGER,
    estado VARCHAR(30) NOT NULL DEFAULT 'ABIERTA',       -- ABIERTA, PENDIENTE_CIERRE, CERRADA, CERRADA_CON_DIFERENCIA, ANULADA
    saldo_inicial DECIMAL(19,4) NOT NULL DEFAULT 0,
    total_ingresos_paciente DECIMAL(19,4),
    total_ingresos_os DECIMAL(19,4),
    total_egresos DECIMAL(19,4),
    saldo_teorico_cierre DECIMAL(19,4),
    saldo_real_cierre DECIMAL(19,4),
    diferencia_cierre DECIMAL(19,4),
    observaciones_apertura TEXT,
    observaciones_cierre TEXT,
    abierta_por UUID NOT NULL,
    abierta_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cerrada_por UUID,
    cerrada_en TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_caja_consultorio_fecha ON cobro_caja_diaria(consultorio_id, fecha_operativa);

CREATE TABLE cobro_movimiento_caja (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    caja_diaria_id UUID NOT NULL REFERENCES cobro_caja_diaria(id),
    tipo_movimiento VARCHAR(20) NOT NULL,                -- INGRESO, EGRESO, AJUSTE
    origen_movimiento VARCHAR(30) NOT NULL,              -- COBRO_PACIENTE, PAGO_OS, EGRESO_MANUAL, AJUSTE_APERTURA
    origen_id UUID,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(500) NOT NULL,
    importe DECIMAL(19,4) NOT NULL,
    signo VARCHAR(5) NOT NULL DEFAULT 'PLUS',            -- PLUS, MINUS
    medio_pago VARCHAR(30),
    es_anulable BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id UUID NOT NULL,
    observaciones TEXT,
    anulado BOOLEAN NOT NULL DEFAULT FALSE,
    anulado_por UUID,
    anulado_en TIMESTAMP,
    motivo_anulacion TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_movimiento_caja_id ON cobro_movimiento_caja(caja_diaria_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_origen ON cobro_movimiento_caja(origen_id);

CREATE TABLE cobro_auditoria_evento (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    entidad VARCHAR(100) NOT NULL,
    entidad_id UUID NOT NULL,
    accion VARCHAR(30) NOT NULL,
    estado_anterior VARCHAR(2000),
    estado_nuevo VARCHAR(2000),
    usuario_id UUID NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT
);

CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON cobro_auditoria_evento(entidad, entidad_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_consultorio ON cobro_auditoria_evento(consultorio_id, timestamp);
