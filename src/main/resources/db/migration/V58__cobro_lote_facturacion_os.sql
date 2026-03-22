-- Fase 4: Lotes de facturación a Obras Sociales
-- Agrupa liquidaciones facturable-OS por período/financiador para presentar a la OS

CREATE TABLE cobro_lote_facturacion_os (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id),
    financiador_id UUID NOT NULL,
    plan_id UUID,
    convenio_id UUID,
    periodo VARCHAR(7) NOT NULL,           -- YYYY-MM
    estado VARCHAR(20) NOT NULL,           -- BORRADOR, CERRADO, PRESENTADO, LIQUIDADO, ANULADO
    cantidad_sesiones INT NOT NULL DEFAULT 0,
    importe_total_os DECIMAL(19,4) NOT NULL DEFAULT 0,
    importe_neto DECIMAL(19,4) NOT NULL DEFAULT 0,
    observaciones TEXT,
    cerrado_en TIMESTAMP,
    cerrado_por UUID,
    presentado_en TIMESTAMP,
    presentado_por UUID,
    creado_por UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE cobro_lote_facturacion_os_detalle (
    id UUID PRIMARY KEY,
    lote_id UUID NOT NULL REFERENCES cobro_lote_facturacion_os(id),
    liquidacion_sesion_id UUID NOT NULL REFERENCES cobro_liquidacion_sesion(id),
    sesion_id UUID NOT NULL,
    paciente_id UUID NOT NULL,
    importe_os DECIMAL(19,4) NOT NULL,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para búsqueda por consultorio + período
CREATE INDEX idx_lote_os_consultorio ON cobro_lote_facturacion_os(consultorio_id, periodo);
CREATE INDEX idx_lote_os_financiador ON cobro_lote_facturacion_os(consultorio_id, financiador_id, estado);

-- Índice para encontrar detalles por lote
CREATE INDEX idx_lote_os_detalle_lote ON cobro_lote_facturacion_os_detalle(lote_id);

-- Note: partial unique index (liquidacion_sesion_id WHERE estado != 'ANULADA') not supported in H2.
-- In production (PostgreSQL), add:
-- CREATE UNIQUE INDEX idx_lote_detalle_liquidacion ON cobro_lote_facturacion_os_detalle(liquidacion_sesion_id);
CREATE INDEX idx_lote_detalle_liquidacion ON cobro_lote_facturacion_os_detalle(liquidacion_sesion_id);
