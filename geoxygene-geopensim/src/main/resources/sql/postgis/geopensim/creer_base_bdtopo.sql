DROP TABLE agentgeo;
DROP TABLE batiment;
DROP TABLE carrefour;
DROP TABLE cimetiere;
DROP TABLE zone_elementaire_urbaine;
DROP TABLE zone_elementaire_peri_urbaine;
DROP TABLE infrastructure_communication;
DROP TABLE parking;
DROP TABLE relation_zone_elem_zone_elem;
DROP TABLE relation_zone_elem_troncon;
DROP TABLE surface_eau;
DROP TABLE terrain_sport;
DROP TABLE troncon_chemin;
DROP TABLE troncon_cours_eau;
DROP TABLE troncon_route;
DROP TABLE troncon_voie_ferree;
DROP TABLE vegetation;
DROP TABLE unite_urbaine;
DROP TABLE unite_peri_urbaine;
DROP TABLE espace_vide;
DROP TABLE relation_agent_agent;
DROP TABLE groupe_batiments;
DROP TABLE alignement;
DROP TABLE relation_alignement_batiment;


CREATE TABLE agentgeo
(
  cogitid integer NOT NULL,
  rep_c_name character varying,
  dates character varying,
  class_name character varying,
  CONSTRAINT agentgeo_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE batiment
(
  cogitid integer NOT NULL,
  nature character varying,
  hauteur integer,
  groupe integer,
  aire double precision,
  typefonct integer,
  distbatpp double precision,
  disttrcpp double precision,
  estbatipp integer,
  source character varying,
  class_name character varying,
  datediff integer,
  datemes integer,
  datesource integer,
  zoneelem integer,
  idgeo integer,
  changement integer,
  simule boolean,
  elongation double precision,
  convexite double precision,
  biscornuite character varying,
  orientgenerale double precision,
  orientmurs double precision,
  orientgeneroute double precision,
  orientmursroute double precision,
  estorienteroute boolean,
  estparalleleroute boolean,
  CONSTRAINT batiment_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE carrefour
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  ectdist double precision,
  simule boolean,
  CONSTRAINT carrefour_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE cimetiere
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  changement integer,
  simule boolean,
  idgeo integer,
  CONSTRAINT cimetiere_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE zone_elementaire_urbaine
(
  densite double precision,
  bordeunite boolean,
  nbtrous integer,
  nbbattrous integer,
  nbtroncons integer,
  nbbat integer,
  elongation double precision,
  convexite double precision,
  moyairebat double precision,
  ectairebat double precision,
  maxairebat double precision,
  minairebat double precision,
  medairebat double precision,
  moyelonbat double precision,
  ectelonbat double precision,
  moyconvbat double precision,
  ectconvbat double precision,
  homtypfbat integer,
  homtailbat integer,
  typefonct integer,
  taillebat integer,
  classifonc integer,
  classitail integer,
  distailbrv integer,
  aire double precision,
  class_name character varying,
  cogitid integer NOT NULL,
  esttroude integer,
  unite integer,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  moydistbat double precision,
  dmoyppbat double precision,
  distcentre integer,
  rappbati double precision,
  distroute double precision,
  CONSTRAINT zone_elementaire_urbaine_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE zone_elementaire_peri_urbaine
(
  bordeunite boolean,
  nbtrous integer,
  nbtroncons integer,
  elongation double precision,
  convexite double precision,
  aire double precision,
  class_name character varying,
  cogitid integer NOT NULL,
  esttroude integer,
  unite integer,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  distroute double precision,
  CONSTRAINT zone_elementaire_peri_urbaine_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE infrastructure_communication
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT infrastructure_communication_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE parking
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT parking_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE relation_zone_elem_zone_elem
(
  zoneelem integer,
  voisin integer
);

CREATE TABLE relation_zone_elem_troncon
(
  zoneelem integer,
  troncon integer
);

CREATE TABLE surface_eau
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT surface_eau_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE terrain_sport
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT terrain_sport_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE troncon_chemin
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT troncon_chemin_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE troncon_cours_eau
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT troncon_cours_eau_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE troncon_route
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  impasse boolean,
  CONSTRAINT troncon_route_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE troncon_voie_ferree
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT troncon_voie_ferree_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE vegetation
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  CONSTRAINT vegetation_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE unite_urbaine
(
  cogitid integer NOT NULL,
  densite double precision,
  moydenszon double precision,
  typseltail integer,
  elongation double precision,
  convexite double precision,
  moyairebat double precision,
  ectairebat double precision,
  maxairebat double precision,
  minairebat double precision,
  medairebat double precision,
  moyelonbat double precision,
  ectelonbat double precision,
  moyconvbat double precision,
  ectconvbat double precision,
  homtypfbat integer,
  homtailbat integer,
  nbbat integer,
  aire double precision,
  class_name character varying,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  CONSTRAINT unite_urbaine_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE unite_peri_urbaine
(
  cogitid integer NOT NULL,
  elongation double precision,
  convexite double precision,
  aire double precision,
  class_name character varying,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  CONSTRAINT unite_peri_urbaine_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE espace_vide
(
  nature character varying,
  source character varying,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement integer,
  simule boolean,
  zoneelem integer,
  elongation double precision,
  importance double precision,
  orient double precision,
  ouvert boolean,
  CONSTRAINT espace_vide_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE relation_agent_agent
(
  pred integer,
  succ integer
);

CREATE TABLE groupe_batiments
(
  densite double precision,
  nbbat integer,
  elongation double precision,
  convexite double precision,
  moyairebat double precision,
  ectairebat double precision,
  maxairebat double precision,
  minairebat double precision,
  medairebat double precision,
  moyelonbat double precision,
  ectelonbat double precision,
  moyconvbat double precision,
  ectconvbat double precision,
  homtypfbat integer,
  homtailbat integer,
  taillebat integer,
  classitail integer,
  distailbrv integer,
  aire double precision,
  class_name character varying,
  cogitid integer NOT NULL,
  unite integer,
  zoneelem integer,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  CONSTRAINT groupe_batiments_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE alignement
(
  densite double precision,
  nbbat integer,
  noteaire double precision,
  noteconv double precision,
  notedist double precision,
  noteetir double precision,
  notegenerale double precision,
  elongation double precision,
  convexite double precision,
  moyairebat double precision,
  ectairebat double precision,
  maxairebat double precision,
  minairebat double precision,
  medairebat double precision,
  moyelonbat double precision,
  ectelonbat double precision,
  moyconvbat double precision,
  ectconvbat double precision,
  homtypfbat integer,
  homtailbat integer,
  taillebat integer,
  classitail integer,
  distailbrv integer,
  aire double precision,
  class_name character varying,
  cogitid integer NOT NULL,
  unite integer,
  zoneelem integer,
  groupe integer,
  changement integer,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  idgeo integer,
  CONSTRAINT alignement_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE relation_alignement_batiment
(
   batiment integer, 
   alignement integer
);

SELECT AddGeometrycolumn ('','batiment','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','carrefour','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','cimetiere','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','zone_elementaire_urbaine','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','zone_elementaire_urbaine','pointcentr','-1','POINT',2);
SELECT AddGeometrycolumn ('','zone_elementaire_peri_urbaine','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','infrastructure_communication','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','parking','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','surface_eau','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','terrain_sport','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','troncon_chemin','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','troncon_cours_eau','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','troncon_route','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','troncon_voie_ferree','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','vegetation','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','unite_urbaine','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','unite_peri_urbaine','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','espace_vide','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','groupe_batiments','geom','-1','GEOMETRY',4);
SELECT AddGeometrycolumn ('','alignement','geom','-1','GEOMETRY',4);

ALTER TABLE batiment DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE carrefour DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE cimetiere DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE zone_elementaire_urbaine DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE zone_elementaire_peri_urbaine DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE infrastructure_communication DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE parking DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE surface_eau DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE terrain_sport DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE troncon_chemin DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE troncon_cours_eau DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE troncon_route DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE troncon_voie_ferree DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE vegetation DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE unite_urbaine DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE unite_peri_urbaine DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE espace_vide DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE groupe_batiments DROP CONSTRAINT enforce_dims_geom;
ALTER TABLE alignement DROP CONSTRAINT enforce_dims_geom;

