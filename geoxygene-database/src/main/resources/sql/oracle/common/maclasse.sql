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


-- Exemple

DROP TABLE MA_CLASSE;

CREATE TABLE MA_CLASSE (
	ID INTEGER PRIMARY KEY,
	FIELD0 INTEGER,
	FIELD1 VARCHAR(50),
	FIELD2 CHAR(1) CHECK (FIELD2='0' OR FIELD2='1'),
	FIELD3 NUMBER
);
