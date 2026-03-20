CREATE TABLE IF NOT EXISTS billing_convenio_financiador (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id) ON DELETE CASCADE,
    nombre VARCHAR(255) NOT NULL,
    modalidad_pago VARCHAR(50) NOT NULL, -- PRESTACION, MODULO, CAPITA
    vigencia_desde DATE NOT NULL,
    vigencia_hasta DATE,
    dia_cierre INTEGER,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_convenio_financiador ON billing_convenio_financiador(financiador_id);
CREATE INDEX IF NOT EXISTS idx_billing_convenio_vigencia ON billing_convenio_financiador(vigencia_desde, vigencia_hasta);
