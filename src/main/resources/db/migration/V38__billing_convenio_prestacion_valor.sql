CREATE TABLE IF NOT EXISTS billing_convenio_prestacion_valor (
    id UUID PRIMARY KEY,
    convenio_id UUID NOT NULL REFERENCES billing_convenio_financiador(id) ON DELETE CASCADE,
    plan_id UUID REFERENCES coverage_plan_financiador(id), -- Nullable si aplica a todos los planes del convenio
    prestacion_id UUID NOT NULL REFERENCES billing_prestacion_arancelable(id),
    vigencia_desde DATE NOT NULL,
    vigencia_hasta DATE,
    importe_base DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_copago DECIMAL(19,4) NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_valor_convenio ON billing_convenio_prestacion_valor(convenio_id);
CREATE INDEX IF NOT EXISTS idx_billing_valor_prestacion ON billing_convenio_prestacion_valor(prestacion_id);
CREATE INDEX IF NOT EXISTS idx_billing_valor_vigencia ON billing_convenio_prestacion_valor(vigencia_desde, vigencia_hasta);
