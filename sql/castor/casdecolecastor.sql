/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

DROP TABLE AUTRE_MERE CASCADE CONSTRAINTS
;

DROP TABLE MERE CASCADE CONSTRAINTS
;

DROP TABLE FILLE2 CASCADE CONSTRAINTS
;

DROP TABLE FILLE1 CASCADE CONSTRAINTS
;

DROP TABLE AUTRE_FILLE CASCADE CONSTRAINTS
;

DROP TABLE DEPEND CASCADE CONSTRAINTS
;

CREATE TABLE AUTRE_MERE(
ID NUMBER(10) NOT NULL,
CL_MERE_ID NUMBER(10)   -- ajout apres generation automatique
, CONSTRAINT PK_AUTRE_MERE PRIMARY KEY (ID)
)
;


CREATE TABLE MERE(
CLASSTYPE VARCHAR2(50) NOT NULL,
ID NUMBER(10) NOT NULL,
FIELD0 NUMBER(10) NULL,
FIELD1 VARCHAR2(50) NULL,
FIELD2 NUMBER(1) NULL,
AUTRE_MERE_ID NUMBER(10) NULL
, CONSTRAINT PK_MERE PRIMARY KEY (ID)
)
;

CREATE INDEX MERE_FIELD0_IDX ON MERE (FIELD0)
;

ALTER TABLE MERE ADD CONSTRAINT FK_MERE_AUTRE_MERE FOREIGN KEY (AUTRE_MERE_ID) REFERENCES AUTRE_MERE(ID)
;


CREATE TABLE FILLE2(
ID NUMBER(10) NOT NULL,
FIELD4 NUMBER(10) NULL
, CONSTRAINT PK_FILLE2 PRIMARY KEY (ID)
)
;

ALTER TABLE FILLE2 ADD CONSTRAINT FK_FILLE2_ID FOREIGN KEY (ID) REFERENCES MERE(ID)
;


CREATE TABLE FILLE1(
ID NUMBER(10) NOT NULL,
FIELD3 NUMBER(34,17) NULL
, CONSTRAINT PK_FILLE1 PRIMARY KEY (ID)
)
;

ALTER TABLE FILLE1 ADD CONSTRAINT FK_FILLE1_ID FOREIGN KEY (ID) REFERENCES MERE(ID)
;


CREATE TABLE AUTRE_FILLE(
ID NUMBER(10) NOT NULL,
FIELD0 NUMBER(10) NULL
, CONSTRAINT PK_AUTRE_FILLE PRIMARY KEY (ID)
)
;

ALTER TABLE AUTRE_FILLE ADD CONSTRAINT FK_AUTRE_FI_ID FOREIGN KEY (ID) REFERENCES AUTRE_MERE(ID)
;


CREATE TABLE DEPEND(
ID NUMBER(10) NOT NULL,
FILLE2_ID NUMBER(10) NULL
, CONSTRAINT PK_DEPEND PRIMARY KEY (ID)
)
;

ALTER TABLE DEPEND ADD CONSTRAINT FK_DEPEND_FILLE2_I FOREIGN KEY (FILLE2_ID) REFERENCES FILLE2(ID)
;

-- ajout apres generation automatique
DROP TABLE LIEN_NM;
CREATE TABLE LIEN_NM (
CL_MERE_ID  NUMBER(10)  REFERENCES MERE (ID),
AUTRE_FILLE_ID   NUMBER(10)  REFERENCES AUTRE_FILLE (ID)
);
