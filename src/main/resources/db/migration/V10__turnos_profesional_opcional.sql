-- Permite crear turnos del consultorio sin profesional asignado
ALTER TABLE turnos
    ALTER COLUMN profesional_id DROP NOT NULL;
