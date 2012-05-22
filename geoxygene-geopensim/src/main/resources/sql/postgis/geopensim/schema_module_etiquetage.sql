-----------------------------------------------------------------
-- SEQUENCES
-----------------------------------------------------------------

CREATE SEQUENCE utilisateur_gid_seq;
CREATE SEQUENCE annotation_ilot_gid_seq;
CREATE SEQUENCE confiance_id_seq;





-----------------------------------------------------------------
-- UTILISATEUR
-----------------------------------------------------------------


SET client_encoding = 'LATIN9';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: utilisateur; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE utilisateur (
    gid integer DEFAULT nextval('utilisateur_gid_seq'::regclass) NOT NULL,
    "login" character varying(25),
    "password" character varying(25),
    userid integer NOT NULL,
    nom character varying(25),
    prenom character varying(25),
    "type" character varying(20)
);


ALTER TABLE public.utilisateur OWNER TO postgres;

--
-- Name: utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (userid);






-----------------------------------------------------------------
-- ANNOTATION ILOT
-----------------------------------------------------------------

CREATE TABLE annotation_ilot (
    gid integer DEFAULT nextval('annotation_ilot_gid_seq'::regclass) NOT NULL,
    idilot integer,
    idutilisateur integer,
    annotation integer NOT NULL,
    contexte integer,
    confiance integer,
    version integer,
    date timestamp without time zone
);


ALTER TABLE public.annotation_ilot OWNER TO postgres;

--
-- Name: annotation_ilot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY annotation_ilot
    ADD CONSTRAINT annotation_ilot_pkey PRIMARY KEY (gid);


--
-- Name: unique_constraint; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY annotation_ilot
    ADD CONSTRAINT unique_constraint UNIQUE (idilot, idutilisateur, contexte, version);



-----------------------------------------------------------------
-- CONFIANCE
-----------------------------------------------------------------

CREATE TABLE confiance (
    id integer DEFAULT nextval('confiance_id_seq'::regclass) NOT NULL,
    hcdtcc integer,
    hdtpi integer,
    hdtcge integer,
    hdm integer,
    esb integer,
    espb integer,
    rh integer,
    autres integer,
    rc integer,
    incertitude double precision,
    commentaires text,
    hdmd integer,
    hdmpd integer,
    tumd integer,
    tumpd integer,
    classe_courante character varying(15),
    sb integer
);


ALTER TABLE public.confiance OWNER TO postgres;

--
-- Name: pk_confiance; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY confiance
    ADD CONSTRAINT pk_confiance PRIMARY KEY (id);



-----------------------------------------------------------------
-- QUADRILLAGE
-----------------------------------------------------------------



CREATE TABLE quadrillage (
    gid integer NOT NULL,
    nom_ville character varying(30),
    i integer,
    j integer,
    geom geometry,
    CONSTRAINT enforce_dims_geom CHECK ((ndims(geom) = 2)),
    CONSTRAINT enforce_geotype_geom CHECK (((geometrytype(geom) = 'POLYGON'::text) OR (geom IS NULL))),
    CONSTRAINT enforce_srid_geom CHECK ((srid(geom) = -1))
);


ALTER TABLE public.quadrillage OWNER TO postgres;

--
-- Name: quadrillage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY quadrillage
    ADD CONSTRAINT quadrillage_pkey PRIMARY KEY (gid);



-----------------------------------------------------------------
-- CLASSE APPRISE
-----------------------------------------------------------------


CREATE TABLE classe_apprise (
    idilot integer NOT NULL,
    idutilisateur integer NOT NULL,
    version integer NOT NULL,
    contexte integer NOT NULL,
    classe character varying(20),
    indice_de_confiance double precision
);


ALTER TABLE public.classe_apprise OWNER TO postgres;

--
-- Name: classe_apprise_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY classe_apprise
    ADD CONSTRAINT classe_apprise_pkey PRIMARY KEY (idilot, idutilisateur, version, contexte);



-----------------------------------------------------------------
-- FONCTIONS 
-----------------------------------------------------------------

CREATE OR REPLACE FUNCTION getminx(nomtable varchar(30)) RETURNS double precision as $$
DECLARE
res record;
BEGIN
       FOR res in EXECUTE 'select min(x(pointn(exteriorring(envelope(geom)),1))) from '||quote_ident(nomtable) LOOP
       	   RETURN res.min;
       END LOOP;
END;
$$LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION getminy(nomtable varchar(30)) RETURNS double precision as $$
DECLARE
res record;
BEGIN
       FOR res in EXECUTE 'select min(y(pointn(exteriorring(envelope(geom)),1))) from '||quote_ident(nomtable) LOOP
       	   RETURN res.min;
       END LOOP;
END;
$$LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getmaxx(nomtable varchar(30)) RETURNS double precision as $$
DECLARE
res record;
BEGIN
       FOR res in EXECUTE 'select max(x(pointn(exteriorring(envelope(geom)),4))) from '||quote_ident(nomtable) LOOP
       	   RETURN res.max;
       END LOOP;
END;
$$LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getmaxy(nomtable varchar(30)) RETURNS double precision as $$
DECLARE
res record;
BEGIN
       FOR res in EXECUTE 'select max(y(pointn(exteriorring(envelope(geom)),4))) from '||quote_ident(nomtable) LOOP
       	   RETURN res.max;
       END LOOP;
END;
$$LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION classes_pures_seulement(t_ligne confiance) RETURNS text AS $$
DECLARE
classes text;
cpt integer;
BEGIN
	classes := '';	
	cpt := 0;

	IF t_ligne.hcdtcc = 3 THEN
		classes := 'hcdtcc';
		cpt := cpt+1;
	END IF;
	IF  t_ligne.hdtpi = 3 THEN
		classes := 'hdtpi';
		cpt := cpt+1;
	END IF;
	IF  t_ligne.hdtcge = 3 THEN
		classes := 'hdtcge';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.hdmd = 3 THEN
		classes := 'hdmd';
		cpt := cpt+1;
	END IF;
	
	IF  t_ligne.hdmpd = 3 THEN
		classes := 'hdmpd';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.esb = 3 THEN
		classes := 'esb';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.espb = 3 THEN
		classes := 'espb';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.rh = 3 THEN
		classes := 'rh';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.tumd = 3 THEN
		classes := 'tumd';
		cpt := cpt+1;
	END IF;
	
	IF  t_ligne.tumpd = 3 THEN
		classes := 'tumpd';
		cpt := cpt+1;
	END IF;

	IF  t_ligne.rc = 3 THEN
		classes := 'rc';
		cpt := cpt+1;
	END IF;
	IF  t_ligne.sb = 3 THEN
		classes := 'sb';
		cpt := cpt+1;
	END IF;

	IF cpt = 1 THEN
		RETURN classes;
	END IF;
	RETURN '';
END;
$$ LANGUAGE plpgsql;








