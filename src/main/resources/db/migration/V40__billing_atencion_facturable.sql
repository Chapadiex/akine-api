CREATE TABLE IF NOT EXISTS billing_atencion_facturable (
    id UUID PRIMARY KEY,
    atencion_id UUID NOT NULL, -- Referencia genérica a la sesión/atención clínica
    paciente_id UUID NOT NULL REFERENCES pacientes(id),
    convenio_id UUID NOT NULL REFERENCES billing_convenio_financiador(id),
    prestacion_id UUID NOT NULL REFERENCES billing_prestacion_arancelable(id),
    
    -- Snapshot económico del momento
    importe_unitario_snapshot DECIMAL(19,4) NOT NULL,
    importe_total_snapshot DECIMAL(19,4) NOT NULL,
    importe_copago_snapshot DECIMAL(19,4) NOT NULL DEFAULT 0,
    
    estado_facturacion VARCHAR(50) NOT NULL, -- PENDIENTE, LISTA_PARA_PRESENTAR, PRESENTADA, LIQUIDADA, DEBITADA
    facturable BOOLEAN NOT NULL DEFAULT TRUE,
    observaciones TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_atencion_clinica ON billing_atencion_facturable(atencion_id);
CREATE INDEX IF NOT EXISTS idx_billing_atencion_paciente ON billing_atencion_facturable(paciente_id);
CREATE INDEX IF NOT EXISTS idx_billing_atencion_estado ON billing_atencion_facturable(estado_facturacion);
