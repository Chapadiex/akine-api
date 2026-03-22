-- Fase 1: Cobros al paciente con soporte multi-medio de pago
-- liquidacion_sesion_id es nullable — se completa en Fase 3

CREATE TABLE cobro_cobro_paciente (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    liquidacion_sesion_id UUID,                          -- FK agregado en Fase 3
    sesion_id UUID REFERENCES historia_clinica_sesiones(id),
    paciente_id UUID NOT NULL REFERENCES pacientes(id),
    caja_diaria_id UUID NOT NULL REFERENCES cobro_caja_diaria(id),
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',     -- PENDIENTE, PARCIAL, COBRADO_TOTAL, ANULADO
    fecha_cobro DATE NOT NULL,
    importe_total DECIMAL(19,4) NOT NULL,
    es_pago_mixto BOOLEAN NOT NULL DEFAULT FALSE,
    comprobante_numero VARCHAR(50),
    recibo_emitido BOOLEAN NOT NULL DEFAULT FALSE,
    observaciones TEXT,
    cobrado_por UUID NOT NULL,
    anulado_por UUID,
    anulado_en TIMESTAMP,
    motivo_anulacion TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_cobro_paciente_id ON cobro_cobro_paciente(paciente_id);
CREATE INDEX IF NOT EXISTS idx_cobro_consultorio ON cobro_cobro_paciente(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_cobro_caja ON cobro_cobro_paciente(caja_diaria_id);

CREATE TABLE cobro_cobro_paciente_detalle (
    id UUID PRIMARY KEY,
    cobro_paciente_id UUID NOT NULL REFERENCES cobro_cobro_paciente(id),
    medio_pago VARCHAR(30) NOT NULL,
    importe DECIMAL(19,4) NOT NULL,
    referencia_operacion VARCHAR(100),
    cuotas INTEGER,
    banco VARCHAR(100),
    marca_tarjeta VARCHAR(50),
    numero_ultimos_4 VARCHAR(4),
    fecha_acreditacion DATE,
    observaciones TEXT
);

CREATE INDEX IF NOT EXISTS idx_cobro_detalle_cobro ON cobro_cobro_paciente_detalle(cobro_paciente_id);
