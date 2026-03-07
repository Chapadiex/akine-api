ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS map_latitude NUMERIC(9,6);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS map_longitude NUMERIC(10,6);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS google_maps_url VARCHAR(500);

ALTER TABLE consultorios
    ADD CONSTRAINT chk_consultorios_map_latitude
        CHECK (map_latitude IS NULL OR (map_latitude >= -90 AND map_latitude <= 90));

ALTER TABLE consultorios
    ADD CONSTRAINT chk_consultorios_map_longitude
        CHECK (map_longitude IS NULL OR (map_longitude >= -180 AND map_longitude <= 180));
