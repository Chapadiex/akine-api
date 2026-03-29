-- V60: Add arancel_particular_por_sesion to cobro_configuracion_consultorio
ALTER TABLE cobro_configuracion_consultorio
    ADD COLUMN arancel_particular_por_sesion DECIMAL(12, 2);
