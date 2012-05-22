/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for 
 * the development and deployment of geographic (GIS) applications. It is a open source 
 * contribution of the COGIT laboratory at the Institut Géographique National (the French 
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net 
 *  
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library (see file LICENSE if present); if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

-- #######################################
-- ########### version pour CASTOR #######
-- #######################################

-------------------------------------------------------------------------------
-- Création des tables du schéma relationnel de GeOxygene ------------------------
-------------------------------------------------------------------------------

--------------------------------------------------------------------------
-- Supression des tables existantes --------------------------------------
--------------------------------------------------------------------------
DROP TABLE RESULT_POINT;
DROP TABLE RESULT_CURVE;
DROP TABLE RESULT_SURFACE;
DROP TABLE TEMP_REQUETE;

DROP TABLE FEATURE_TOPO;
DROP TABLE GEO_TRAIT;
DROP TABLE FT_FEATURE;

DROP TABLE TP_EDGE;
DROP TABLE TP_NODE;
DROP TABLE TP_FACE;
--DROP TABLE TP_SOLID;

--DROP TABLE TP_COMPLEX;
DROP TABLE TP_OBJECT;

--DROP TABLE GM_COMPLEX;
--DROP TABLE GM_AGGREGATE;
--DROP TABLE GM_OBJECT;

DROP TABLE GF_CONSTRAINT;
DROP TABLE GF_OPERATION;
DROP TABLE GF_ASSOCIATIONROLE;
DROP TABLE GF_ASSOCIATION_FEATURE;
DROP TABLE GF_ATTRIBUTETYPE;
DROP TABLE GF_ASSOCIATIONTYPE;
DROP TABLE GF_INHERITANCERELATION;
DROP TABLE GF_FEATURETYPE;



--------------------------------------------------------------------------
-- Créations des tables du dictionnaire (tables GF_) ---------------------
--------------------------------------------------------------------------
CREATE TABLE GF_FEATURETYPE (
	GF_FeatureTypeID	NUMBER(10) 	PRIMARY KEY,
	typeName 		VARCHAR2(255)   UNIQUE,
	definition		VARCHAR2(2000),
	isAbstract		CHAR(1)         CHECK (isAbstract IN ('0','1')),
        tableName               VARCHAR2(100)   --revoir si utile ?
        );


CREATE TABLE GF_INHERITANCERELATION (
	GF_InheritanceRelationID    NUMBER(10)      PRIMARY KEY,
	name                        VARCHAR2(255),
	description                 VARCHAR2(2000),
	uniqueInstance              CHAR(1)         CHECK (uniqueInstance IN ('0','1')),
	subType                     NUMBER(10),
	superType                   NUMBER(10),
 	CONSTRAINT fk_GF_InRel_subType FOREIGN KEY (subType) REFERENCES GF_FEATURETYPE ON DELETE CASCADE,
	CONSTRAINT fk_GF_InRel_superType FOREIGN KEY (superType) REFERENCES GF_FEATURETYPE ON DELETE CASCADE  
        );


CREATE TABLE GF_ASSOCIATIONTYPE (
	GF_AssociationTypeID	NUMBER(10) 	PRIMARY KEY,
	typeName 		VARCHAR2(255)   UNIQUE,
	definition		VARCHAR2(2000),
	isAbstract		CHAR(1),
	associationType		VARCHAR2(255)  -- associationType indique : agregation, spatial, temporal, autre (faire un check)
        );


-- implementation de linkBeetween / memberOf
CREATE TABLE GF_ASSOCIATION_FEATURE (
	GF_FeatureTypeID	NUMBER(10),
	GF_AssociationTypeID	NUMBER(10),
	CONSTRAINT fk_GF_AsFe_FeatureTypeID FOREIGN KEY (GF_FeatureTypeID) REFERENCES GF_FeatureType ON DELETE CASCADE,
	CONSTRAINT fk_GF_AsFe_AssociationTypeID FOREIGN KEY (GF_AssociationTypeID) REFERENCES GF_AssociationType ON DELETE CASCADE
        );


CREATE TABLE GF_ASSOCIATIONROLE (
	GF_AssociationRoleID	NUMBER(10) 	PRIMARY KEY,
	GF_AssociationTypeID	NUMBER(10),
	GF_FeatureTypeID	NUMBER(10),
	memberName		VARCHAR2(255),
	definition		VARCHAR2(2000),
	valueType		VARCHAR2(255),
	cardMin			INTEGER,
	cardMax			INTEGER,
        CONSTRAINT un_GF_AsRo UNIQUE(GF_AssociationTypeID,GF_FeatureTypeID,memberName),
	CONSTRAINT fk_GF_AsRo_AssociationTypeID FOREIGN KEY (GF_AssociationTypeID) REFERENCES GF_AssociationType ON DELETE CASCADE,
	CONSTRAINT fk_GF_AsRo_FeatureTypeID FOREIGN KEY (GF_FeatureTypeID) REFERENCES GF_FeatureType ON DELETE CASCADE
        );


-- Un attribut se rapporte soit a un feature type, soit a une association,
-- donc l'un des 2 champs GF_FeatureTypeID ou GF_AssociationTypeID doit etre NULL.
CREATE TABLE GF_ATTRIBUTETYPE (
	GF_AttributeTypeID	NUMBER(10)	PRIMARY KEY,
	GF_FeatureTypeID	NUMBER(10),
	GF_AssociationTypeID	NUMBER(10),
	memberName		VARCHAR2(255),
	definition		VARCHAR2(2000),
	valueType		VARCHAR2(255),
	domainOfValues		VARCHAR2(2000),
	cardMin			INTEGER,
	cardMax			INTEGER,
	characterizeID		NUMBER(10),
        CONSTRAINT un_GF_AtTy UNIQUE(GF_FeatureTypeID,GF_AssociationTypeID,memberName),
	CONSTRAINT fk_GF_AtTy_FeatureTypeID FOREIGN KEY (GF_FeatureTypeID) REFERENCES GF_FeatureType ON DELETE CASCADE,
	CONSTRAINT fk_GF_AtTy_AssociationTypeID FOREIGN KEY (GF_AssociationTypeID) REFERENCES GF_AssociationType ON DELETE CASCADE,
 	CONSTRAINT fk_GF_AtTy_characterizeID FOREIGN KEY (characterizeID) REFERENCES GF_AttributeType ON DELETE CASCADE
        );


-- Une operation se rapporte soit a un feature type, soit a une association,
-- donc l'un des 2 champs GF_FeatureTypeID ou GF_AssociationTypeID doit etre NULL.
-- Incomplet par rapport au modele : les liens "triggered", "affects", "observes", 
-- "depends" ne sont pas implémentés (faire une nouvelle table pour chacune de ces relations).
CREATE TABLE GF_OPERATION (
	GF_OperationID		NUMBER(10)	PRIMARY KEY,
	GF_FeatureTypeID	NUMBER(10),
	GF_AssociationTypeID	NUMBER(10),
	memberName		VARCHAR2(255),
	definition		VARCHAR2(2000),
	signature		VARCHAR2(2000),
        CONSTRAINT un_GF_Op UNIQUE(GF_FeatureTypeID,GF_AssociationTypeID,memberName),
        CONSTRAINT fk_GF_Op_FeatureTypeID FOREIGN KEY (GF_FeatureTypeID) REFERENCES GF_FeatureType ON DELETE CASCADE,
	CONSTRAINT fk_GF_Op_AssociationTypeID FOREIGN KEY (GF_AssociationTypeID) REFERENCES GF_AssociationType ON DELETE CASCADE
        );


CREATE TABLE GF_CONSTRAINT (
	GF_ConstraintID		NUMBER(10)	PRIMARY KEY,
	description		VARCHAR2(2000),
	GF_FeatureTypeID	NUMBER(10),
	GF_AssociationTypeID	NUMBER(10),
	GF_AttributeTypeID	NUMBER(10),
	GF_OperationID		NUMBER(10),
	GF_AssociationRoleID	NUMBER(10),
	CONSTRAINT fk_GF_Cons_FeatureTypeID FOREIGN KEY (GF_FeatureTypeID) REFERENCES GF_FeatureType ON DELETE CASCADE,
	CONSTRAINT fk_GF_Cons_AssociationTypeID FOREIGN KEY (GF_AssociationTypeID) REFERENCES GF_AssociationType ON DELETE CASCADE,
	CONSTRAINT fk_GF_Cons_AttributeTypeID FOREIGN KEY (GF_AttributeTypeID) REFERENCES GF_AttributeType ON DELETE CASCADE,
	CONSTRAINT fk_GF_Cons_OperationID FOREIGN KEY (GF_OperationID) REFERENCES GF_Operation ON DELETE CASCADE,
	CONSTRAINT fk_GF_Cons_AssociationRoleID FOREIGN KEY (GF_AssociationRoleID) REFERENCES GF_AssociationRole ON DELETE CASCADE
        );



--------------------------------------------------------------------------
-- Créations des triggers pour la génération des clefs (algo du max) -----
--------------------------------------------------------------------------
-- attention : revoir l'utilité de ceci (les clés sont aussi gérées au niveau de Castor)
CREATE OR REPLACE TRIGGER key_GF_FeatureType
	before insert on GF_FeatureType for each row
	declare nbre integer;
	begin
		select max(GF_FeatureTypeID) into nbre from GF_FeatureType;
		if nbre is null then
			select 1 into :new.GF_FeatureTypeID from dual;
		else
			select (nbre+1) into :new.GF_FeatureTypeID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_InheritanceRelation
	before insert on GF_InheritanceRelation for each row
	declare nbre integer;
	begin
		select max(GF_InheritanceRelationID) into nbre from GF_InheritanceRelation;
		if nbre is null then
			select 1 into :new.GF_InheritanceRelationID from dual;
		else
			select (nbre+1) into :new.GF_InheritanceRelationID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_AssociationType
	before insert on GF_AssociationType for each row
	declare nbre integer;
	begin
		select max(GF_AssociationTypeID) into nbre from GF_AssociationType;
		if nbre is null then
			select 1 into :new.GF_AssociationTypeID from dual;
		else
			select (nbre+1) into :new.GF_AssociationTypeID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_AssociationRole
	before insert on GF_AssociationRole for each row
	declare nbre integer;
	begin
		select max(GF_AssociationRoleID) into nbre from GF_AssociationRole;
		if nbre is null then
			select 1 into :new.GF_AssociationRoleID from dual;
		else
			select (nbre+1) into :new.GF_AssociationRoleID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_AttributeType
	before insert on GF_AttributeType for each row
	declare nbre integer;
	begin
		select max(GF_AttributeTypeID) into nbre from GF_AttributeType;
		if nbre is null then
			select 1 into :new.GF_AttributeTypeID from dual;
		else
			select (nbre+1) into :new.GF_AttributeTypeID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_Operation
	before insert on GF_Operation for each row
	declare nbre integer;
	begin
		select max(GF_OperationID) into nbre from GF_Operation;
		if nbre is null then
			select 1 into :new.GF_OperationID from dual;
		else
			select (nbre+1) into :new.GF_OperationID from dual;
		end if;
	end;
/


CREATE OR REPLACE TRIGGER key_GF_Constraint
	before insert on GF_Constraint for each row
	declare nbre integer;
	begin
		select max(GF_ConstraintID) into nbre from GF_Constraint;
		if nbre is null then
			select 1 into :new.GF_ConstraintID from dual;
		else
			select (nbre+1) into :new.GF_ConstraintID from dual;
		end if;
	end;
/



--------------------------------------------------------------------------
-- Créations des tables FT_ ----------------------------------------------
--------------------------------------------------------------------------
-- a completer pour les associations
CREATE TABLE FT_FEATURE (
	COGITID 	NUMBER(10) 	PRIMARY KEY,
        classType 	VARCHAR2(255),
        DALLE_ID        NUMBER(10),
        TP_OBJECTID	NUMBER(10)
        );


-- lien objet geo / objet trait
CREATE TABLE GEO_TRAIT (
	OBJET_GEO_ID 	NUMBER(10) REFERENCES FT_Feature ON DELETE CASCADE,
        OBJET_TRAIT_ID 	NUMBER(10) REFERENCES FT_Feature ON DELETE CASCADE
        );



--------------------------------------------------------------------------
-- Geometrie : créations des tables GM_ ----------------------------------
--------------------------------------------------------------------------
-- il faudra revoir ceci pour les multi geometries et les partages de geometrie
--CREATE TABLE GM_OBJECT (
--	GM_ObjectID 	NUMBER(10)      PRIMARY KEY,
--	classType 	VARCHAR2(16)    CHECK (classType IN ('GM_Primitive','GM_Aggregate','GM_Complex'
--                                                                ,'GM_Point','GM_Curve','GM_Surface','GM_Solid'
--                                                                ,'GM_MultiPoint','GM_MultiCurve','GM_MultiSurface','GM_MultiSolid'
--                                                                ,'GM_CompositePoint','GM_CompositeCurve','GM_CompositeSurface','GM_CompositeSolid'
--                                                                ,NULL)),
--        attrGeom         LONG  -- attrGeom est utilise pour stocker des attributs sur une geometrie
--        );


--CREATE TABLE GM_AGGREGATE (
--	GM_ObjectID 	NUMBER(10),
--	GM_AggregateID 	NUMBER(10),
--	CONSTRAINT fk_GM_Aggr_ObjectID FOREIGN KEY (GM_ObjectID) REFERENCES GM_Object ON DELETE CASCADE,
--	CONSTRAINT fk_GM_Aggr_AggregateID FOREIGN KEY (GM_ObjectID) REFERENCES GM_Object  ON DELETE CASCADE
--        );


--CREATE TABLE GM_COMPLEX (
--	GM_ObjectID 	NUMBER(10),
--	GM_ComplexID 	NUMBER(10),
--	CONSTRAINT fk_GM_Complex_ObjectID FOREIGN KEY (GM_ObjectID) REFERENCES GM_Object ON DELETE CASCADE,
--	CONSTRAINT fk_GM_Complex_ComplexID FOREIGN KEY (GM_ObjectID) REFERENCES GM_Object ON DELETE CASCADE
--        );



--------------------------------------------------------------------------
-- Topologie : créations des tables TP_ ----------------------------------
--------------------------------------------------------------------------
CREATE TABLE TP_OBJECT (
	TP_ObjectID	NUMBER(10) 	PRIMARY KEY,
	classType 	VARCHAR2(255),
        DALLE_ID        NUMBER(10),
        FT_FEATUREID	NUMBER(10)
        );


--CREATE TABLE TP_COMPLEX (
--	TP_ObjectID 	NUMBER(10),
--	TP_ComplexID 	NUMBER(10),
--	CONSTRAINT fk_TP_Complex_ObjectID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE,
--	CONSTRAINT fk_TP_Complex_ComplexID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE 
--        );


--CREATE TABLE TP_SOLID (
--	TP_ObjectID          NUMBER(10)  PRIMARY KEY,
--	CONSTRAINT fk_TP_Solid_SolidID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE
--        );


CREATE TABLE TP_FACE (
	TP_ObjectID         NUMBER(10)  PRIMARY KEY,
	solidID             NUMBER(10),
	CONSTRAINT fk_TP_Face_FaceID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE
--	CONSTRAINT fk_TP_Face_Solid FOREIGN KEY (SolidID) REFERENCES TP_Solid ON DELETE SET NULL
        );


CREATE TABLE TP_NODE (
	TP_ObjectID         NUMBER(10)  PRIMARY KEY,
	container           NUMBER(10),
	CONSTRAINT fk_TP_Node_NodeID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE,
        CONSTRAINT fk_TP_Node_container FOREIGN KEY (container) REFERENCES TP_Face ON DELETE SET NULL
        );


CREATE TABLE TP_EDGE (
	TP_ObjectID         NUMBER(10)  PRIMARY KEY,
	startNodeID         NUMBER(10),
	endNodeID           NUMBER(10),
        leftFaceID          NUMBER(10),
        rightFaceID         NUMBER(10),
	container           NUMBER(10),
	CONSTRAINT fk_TP_TP_Edge_EdgeID FOREIGN KEY (TP_ObjectID) REFERENCES TP_Object ON DELETE CASCADE,
	CONSTRAINT fk_TP_Edge_startNode FOREIGN KEY (startNodeID) REFERENCES TP_Node ON DELETE SET NULL,
        CONSTRAINT fk_TP_Edge_endNode FOREIGN KEY (endNodeID) REFERENCES TP_Node ON DELETE SET NULL,
	CONSTRAINT fk_TP_Edge_leftFace FOREIGN KEY (leftFaceID) REFERENCES TP_Face ON DELETE SET NULL,
        CONSTRAINT fk_TP_Edge_rightFace FOREIGN KEY (rightFaceID) REFERENCES TP_Face ON DELETE SET NULL
--        CONSTRAINT fk_TP_Edge_container FOREIGN KEY (container) REFERENCES TP_Solid ON DELETE SET NULL
        );


-- n'est plus utilise
-- lien n-m entre feature et topo
CREATE TABLE FEATURE_TOPO (
    FT_FeatureID   NUMBER(10) REFERENCES FT_Feature ON DELETE CASCADE,
    TP_ObjectID      NUMBER(10) REFERENCES TP_Object ON DELETE CASCADE
);




--------------------------------------------------------------------------
-- Créations des triggers pour la génération des clefs (algo du max) -----
--------------------------------------------------------------------------
-- inutile car key-generator dans Castor
--CREATE OR REPLACE TRIGGER key_FT_Feature
--	before insert on FT_Feature for each row
--	declare nbre integer;
--	begin
--		select max(FT_FeatureID) into nbre from FT_Feature;
--		if nbre is null then
--			select 1 into :new.FT_FeatureID from dual;
--		else
--			select (nbre+1) into :new.FT_FeatureID from dual;
--		end if;
--	end;
--/


--CREATE OR REPLACE TRIGGER key_GM_Object
--	before insert on GM_Object for each row
--	declare nbre integer;
--	begin
--		select max(GM_ObjectID) into nbre from GM_Object;
--		if nbre is null then
--			select 1 into :new.GM_ObjectID from dual;
--		else
--			select (nbre+1) into :new.GM_ObjectID from dual;
--		end if;
--	end;
--/

-- inutile car key-generator dans Castor
--CREATE OR REPLACE TRIGGER key_TP_Object
--	before insert on TP_Object for each row
--	declare nbre integer;
--	begin
--		select max(TP_ObjectID) into nbre from TP_Object;
--		if nbre is null then
--			select 1 into :new.TP_ObjectID from dual;
--		else
--			select (nbre+1) into :new.TP_ObjectID from dual;
--		end if;
--	end;
--/



--------------------------------------------------------------------------
-- Création de quelques index --------------------------------------------
--------------------------------------------------------------------------
CREATE INDEX TP_node_container_idx ON TP_Node(container);
CREATE INDEX TP_edge_startnode_idx ON TP_Edge(startNodeID);
CREATE INDEX TP_edge_endnode_idx ON TP_Edge(endNodeID);
CREATE INDEX TP_edge_leftface_idx ON TP_Edge(leftFaceID);
CREATE INDEX TP_edge_rightface_idx ON TP_Edge(rightFaceID);



--------------------------------------------------------------------------
-- Création de la table utilisée pour les requêtes spatiales -------------
--------------------------------------------------------------------------
CREATE TABLE TEMP_REQUETE (
	GID NUMBER PRIMARY KEY,
	GEOM MDSYS.SDO_GEOMETRY
        );



--------------------------------------------------------------------------
-- Création des tables utilisée pour écrire les résultats ----------------
--------------------------------------------------------------------------
CREATE TABLE RESULT_POINT (
        COGITID     NUMBER(10)  PRIMARY KEY,
	GEOM        MDSYS.SDO_GEOMETRY,
        DOUBLE1     NUMBER,
        DOUBLE2     NUMBER,
        DOUBLE3     NUMBER,
        INT1        INTEGER,
        INT2        INTEGER,
        INT3        INTEGER,
        STRING1     VARCHAR2(255),
        STRING2     VARCHAR2(255),
        STRING3     VARCHAR2(255),
	BOOLEAN1    CHAR(1) CHECK (BOOLEAN1='1' or BOOLEAN1='0'),
	BOOLEAN2    CHAR(1) CHECK (BOOLEAN2='1' or BOOLEAN2='0'),
	BOOLEAN3    CHAR(1) CHECK (BOOLEAN3='1' or BOOLEAN3='0')
        --TOPO        NUMBER(10) REFERENCES TP_Object ON DELETE SET NULL
        );
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='RESULT_POINT';
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('RESULT_POINT','GEOM',NULL,NULL);
ALTER TABLE RESULT_POINT ADD CONSTRAINT fk_resultpoint_feature FOREIGN KEY (cogitid) REFERENCES FT_FEATURE ON DELETE CASCADE;


CREATE TABLE RESULT_CURVE (
        COGITID     NUMBER(10)  PRIMARY KEY,
	GEOM        MDSYS.SDO_GEOMETRY,
        DOUBLE1     NUMBER,
        DOUBLE2     NUMBER,
        DOUBLE3     NUMBER,
        INT1        INTEGER,
        INT2        INTEGER,
        INT3        INTEGER,
        STRING1     VARCHAR2(255),
        STRING2     VARCHAR2(255),
        STRING3     VARCHAR2(255),
	BOOLEAN1    CHAR(1) CHECK (BOOLEAN1='1' or BOOLEAN1='0'),
	BOOLEAN2    CHAR(1) CHECK (BOOLEAN2='1' or BOOLEAN2='0'),
	BOOLEAN3    CHAR(1) CHECK (BOOLEAN3='1' or BOOLEAN3='0')
        --TOPO         NUMBER(10) REFERENCES TP_Object ON DELETE SET NULL
        );
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='RESULT_CURVE';
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('RESULT_CURVE','GEOM',NULL,NULL);
ALTER TABLE RESULT_CURVE ADD CONSTRAINT fk_resultcurve_feature FOREIGN KEY (cogitid) REFERENCES FT_FEATURE ON DELETE CASCADE;


CREATE TABLE RESULT_SURFACE (
        COGITID     NUMBER(10)  PRIMARY KEY,
	GEOM        MDSYS.SDO_GEOMETRY,
        DOUBLE1     NUMBER,
        DOUBLE2     NUMBER,
        DOUBLE3     NUMBER,
        INT1        INTEGER,
        INT2        INTEGER,
        INT3        INTEGER,
        STRING1     VARCHAR2(255),
        STRING2     VARCHAR2(255),
        STRING3     VARCHAR2(255),
	BOOLEAN1    CHAR(1) CHECK (BOOLEAN1='1' or BOOLEAN1='0'),
	BOOLEAN2    CHAR(1) CHECK (BOOLEAN2='1' or BOOLEAN2='0'),
	BOOLEAN3    CHAR(1) CHECK (BOOLEAN3='1' or BOOLEAN3='0')
        --TOPO        NUMBER(10) REFERENCES TP_Object ON DELETE SET NULL
        );
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='RESULT_SURFACE';
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('RESULT_SURFACE','GEOM',NULL,NULL);
ALTER TABLE RESULT_SURFACE ADD CONSTRAINT fk_resultsurface_feature FOREIGN KEY (cogitid) REFERENCES FT_FEATURE ON DELETE CASCADE;



--------------------------------------------------------------------------
-- Création de la table pour le dallage ----------------------------------
--------------------------------------------------------------------------
DROP TABLE DALLAGE;
CREATE TABLE DALLAGE (
        COGITID     NUMBER(10)  PRIMARY KEY,
	GEOM        MDSYS.SDO_GEOMETRY
        );
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME='DALLAGE';
INSERT INTO USER_SDO_GEOM_METADATA VALUES ('DALLAGE','GEOM',NULL,NULL);



--------------------------------------------------------------------------
-- Validation ------------------------------------------------------------
--------------------------------------------------------------------------
COMMIT;
