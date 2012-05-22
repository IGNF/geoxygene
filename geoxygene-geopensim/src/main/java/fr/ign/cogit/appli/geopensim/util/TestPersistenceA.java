/**
 * 
 */
package fr.ign.cogit.appli.geopensim.util;

import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * @author Julien Perret
 *
 */
public class TestPersistenceA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		TestA t=new TestA();
		t.setA(9);
		t.setGeom(new GM_Point(new DirectPosition(999,111)));
		 */

		DataSet.db=new GeodatabaseOjbPostgis();
		/*
		DataSet.db.begin();
		DataSet.db.makePersistent(t);
		DataSet.db.commit();
		 */
		DataSet.db.begin();
		IFeatureCollection<?> zones = DataSet.db.loadAllFeatures(ZoneElementaireUrbaine.class);
		for(Object o:zones) {
			ZoneElementaireUrbaine zone = (ZoneElementaireUrbaine) o;
			System.out.println(zone.toString());
		}
		DataSet.db.close();

	}

}
