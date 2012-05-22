package fr.ign.cogit.appli.geopensim.util;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * Petite application pour recharger les Unités, les requalifier et les sauver.
 * Au cas où la qualification n'a pas bien fonctionné pendant la construction, 
 * cette Méthode peut aider à corriger ça.
 * 
 * @author Julien Perret
 */
public class ReQualifierUnites {
    static Logger logger = Logger.getLogger(ReQualifierUnites.class
            .getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        logger.info("Opening the database");
		DataSet.db = GeodatabaseOjbFactory.newInstance();
		DataSet.db.begin();
        logger.info("Loading features");
        long time = System.currentTimeMillis();
		IFeatureCollection<UniteUrbaine> collection = DataSet.db.loadAllFeatures(UniteUrbaine.class);
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Qualifying features");
        time = System.currentTimeMillis();
        for (UniteUrbaine unite : collection) {
            unite.qualifier();
        }
        logger.info("Qualifying features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Committing features");
        time = System.currentTimeMillis();
		DataSet.db.commit();
        logger.info("Committing features took "
                + (System.currentTimeMillis() - time) + " ms");
	}
}
