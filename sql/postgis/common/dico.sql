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


DROP TABLE GF_CONSTRAINT;
DROP TABLE GF_OPERATION;
DROP TABLE GF_ASSOCIATIONROLE;
DROP TABLE GF_ASSOCIATION_FEATURE;
DROP TABLE GF_ATTRIBUTETYPE;
DROP TABLE GF_ASSOCIATIONTYPE;
DROP TABLE GF_INHERITANCERELATION;
DROP TABLE GF_FEATURETYPE;


CREATE TABLE GF_FEATURETYPE (
	GF_FeatureTypeID	INTEGER PRIMARY KEY,
	typeName 			VARCHAR(255)   UNIQUE,
	definition			VARCHAR(2000),
	isAbstract			CHAR(1)	CHECK (isAbstract IN ('0','1'))
        );

CREATE TABLE GF_INHERITANCERELATION (
	GF_InheritanceRelationID    INTEGER PRIMARY KEY,
	name                        VARCHAR(255),
	description                 VARCHAR(2000),
	uniqueInstance              CHAR(1)  CHECK (uniqueInstance IN ('0','1')),
	subType                     INTEGER,
	superType                   INTEGER
        );

-- Attention : herite de GF_FEATURETYPE
CREATE TABLE GF_ASSOCIATIONTYPE (
	GF_AssociationTypeID	INTEGER	PRIMARY KEY,
	typeName 				VARCHAR(255)   UNIQUE,
	definition				VARCHAR(2000),
	isAbstract				CHAR(1)	CHECK (isAbstract IN ('0','1'))
        );

-- implementation de linkBeetween / memberOf
CREATE TABLE GF_ASSOCIATION_FEATURE (
	GF_FeatureTypeID		INTEGER,
	GF_AssociationTypeID 	INTEGER
        );

CREATE TABLE GF_ASSOCIATIONROLE (
	GF_PropertyTypeID	INTEGER	PRIMARY KEY,
	GF_AssociationTypeID	INTEGER,
	GF_FeatureTypeID		INTEGER,
	memberName				VARCHAR(255),
	definition				VARCHAR(2000),
	valueType				VARCHAR(255),
	cardMin					INTEGER,
	cardMax					INTEGER,
	UNIQUE (GF_FeatureTypeID,memberName)
        );

CREATE TABLE GF_ATTRIBUTETYPE (
	GF_PropertyTypeID	INTEGER	PRIMARY KEY,
	GF_FeatureTypeID	INTEGER,
	memberName			VARCHAR(255),
	definition			VARCHAR(2000),
	valueType			VARCHAR(255),
	domainOfValues		VARCHAR(2000),
	cardMin				INTEGER,
	cardMax				INTEGER,
	characterizeID		INTEGER,
	UNIQUE (GF_FeatureTypeID,memberName)
        );

CREATE TABLE GF_OPERATION (
	GF_PropertyTypeID	INTEGER	PRIMARY KEY,
	GF_FeatureTypeID	INTEGER,
	memberName			VARCHAR(255),
	definition			VARCHAR(2000),
	signature			VARCHAR(2000),
	UNIQUE (GF_FeatureTypeID,memberName)	
        );

CREATE TABLE GF_CONSTRAINT (
	GF_ConstraintID		INTEGER	PRIMARY KEY,
	description			VARCHAR(2000),
	GF_FeatureTypeID	INTEGER,
	GF_PropertyTypeID	INTEGER
        );

