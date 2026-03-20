CREATE TABLE IF NOT EXISTS billing_lote_presentacion (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id),
    convenio_id UUID NOT NULL REFERENCES billing_convenio_financiador(id),
    periodo VARCHAR(7) NOT NULL, -- Format YYYY-MM
    fecha_presentacion DATE,
    importe_neto_presentado DECIMAL(19,4) NOT NULL DEFAULT 0,
    estado_lote VARCHAR(50) NOT NULL, -- BORRADOR, CERRADO, PRESENTADO, LIQUIDADO
    observaciones TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_lote_financiador ON billing_lote_presentacion(financiador_id);
CREATE INDEX IF NOT EXISTS idx_billing_lote_periodo ON billing_lote_presentacion(periodo);
