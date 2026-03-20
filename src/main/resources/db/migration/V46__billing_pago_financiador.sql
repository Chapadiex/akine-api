CREATE TABLE IF NOT EXISTS billing_pago_financiador (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id),
    liquidacion_id UUID REFERENCES billing_liquidacion_financiador(id),
    fecha_pago DATE NOT NULL,
    importe_pagado DECIMAL(19,4) NOT NULL,
    metodo_pago VARCHAR(50), -- TRANSFERENCIA, CHEQUE, EFECTIVO
    referencia_pago VARCHAR(100),
    conciliado BOOLEAN NOT NULL DEFAULT FALSE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_pago_financiador ON billing_pago_financiador(financiador_id);
CREATE INDEX IF NOT EXISTS idx_billing_pago_fecha ON billing_pago_financiador(fecha_pago);
