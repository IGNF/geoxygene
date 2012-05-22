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


drop table AAA ;
drop table BBB ;
drop table RELATION_BI_NN_AAA_BBB ;
drop table RELATION_MONO_NN_AAA_BBB ;

-- VERSION SANS BIDIRECTION AU NIVEAU MAPPING ET XML (voir commentaires sur XML)

create table AAA (
    ID integer primary key, --identifiant
    OBJETBBB_BI11 integer,  --relation 1-1 bidirectionnelle
    OBJETBBB_MONO11 integer,  --relation vers 1 objet monodirectionnelle
    NOM VARCHAR2(10)
    );

create table BBB (
    ID integer primary key, --identifiant
    OBJETAAA_BI1N integer,  --relation 1-n bidirectionnelle
    OBJETAAA_MONO1N integer, --relation 1-n monodirectionnelle
    NOM VARCHAR2(10)
    );

--relation n-m bidirectionnelle
create table RELATION_BI_NN_AAA_BBB (
    OBJET_AAA integer,
    OBJET_BBB integer
);

--relation n-m monodirectionnelle
create table RELATION_MONO_NN_AAA_BBB (
    OBJET_AAA integer,
    OBJET_BBB integer
);

-- note : pour etre rigoureux, il faudrait creer des contraintes d'integrite sur les cles etrangeres

-- les index sont optionnels. Leur apport n'a pas vraiment été testé. A creuser.
create index BI11_AAA_IDX on AAA(OBJETBBB_BI11); 
create index MONO11_AAA_IDX on AAA(OBJETBBB_MONO11); 
create index BI1N_BBB_IDX on BBB(OBJETAAA_BI1N); 
create index MONO1N_BBB_IDX on BBB(OBJETAAA_MONO1N); 
create index BINM_AAA_IDX on RELATION_BI_NN_AAA_BBB(OBJET_AAA); 
create index BINM_BBB_IDX on RELATION_BI_NN_AAA_BBB(OBJET_BBB); 
create index MONONM_AAA_IDX on RELATION_MONO_NN_AAA_BBB(OBJET_AAA); 
create index MONONM_BBB_IDX on RELATION_MONO_NN_AAA_BBB(OBJET_BBB); 

