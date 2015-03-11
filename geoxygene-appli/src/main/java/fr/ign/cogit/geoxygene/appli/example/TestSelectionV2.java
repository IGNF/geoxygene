package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Reperer les quasi-cercles dans une couche de lineaires
 * 
 * 
 * @author GBrun
 */
public class TestSelectionV2 implements GeOxygeneApplicationPlugin,
    ActionListener {

  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(TestSelectionV2.class
      .getName());

  /** GeOxygeneApplication */
  private GeOxygeneApplication application = null;

  // ...
  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;

    JMenu menuExample = null;
    String menuName = "Example";

    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menuExample = aMenu;
        }
      }
    }
    if (menuExample == null) {
      menuExample = new JMenu(menuName);
    }

    JMenuItem menuItem = new JMenuItem("test selection quasi-cercles v2");

    menuItem.addActionListener(this);
    menuExample.add(menuItem);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(final ActionEvent e) {

    // On récupère la couche sélectionnée
    ProjectFrame project = this.application.getMainFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      javax.swing.JOptionPane.showMessageDialog(null,
          "You need to select one (and only one) layer.");
      TestSelectionV2.LOGGER
          .error("You need to select one (and only one) layer.");
      return;
    }
    Layer layer = selectedLayers.iterator().next();

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
    project.getDataSet().addPopulation(pop);
    project.addFeatureCollection(pop, pop.getNom(), layer.getCRS());
  }
}
