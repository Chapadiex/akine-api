-- Renombrar columna profesion a profesiones y ampliar longitud para soportar
-- múltiples profesiones separadas por coma.
ALTER TABLE pacientes RENAME COLUMN profesion TO profesiones;
ALTER TABLE pacientes ALTER COLUMN profesiones VARCHAR(500);
