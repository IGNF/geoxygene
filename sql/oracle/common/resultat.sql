-- Copyright (C) 2005 Institut Géographique National
--
-- This library is free software; you can redistribute it and/or modify it under the terms
-- of the GNU Lesser General Public License as published by the Free Software Foundation; 
-- either version 2.1 of the License, or any later version.
--
-- This library is distributed in the hope that it will be useful, but WITHOUT ANY 
-- WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
-- PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public License along with 
-- this library (see file LICENSE if present); if not, write to the Free Software 
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--  
--

-- Creation de tables necessaires au fonctionnement de GeOxygene avec Oracle et OJB.
-- Script a appliquer avant toute d'utilisation de GeOxygene avec Oracle et OJB.


-- Exemple de creation d'une table "RESULTAT" utilisee pour stocker des resultats de traitements geographiques. 


DROP TABLE RESULTAT;

CREATE TABLE RESULTAT (
        COGITID     NUMBER(10)  PRIMARY KEY,
		GEOM        MDSYS.SDO_GEOMETRY,
        DOUBLE1     NUMBER,
        INT1        INTEGER,
        STRING1     VARCHAR2(255),
		BOOLEAN1    CHAR(1) CHECK (BOOLEAN1='1' or BOOLEAN1='0')
        );
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='RESULTAT';
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('RESULTAT','GEOM',NULL,NULL);

COMMIT;
