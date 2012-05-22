package fr.ign.cogit.appli.geopensim.util;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Petite application pour recharger les Unités, les requalifier et les sauver.
 * Au cas où la qualification n'a pas bien fonctionné pendant la construction, 
 * cette Méthode peut aider à corriger ça.
 * 
 * @author Julien Perret
 */
public class SaveAsShapefiles {
    static Logger logger = Logger.getLogger(SaveAsShapefiles.class
            .getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String path = "D:\\Users\\JulienPerret\\Data\\Strasbourg\\GeOpenSim_Strasbourg_1989\\";
        logger.info("Opening the database");
		DataSet.db = GeodatabaseOjbFactory.newInstance();
		DataSet.db.begin();
        logger.info("Loading features");
        long time = System.currentTimeMillis();
        IFeatureCollection<UniteUrbaine> collectionBatiment = DataSet.db.loadAllFeatures(Batiment.class);
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Saving features");
        ShapefileWriter.write(collectionBatiment, path + "batiment.shp");
        collectionBatiment.clear();
        System.gc();
        time = System.currentTimeMillis();
        logger.info("Saving features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Loading features");
        time = System.currentTimeMillis();
		IFeatureCollection<UniteUrbaine> collection = DataSet.db.loadAllFeatures(UniteUrbaine.class);
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Saving features");
        ShapefileWriter.write(collection, path + "unite_urbaine.shp");
        time = System.currentTimeMillis();
        logger.info("Saving features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Loading features");
        time = System.currentTimeMillis();
        FT_FeatureCollection<ZoneElementaireUrbaine> collectionZone = new FT_FeatureCollection<ZoneElementaireUrbaine>();
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Saving features");
        ShapefileWriter.write(collectionZone, path + "zone_elementaire_urbaine.shp");
        time = System.currentTimeMillis();
        logger.info("Saving features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Loading features");
        time = System.currentTimeMillis();
        FT_FeatureCollection<ZoneElementaireUrbaine> collectionRoute = new FT_FeatureCollection<ZoneElementaireUrbaine>();
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Saving features");
        ShapefileWriter.write(collectionRoute, path + "troncon_route.shp");
        time = System.currentTimeMillis();
        logger.info("Saving features took "
                + (System.currentTimeMillis() - time) + " ms");
	}
}
