DROP FUNCTION way_geom(id_serial_min integer, id_serial_max integer);
CREATE OR REPLACE FUNCTION way_geom(id_serial_min integer, id_serial_max integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
	row way%rowtype;
	nodeid BIGINT;
	result RECORD;
	lon_min_rec numeric;
	lat_min_rec numeric;
	lon_max_rec numeric;
	lat_max_rec numeric;

BEGIN

	FOR row in SELECT * FROM way WHERE id_serial >= id_serial_min AND id_serial<=id_serial_max LOOP -- Parcourt la table way
		lon_min_rec := 180;
		lat_min_rec := 90;
		lon_max_rec := -180;
		lat_max_rec := -90;

		IF row.composedof IS NOT NULL THEN		
			FOREACH nodeid IN ARRAY row.composedof LOOP -- Parcourt la liste des noeuds composant chaque elt de la table way
					/*IF lon_min_rec = 180 THEN -- recherche partout
						SELECT DISTINCT ON (datemodif) id, lon as longitude, lat as latitude
						FROM node
						WHERE node.id = nodeid AND datemodif <= row.datemodif
						ORDER BY datemodif DESC
						INTO result;
					ELSE
						SELECT DISTINCT ON (datemodif) id, lon as longitude, lat as latitude
						FROM (SELECT * FROM node WHERE ST_DWithin(ST_SetSRID(ST_MakePoint(lon_min_rec,lat_min_rec),4326), geom,0.1)
						AND datemodif <= row.datemodif) AS node_within_0_1_deg
						WHERE node_within_0_1_deg.id = nodeid 
						ORDER BY datemodif DESC
						INTO result;
					END IF;*/
					SELECT id, lon as longitude, lat as latitude
						FROM node
						WHERE node.id = nodeid AND node.datemodif <= row.datemodif
						ORDER BY datemodif DESC
						LIMIT 1
						INTO result;
					

					IF result.longitude < lon_min_rec THEN
						lon_min_rec := result.longitude;
					END IF;
					
					IF result.latitude < lat_min_rec THEN
						lat_min_rec := result.latitude;	
					END IF;
					
					IF result.longitude > lon_max_rec THEN
						lon_max_rec := result.longitude;
					END IF;
					
					IF result.latitude > lat_max_rec THEN
						lat_max_rec := result.latitude;
					END IF;
			END LOOP;
		END IF;
		IF lon_min_rec <> 180 AND lat_min_rec <> 90 AND lon_max_rec <> -180 AND lat_max_rec <> -90 THEN
			UPDATE way SET lon_min = lon_min_rec, lat_min = lat_min_rec, lon_max = lon_max_rec, lat_max = lat_max_rec WHERE idway = row.idway;
			--UPDATE way SET lat_min = lat_min_rec WHERE idway = row.idway;
			--UPDATE way SET lon_max = lon_max_rec WHERE idway = row.idway;
			--UPDATE way SET lat_max = lat_max_rec WHERE idway = row.idway;
		END IF;
	END LOOP;
END;
$$;

