ALTER TABLE historia_clinica_adjuntos
    ADD COLUMN IF NOT EXISTS caso_atencion_id UUID REFERENCES caso_atencion(id);

CREATE INDEX IF NOT EXISTS idx_hc_adjuntos_caso_atencion
    ON historia_clinica_adjuntos (caso_atencion_id, created_at ASC);
