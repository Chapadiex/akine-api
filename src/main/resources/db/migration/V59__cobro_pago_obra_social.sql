-- Fase 4: Pago de obra social
-- Registra el cobro recibido de la OS y permite imputarlo a la caja diaria

CREATE TABLE cobro_pago_obra_social (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    lote_id UUID NOT NULL REFERENCES cobro_lote_facturacion_os(id),
    financiador_id UUID NOT NULL,
    importe_esperado DECIMAL(19,4) NOT NULL,
    importe_recibido DECIMAL(19,4) NOT NULL,
    diferencia DECIMAL(19,4) NOT NULL DEFAULT 0,
    fecha_notificacion DATE NOT NULL,
    fecha_imputacion DATE,
    caja_diaria_id UUID REFERENCES cobro_caja_diaria(id),
    imputado_por UUID,
    imputado_en TIMESTAMP,
    observaciones TEXT,
    registrado_por UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_pago_os_consultorio ON cobro_pago_obra_social(consultorio_id);
CREATE INDEX idx_pago_os_lote ON cobro_pago_obra_social(lote_id);
