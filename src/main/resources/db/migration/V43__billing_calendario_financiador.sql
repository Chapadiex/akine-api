CREATE TABLE IF NOT EXISTS billing_calendario_financiador (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id),
    convenio_id UUID REFERENCES billing_convenio_financiador(id),
    anio INTEGER NOT NULL,
    mes INTEGER NOT NULL,
    estado_periodo VARCHAR(50) NOT NULL, -- ABIERTO, CERRADO, PRESENTADO
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_billing_calendario UNIQUE (financiador_id, anio, mes)
);
