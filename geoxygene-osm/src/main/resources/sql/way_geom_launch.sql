ALTER TABLE way ADD COLUMN id_serial serial;
ALTER TABLE way ADD COLUMN lon_min numeric;
ALTER TABLE way ADD COLUMN lat_min numeric;
ALTER TABLE way ADD COLUMN lon_max numeric;
ALTER TABLE way ADD COLUMN lat_max numeric;

SELECT min(id_serial), max(id_serial) from way;
SELECT way_geom(1,25607);
SELECT count(*) FROM way WHERE lon_min IS NULL
SELECT * FROM way WHERE lon_min IS NULL