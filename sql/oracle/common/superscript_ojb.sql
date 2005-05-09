--
-- This file is part of the GeOxygene project source files. 
-- 
-- GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
-- the development and deployment of geographic (GIS) applications. It is a open source 
-- contribution of the COGIT laboratory at the Institut Géographique National (the French 
-- National Mapping Agency).
-- 
-- See: http://oxygene-project.sourceforge.net 
--  
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


--------------------------------------------------------------------------
-- Creation de la table utilisee pour les requetes spatiales -------------
--------------------------------------------------------------------------
DROP TABLE TEMP_REQUETE;
CREATE TABLE TEMP_REQUETE (
	GID NUMBER PRIMARY KEY,
	GEOM MDSYS.SDO_GEOMETRY
);


--------------------------------------------------------------------------
-- Tables internes pour OJB ----------------------------------------------
--------------------------------------------------------------------------
DROP TABLE OJB_HL_SEQ;
CREATE TABLE OJB_HL_SEQ (
    TABLENAME VARCHAR(175),
    FIELDNAME VARCHAR(70),
    UNIQUE (TABLENAME,FIELDNAME),
    MAX_KEY INTEGER,
    GRAB_SIZE INTEGER
);

DROP TABLE OJB_DLIST;
CREATE TABLE OJB_DLIST (
    ID INTEGER PRIMARY KEY,
    SIZE_ INTEGER
);

DROP TABLE OJB_DLIST_ENTRIES;
CREATE TABLE OJB_DLIST_ENTRIES (
    ID INTEGER PRIMARY KEY,
    DLIST_ID INTEGER,
    POSITION_ INTEGER,
    OID_ LONG RAW
);


--------------------------------------------------------------------------
-- Validation ------------------------------------------------------------
--------------------------------------------------------------------------
COMMIT;
