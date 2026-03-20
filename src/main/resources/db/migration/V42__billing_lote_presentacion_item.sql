CREATE TABLE IF NOT EXISTS billing_lote_presentacion_item (
    id UUID PRIMARY KEY,
    lote_id UUID NOT NULL REFERENCES billing_lote_presentacion(id) ON DELETE CASCADE,
    atencion_facturable_id UUID NOT NULL REFERENCES billing_atencion_facturable(id),
    importe_presentado DECIMAL(19,4) NOT NULL,
    estado_item VARCHAR(50) NOT NULL, -- INCLUIDO, EXCLUIDO, OBSERVADO
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_billing_lote_item_lote ON billing_lote_presentacion_item(lote_id);
CREATE INDEX IF NOT EXISTS idx_billing_lote_item_atencion ON billing_lote_presentacion_item(atencion_facturable_id);
