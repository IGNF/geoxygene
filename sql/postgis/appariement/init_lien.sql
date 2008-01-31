------------------------------------------------------------------------------
-- SCRIPT DE CREATION DEs TABLES RELATIVES AUX LIENS ET AUX ENSEMBLES DE LIENS
------------------------------------------------------------------------------

-- script de destruction
-- DROP TABLE ensemble_liens;
-- DROP TABLE lien;
-- SELECT DropGeometryColumn('lien','geom');


CREATE TABLE ensemble_liens (
    cogitid INTEGER PRIMARY KEY,
    nom VARCHAR(255),
    parametrage VARCHAR(255),
    evalinterne VARCHAR(255),
    evalglobale VARCHAR(255),
    dateheure VARCHAR(100),
    populations VARCHAR(1000),
    rouge INTEGER,
    vert INTEGER,
    bleu INTEGER
    );

CREATE TABLE lien (
    cogitid INTEGER PRIMARY KEY,
    objetsref VARCHAR(1000),
    objetscomp VARCHAR(1000),
    evaluation DOUBLE PRECISION,
    indicateurs VARCHAR(255),
    commentaire VARCHAR(255),
    nom VARCHAR(255),
    type VARCHAR(255),
    reference VARCHAR(255),
    comparaison VARCHAR(255),
    ensliensid INTEGER
    );
SELECT AddGeometryColumn('','lien','geom','-1','GEOMETRYCOLLECTION',2);

commit;