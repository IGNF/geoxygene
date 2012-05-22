UPDATE ilot SET class_name = 'fr.ign.cogit.appli.geopensim.feature.meso.Ilot';
UPDATE ville SET class_name = 'fr.ign.cogit.appli.geopensim.feature.meso.Ville';

UPDATE batiment SET class_name = 'fr.ign.cogit.appli.geopensim.feature.bdtopo2.Batiment2';
UPDATE troncon_route SET class_name = 'fr.ign.cogit.appli.geopensim.feature.bdtopo2.TronconRoute2';
UPDATE troncon_chemin SET class_name = 'fr.ign.cogit.appli.geopensim.feature.bdtopo2.TronconChemin2';
UPDATE troncon_cours_eau SET class_name = 'fr.ign.cogit.appli.geopensim.feature.bdtopo2.TronconCoursEau2';
UPDATE troncon_voie_ferree SET class_name = 'fr.ign.cogit.appli.geopensim.feature.bdtopo2.TronconVoieFerree2';

ALTER TABLE ilot DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE ville DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE batiment DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE troncon_route DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE troncon_chemin DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE troncon_cours_eau DROP CONSTRAINT enforce_srid_geom;
ALTER TABLE troncon_voie_ferree DROP CONSTRAINT enforce_srid_geom;
