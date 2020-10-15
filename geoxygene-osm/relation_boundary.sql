﻿DROP FUNCTION IF EXISTS relation_boundary(idrel integer, datemodif timestamp);
-- Get the coordinates of a city's spatial envelope
----------------------------------------------------------------- PARAMETERS ----------------------------------------------------------------
-- idrelation : ID of the OSM relation object which corresponds to the city ; timestamp : date when the OSM relation (idrelation) is valid --
---------------------------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION relation_boundary(idrelation integer, t timestamp) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
	row relationmember%rowtype;
	result RECORD;
	
	lon_min_rec numeric;
	lat_min_rec numeric;
	lon_max_rec numeric;
	lat_max_rec numeric;

BEGIN
	lon_min_rec := 180;
	lat_min_rec := 90;
	lon_max_rec := -180;
	lat_max_rec := -90;
	FOR row in SELECT * FROM relationmember WHERE relationmember.idrel = idrelation LOOP -- Browse the relation members of OSM relation object

			IF row.typemb='w' THEN	-- On regarde les membres de type way	
				-- Get the visible way at the chosen date t
				SELECT id, way.lon_min, way.lat_min, way.lon_max, way.lat_max
				FROM way
				WHERE way.id = row.idmb AND way.visible IS TRUE AND way.datemodif <= t
				ORDER BY datemodif DESC
				LIMIT 1
				INTO result;

				IF result.lon_min < lon_min_rec THEN lon_min_rec := result.lon_min;
				END IF;
							
				IF result.lat_min < lat_min_rec THEN lat_min_rec := result.lat_min;	
				END IF;
							
				IF result.lon_max > lon_max_rec THEN lon_max_rec := result.lon_max;
				END IF;
							
				IF result.lat_max > lat_max_rec THEN lat_max_rec := result.lat_max;
				END IF;
			END IF;
	END LOOP;
	UPDATE enveloppe SET lon_min = lon_min_rec, lat_min = lat_min_rec, lon_max = lon_max_rec, lat_max = lat_max_rec;
END;
$$;

