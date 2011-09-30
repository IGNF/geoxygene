package fr.ign.cogit.appli.geopensim.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaire;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
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
public class ReconstruireVoisinage {
    static Logger logger = Logger.getLogger(ReconstruireVoisinage.class
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
		IFeatureCollection<ZoneElementaireUrbaine> collection = DataSet.db.loadAllFeatures(ZoneElementaireUrbaine.class);
        logger.info("Loading features took "
                + (System.currentTimeMillis() - time) + " ms");
        logger.info("Qualifying features");
        time = System.currentTimeMillis();
        for (ZoneElementaireUrbaine unite : collection) {
            Set<ZoneElementaire> voisins = new HashSet<ZoneElementaire>(0);
            Set<Troncon> troncons = unite.getTroncons();
            for (Troncon troncon : troncons) {
                voisins.addAll(troncon.getZonesElementaires());
            }
            voisins.remove(unite);
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
