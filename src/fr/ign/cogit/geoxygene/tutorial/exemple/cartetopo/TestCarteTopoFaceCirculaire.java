package fr.ign.cogit.geoxygene.tutorial.exemple.cartetopo;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.tutorial.data.BdTopoTrRoute;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/** Exemple d'utilisation de la carte topologique : 
 *  D�tection des faces circulaires.
 * 
 *  @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TestCarteTopoFaceCirculaire {

	public static void main(String[] args) {
	
		//Initialisation de la connexion à la base de données
		Geodatabase geodb = GeodatabaseOjbFactory.newInstance();

		//Chargement des données
		
		//données BDTopo
		FT_FeatureCollection<BdTopoTrRoute> tronconsBDT = geodb.loadAllFeatures(BdTopoTrRoute.class);		
		
		//création de la carte topologique
		CarteTopo carteTopo = CarteTopoFactory.creeCarteTopoDefaut(tronconsBDT);

		//D�tection des faces circulaires
		FT_FeatureCollection<Face> facesCirculaires =
			CarteTopoAlgorithmie.detectionFacesCirculaires(carteTopo);

		//Affichage
		
		//Initiatlisation du visualisateur
		ObjectViewer viewer = new ObjectViewer();
		viewer.addFeatureCollection(tronconsBDT,"Tronçons routiers");
		viewer.addFeatureCollection(facesCirculaires,"Faces circulaires");
	}

}