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


-- Exemple de creation d'une table "TABLEAUX" .
-- Montre comment on peut rendre persistants des tableaux.


--------------------------------------------------------------------------
-- Suppression de la table -----------------------------------------------
--------------------------------------------------------------------------
DROP TABLE TABLEAUX;


--------------------------------------------------------------------------
-- Creation de types pour utiliser des tableaux --------------------------
--------------------------------------------------------------------------
CREATE OR REPLACE TYPE VARRAY_OF_DOUBLE AS VARRAY(10) OF NUMBER;
/
CREATE OR REPLACE TYPE VARRAY_OF_INTEGER AS VARRAY(15) OF INTEGER;
/
CREATE OR REPLACE TYPE VARRAY_OF_BOOLEAN AS VARRAY(5) OF CHAR(1);
/
CREATE OR REPLACE TYPE VARRAY_OF_STRING AS VARRAY(10) OF VARCHAR(50);
/


--------------------------------------------------------------------------
-- Creation de la table --------------------------------------------------
--------------------------------------------------------------------------
CREATE TABLE TABLEAUX (
        COGITID     NUMBER(10)  PRIMARY KEY,
        DOUBLES     VARRAY_OF_DOUBLE,
        INTS        VARRAY_OF_INTEGER,
        BOOLEANS    VARRAY_OF_BOOLEAN,
        STRINGS     VARRAY_OF_STRING		        		
        );


--------------------------------------------------------------------------
-- Validation ------------------------------------------------------------
--------------------------------------------------------------------------
COMMIT;
