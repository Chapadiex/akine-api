CREATE TABLE IF NOT EXISTS billing_liquidacion_financiador (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id),
    convenio_id UUID NOT NULL REFERENCES billing_convenio_financiador(id),
    numero_liquidacion VARCHAR(100) NOT NULL,
    periodo_referido VARCHAR(7) NOT NULL, -- Format YYYY-MM
    importe_bruto DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_debitos DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_neto DECIMAL(19,4) NOT NULL DEFAULT 0,
    estado_conciliacion VARCHAR(50) NOT NULL, -- PENDIENTE, PARCIAL, CONCILIADO
    observaciones TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_liq_financiador ON billing_liquidacion_financiador(financiador_id);
CREATE INDEX IF NOT EXISTS idx_billing_liq_numero ON billing_liquidacion_financiador(numero_liquidacion);
