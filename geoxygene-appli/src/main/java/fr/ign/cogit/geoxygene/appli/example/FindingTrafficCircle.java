package fr.ign.cogit.geoxygene.appli.example;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Reperer les quasi-cercles dans une couche de lineaires
 * Surfaces => Ronds
 * 
 * 
 */
public class FindingTrafficCircle extends AbstractGeOxygeneApplicationPlugin {

  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(FindingTrafficCircle.class.getName());

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenu menuAwt = addMenu("Example", "Selection quasi cercles");
    application.getMainFrame().getMenuBar()
      .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(final ActionEvent e) {

    ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
    URL routeURL;
    Layer layer;
    try {

      routeURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/ROUTE.SHP");
      IPopulation<IFeature> routePop = ShapefileReader.read(routeURL.getPath());
      layer = projectFrame.addUserLayer(routePop, "Routes", null);
    
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
      return;
    }

    CarteTopo carte = new CarteTopo("Carte");
    carte.importClasseGeo(layer.getFeatureCollection());
    LOGGER.debug("--- creation des noeuds --- ");
    carte.creeNoeudsManquants(1.0);
    LOGGER.debug("--- fusion des noeuds --- ");
    carte.fusionNoeuds(1.0);
    LOGGER.debug("--- découpage des arcs --- ");
    carte.decoupeArcs(1.0);
    LOGGER.debug("--- filtrage des arcs doublons --- ");
    carte.filtreArcsDoublons();
    LOGGER.debug("--- rend planaire --- ");
    carte.rendPlanaire(1.0);
    LOGGER.debug("--- fusion des doublons --- ");
    carte.fusionNoeuds(1.0);
    LOGGER.debug("--- filtrage des arcs doublons --- ");
    carte.filtreArcsDoublons();
    LOGGER.debug("--- creation de la topologie des Faces --- ");
    carte.creeTopologieFaces();
    LOGGER.info(carte.getListeFaces().size() + " faces trouvées");
    LOGGER.debug("Création de l'Index spatial");
    carte.getPopFaces().initSpatialIndex(Tiling.class, false);
    LOGGER.info("Index spatial initialisé");

    // On construit une population de DefaultFeature
    Population<DefaultFeature> pop = new Population<DefaultFeature>(
        "ronds-points(comp > 0.97) et routes incidentes");
    pop.setClasse(DefaultFeature.class);
    pop.setPersistant(false);
    // liste qui contiendra les arcs incidents aux ronds points
    // List<Arc> listeArcs = new ArrayList<Arc>();

    // on va ajouter les arcs composant le rond point à notre population
    for (Face f : carte.getPopFaces()) {
      double comp = 4 * Math.PI * f.getGeom().area()
          / (f.getGeom().length() * f.getGeom().length());
      if (comp >= 0.97) {
        LOGGER.info("face " + f.getId() + " - compacite : " + comp);
        LOGGER.info("FeatureType " + f.getFeatureType());
        LOGGER.info("Geometry " + f.getGeom());
        ;
        for (Arc a : f.arcs())
          pop.nouvelElement(a.getGeom());
        for (Arc a : (List<Arc>) f.arcsExterieursClasses().get(0))
          pop.nouvelElement(a.getGeom());
      }
    }

    // Créer les métadonnées du jeu correspondant et on l'ajoute à la population
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setGeometryType(pop.get(0).getGeom().getClass());
    pop.setFeatureType(newFeatureType);

    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.addFeatureCollection(pop, pop.getNom(), layer.getCRS());
  }
}
