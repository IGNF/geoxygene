DROP TABLE IF EXISTS node, way, relation, relationmember;
/* Creates OSM nodes table */
CREATE TABLE node (
idnode BIGINT, -- idnode is composed of id + vnode
id BIGINT,
uid BIGINT,
vnode INTEGER,
changeset INTEGER,
username VARCHAR,
datemodif TIMESTAMP WITH TIME ZONE,
tags HSTORE,
lat NUMERIC,
lon NUMERIC,
geom geometry(POINT,4326)
);
CREATE INDEX node_geom_gist ON node USING GIST (geom);
CREATE INDEX id_idx ON node (id);

/* Creates OSM ways table */
CREATE TABLE way (
idway BIGINT, -- idway is composed of id + vway
id BIGINT,
uid BIGINT,
vway INTEGER,
changeset INTEGER,
username VARCHAR,
datemodif TIMESTAMP WITH TIME ZONE,
tags HSTORE,
composedof BIGINT[] -- array of the nodes that compose the way
);
CREATE INDEX id_way_idx ON way (id);

/* Creates OSM relations table */
CREATE TABLE relation (
idrel BIGINT, -- idrel is composed of id + vrel
id BIGINT,
uid BIGINT,
vrel INTEGER,
changeset INTEGER,
username VARCHAR,
datemodif TIMESTAMP WITH TIME ZONE,
tags HSTORE);
CREATE INDEX id_relation_idx ON relation (id);


CREATE TABLE relationmember (
idrel BIGINT,-- idrel is composed of id + vrel
idmb BIGINT,
idrelmb VARCHAR,-- idrelmb is composed of idrel + idmb
typemb VARCHAR, -- member type: way or node
rolemb VARCHAR -- member role in the relation : outer or inner in case of a multipolygon relation
);
CREATE INDEX idmb_idx ON relationmember (idmb);