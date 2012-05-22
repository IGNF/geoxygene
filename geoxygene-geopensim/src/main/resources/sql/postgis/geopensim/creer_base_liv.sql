CREATE TABLE elementgeo
(
   cogitid integer NOT NULL,
   rep_c_name varchar,
   dates character varying,
   CONSTRAINT elementgeo_pkey PRIMARY KEY (cogitid)
) ;

CREATE TABLE relation_ilot_troncon
(
  ilot int4,
  troncon int4
) ;


CREATE TABLE relation_ilot_ilot
(
  ilot int4,
  voisin int4
) ;

CREATE TABLE ilot
(
  densite float8,
  bordeville bool,
  nbtrous int4,
  nbbattrous int4,
  nbtroncons int4,
  nbbat int4,
  elongation float8,
  convexite float8,
  moyairebat float8,
  ectairebat float8,
  maxairebat float8,
  minairebat float8,
  medairebat float8,
  moyelonbat float8,
  ectelonbat float8,
  moyconvbat float8,
  ectconvbat float8,
  homtypfbat int4,
  homtailbat int4,
  typefonct int4,
  taillebat int4,
  classifonc int4,
  classitail int4,
  distailbrv int4,
  aire float8,
  class_name varchar,
  geom geometry,
  cogitid int4 NOT NULL,
  esttroude int4,
  ville int4,
  changement int4,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  CONSTRAINT ilot_pkey PRIMARY KEY (cogitid)
) ;

CREATE TABLE ville
(
  cogitid integer NOT NULL,
  densite double precision,
  moydensilo double precision,
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
  geom geometry,
  changement int4,
  datediff integer,
  datemes integer,
  datesource integer,
  simule boolean,
  CONSTRAINT ville_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE batiment
(
  cogitid integer NOT NULL,
  nature character varying,
  hauteur integer,
  aire double precision,
  typefonct integer,
  source character varying,
  class_name character varying,
  datediff integer,
  datemes integer,
  datesource integer,
  ilot integer,
  idgeo integer,
  geom geometry,
  changement int4,
  simule boolean,
  CONSTRAINT batiment_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE troncon_chemin
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT troncon_chemin_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE troncon_cours_eau
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT troncon_cours_eau_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE troncon_route
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT troncon_route_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE troncon_voie_ferree
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT troncon_voie_ferree_pkey PRIMARY KEY (cogitid) 
);

UPDATE ilot SET class_name = 'fr.ign.cogit.appli.geopensim.feature.meso.Ilot';
UPDATE ville SET class_name = 'fr.ign.cogit.appli.geopensim.feature.meso.Ville';

UPDATE batiment SET class_name = 'fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment';
UPDATE troncon_route SET class_name = 'fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconRoute';
UPDATE troncon_chemin SET class_name = 'fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconChemin';
UPDATE troncon_cours_eau SET class_name = 'fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconCoursEau';
UPDATE troncon_voie_ferree SET class_name = 'fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconVoieFerree';

CREATE TABLE cimetiere
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT cimetiere_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE parking
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT parking_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE carrefour
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  ectdist double precision,
  simule boolean,
  CONSTRAINT carrefour_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE surface_eau
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT surface_eau_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE vegetation
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT vegetation_pkey PRIMARY KEY (cogitid)
);
CREATE TABLE terrain_sport
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT terrain_sport_pkey PRIMARY KEY (cogitid)
);

CREATE TABLE infrastructure_communication
(
  nature character varying,
  source character varying,
  geom geometry,
  class_name character varying,
  cogitid integer NOT NULL,
  datediff integer,
  datemes integer,
  datesource integer,
  idgeo integer,
  changement int4,
  simule boolean,
  CONSTRAINT infrastructure_communication_pkey PRIMARY KEY (cogitid)
);

INSERT INTO geometry_columns VALUES('','public','batiment','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','cimetiere','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','ilot','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','parking','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','carrefour','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','surface_eau','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','terrain_sport','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','infrastructure_communication','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','troncon_chemin','geom','4', '27571', 'MULTILINESTRING');
INSERT INTO geometry_columns VALUES('','public','troncon_cours_eau','geom','4', '27571', 'MULTILINESTRING');
INSERT INTO geometry_columns VALUES('','public','troncon_route','geom','4', '27571', 'MULTILINESTRING');
INSERT INTO geometry_columns VALUES('','public','troncon_voie_ferree','geom','4', '27571', 'MULTILINESTRING');
INSERT INTO geometry_columns VALUES('','public','vegetation','geom','4', '27571', 'MULTIPOLYGON');
INSERT INTO geometry_columns VALUES('','public','ville','geom','4', '27571', 'MULTIPOLYGON');

