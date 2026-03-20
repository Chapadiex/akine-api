CREATE TABLE IF NOT EXISTS billing_liquidacion_ajuste (
    id UUID PRIMARY KEY,
    liquidacion_id UUID NOT NULL REFERENCES billing_liquidacion_financiador(id) ON DELETE CASCADE,
    lote_item_id UUID REFERENCES billing_lote_presentacion_item(id), -- Opcional si es un ajuste general
    tipo_ajuste VARCHAR(50) NOT NULL, -- DEBITO_ADMINISTRATIVO, DEBITO_MEDICO, AJUSTE_ARANCEL, OTRO
    importe DECIMAL(19,4) NOT NULL,
    motivo TEXT,
    resuelto BOOLEAN NOT NULL DEFAULT FALSE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_ajuste_liq ON billing_liquidacion_ajuste(liquidacion_id);
