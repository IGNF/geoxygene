package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class CarteTopoFactory {
  static Logger logger = Logger.getLogger(CarteTopoFactory.class);

  /**
   * Create a topological map from a feature collection, a name and a threshold.
   * The threshold is used for node creation as well as for topology and planar
   * map creations.
   * <p>
   * Création d'une CarteTopo à partir d'une FT_FeatureCollection, d'un nom et
   * d'un seuil. Ce dernier sert à créer les noeuds manquants, la topologie
   * arc-noeud et pour rendre le graphe planaire.
   * @param name name of the topo map
   * @param collection collection de features
   * @param threshold threshold used for all the things mentioned above
   * @return a topological map
   */
  public static CarteTopo newCarteTopo(String name,
      FT_FeatureCollection<? extends FT_Feature> collection, double threshold) {
    // Initialisation d'une nouvelle CarteTopo
    CarteTopo carteTopo = new CarteTopo(name);
    carteTopo.setBuildInfiniteFace(false);
    // Récupération des arcs de la carteTopo
    Population<Arc> arcs = carteTopo.getPopArcs();
    // Import des arcs de la collection dans la carteTopo
    for (FT_Feature feature : collection) {
      // création d'un nouvel élément
      Arc arc = arcs.nouvelElement();
      // affectation de la géométrie de l'objet issu de la collection
      // à l'arc de la carteTopo
      arc.setGeometrie((GM_LineString) feature.getGeom());
      // instanciation de la relation entre l'arc créé et l'objet
      // issu de la collection
      arc.addCorrespondant(feature);
    }
    CarteTopoFactory.logger.info("Nombre d'arcs créés : "
        + carteTopo.getPopArcs().size());
    // Création des noeuds manquants
    CarteTopoFactory.logger.info("Création des noeuds manquants");
    carteTopo.creeNoeudsManquants(threshold);
    // Création de la topologie Arcs Noeuds
    CarteTopoFactory.logger.info("Création de la topologie Arcs Noeuds");
    carteTopo.creeTopologieArcsNoeuds(threshold);
    // La carteTopo est rendue planaire
    CarteTopoFactory.logger.info("La carte topologique est rendue planaire");
    carteTopo.rendPlanaire(threshold);
    CarteTopoFactory.logger.info("Création des faces de la carte topologique");
    // Création des faces de la carteTopo
    carteTopo.creeTopologieFaces();
    CarteTopoFactory.logger.info("Nombre de faces créées : "
        + carteTopo.getPopFaces().size());
    return carteTopo;
  }

  public static CarteTopo newCarteTopo(String name,
      FT_FeatureCollection<? extends FT_Feature> collection) {
    return CarteTopoFactory.newCarteTopo(name, collection, 1.0);
  }

  public static CarteTopo newCarteTopo(
      FT_FeatureCollection<? extends FT_Feature> collection) {
    return CarteTopoFactory.newCarteTopo("TopoMap", collection);
  }
}
