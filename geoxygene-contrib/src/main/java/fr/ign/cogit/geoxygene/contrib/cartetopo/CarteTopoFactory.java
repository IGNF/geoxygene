package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Simple factory for topological map creation.
 * 
 * @author Eric Grosso
 * @author Julien Perret
 */
public class CarteTopoFactory {
  public static Logger logger = Logger.getLogger(CarteTopoFactory.class.getName());

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
  public static CarteTopo newCarteTopo(String name, IFeatureCollection<? extends IFeature> collection, double threshold,
      boolean infiniteFace) {
    // Initialisation d'une nouvelle CarteTopo
    CarteTopo carteTopo = new CarteTopo(name);
    carteTopo.setBuildInfiniteFace(infiniteFace);
    // Récupération des arcs de la carteTopo
    IPopulation<Arc> arcs = carteTopo.getPopArcs();
    // Import des arcs de la collection dans la carteTopo
    for (IFeature feature : collection) {
      // création d'un nouvel élément
      Arc arc = arcs.nouvelElement();
      // affectation de la géométrie de l'objet issu de la collection
      // à l'arc de la carteTopo
      arc.setGeometrie((GM_LineString) feature.getGeom());
      // instanciation de la relation entre l'arc créé et l'objet
      // issu de la collection
      arc.addCorrespondant(feature);
    }
    CarteTopoFactory.logger.info("Nombre d'arcs créés : " //$NON-NLS-1$
        + carteTopo.getPopArcs().size());
    // Création des noeuds manquants
    CarteTopoFactory.logger.info("Création des noeuds manquants"); //$NON-NLS-1$
    carteTopo.creeNoeudsManquants(threshold);
    // Création de la topologie Arcs Noeuds
    CarteTopoFactory.logger.info("Création de la topologie Arcs Noeuds"); //$NON-NLS-1$
    carteTopo.creeTopologieArcsNoeuds(threshold);
    // La carteTopo est rendue planaire
    CarteTopoFactory.logger.info("La carte topologique est rendue planaire"); //$NON-NLS-1$
    carteTopo.rendPlanaire(threshold);
    CarteTopoFactory.logger.info("Création des faces de la carte topologique"); //$NON-NLS-1$
    // Création des faces de la carteTopo
    carteTopo.creeTopologieFaces();
    CarteTopoFactory.logger.info("Nombre de faces créées : " //$NON-NLS-1$
        + carteTopo.getPopFaces().size());
    return carteTopo;
  }

  /**
   * Create a topological map from a feature collection and a name. A threshold
   * of 1m is used for node creation as well as for topology and planar map
   * creations.
   * <p>
   * Création d'une CarteTopo à partir d'une FT_FeatureCollection et d'un nom.
   * @see #newCarteTopo(String, IFeatureCollection, double, boolean)
   * @param name name of the topo map
   * @param collection collection de features
   * @return a topological map
   */
  public static CarteTopo newCarteTopo(String name, IFeatureCollection<? extends IFeature> collection) {
    return CarteTopoFactory.newCarteTopo(name, collection, 1.0, false);
  }

  /**
   * Create a topological map from a feature collection. A threshold of 1m is
   * used for node creation as well as for topology and planar map creations.
   * <p>
   * Création d'une CarteTopo à partir d'une FT_FeatureCollection.
   * @see #newCarteTopo(String, IFeatureCollection, double, boolean)
   * @param collection collection de features
   * @return a topological map
   */
  public static CarteTopo newCarteTopo(IFeatureCollection<? extends IFeature> collection) {
    return CarteTopoFactory.newCarteTopo("TopoMap", collection); //$NON-NLS-1$
  }

  /**
   * Create a topological map from a feature collection, a name and a threshold.
   * The threshold is used for node creation as well as for topology and planar
   * map creations.
   * <p>
   * Création d'une CarteTopo à partir de deux FT_FeatureCollection, d'un nom et
   * d'un seuil. Ce dernier sert à créer les noeuds manquants, la topologie
   * arc-noeud et pour rendre le graphe planaire.
   * @param name name of the topo map
   * @param collection collection de features
   * @param threshold threshold used for all the things mentioned above
   * @return a topological map
   */
  public static CarteTopo newCarteTopo(String name, IFeatureCollection<? extends IFeature> collection,
      IFeatureCollection<? extends IFeature> collection2, double threshold, boolean infiniteFace) {
    // Initialisation d'une nouvelle CarteTopo
    CarteTopo carteTopo = new CarteTopo(name);
    carteTopo.setBuildInfiniteFace(infiniteFace);
    // Récupération des arcs de la carteTopo
    IPopulation<Arc> arcs = carteTopo.getPopArcs();
    // Import des arcs de la collection dans la carteTopo
    for (IFeature feature : collection) {
      // création d'un nouvel élément
      Arc arc = arcs.nouvelElement();
      // affectation de la géométrie de l'objet issu de la collection
      // à l'arc de la carteTopo
      arc.setGeometrie((GM_LineString) feature.getGeom());
      // instanciation de la relation entre l'arc créé et l'objet
      // issu de la collection
      arc.addCorrespondant(feature);
    }
    for (IFeature feature : collection2) {
      // création d'un nouvel élément
      Arc arc = arcs.nouvelElement();
      // affectation de la géométrie de l'objet issu de la collection
      // à l'arc de la carteTopo
      arc.setGeometrie((GM_LineString) feature.getGeom());
      // instanciation de la relation entre l'arc créé et l'objet
      // issu de la collection
      arc.addCorrespondant(feature);
    }

    CarteTopoFactory.logger.info("Nombre d'arcs créés : " //$NON-NLS-1$
        + carteTopo.getPopArcs().size());
    // Création des noeuds manquants
    CarteTopoFactory.logger.info("Création des noeuds manquants"); //$NON-NLS-1$
    carteTopo.creeNoeudsManquants(threshold);
    // Création de la topologie Arcs Noeuds
    CarteTopoFactory.logger.info("Création de la topologie Arcs Noeuds"); //$NON-NLS-1$
    carteTopo.creeTopologieArcsNoeuds(threshold);
    // La carteTopo est rendue planaire
    CarteTopoFactory.logger.info("La carte topologique est rendue planaire"); //$NON-NLS-1$
    carteTopo.rendPlanaire(threshold);
    CarteTopoFactory.logger.info("Création des faces de la carte topologique"); //$NON-NLS-1$
    // Création des faces de la carteTopo
    carteTopo.creeTopologieFaces();
    CarteTopoFactory.logger.info("Nombre de faces créées : " //$NON-NLS-1$
        + carteTopo.getPopFaces().size());
    return carteTopo;
  }

}
