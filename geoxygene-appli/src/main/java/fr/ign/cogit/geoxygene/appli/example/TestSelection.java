package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Centrer facilement une carte sur une longitude particulière. Très bon
 * exercice pour manipuler les géométries avec les DirectPosition
 * 
 * @author GBrun
 */
public class TestSelection implements GeOxygeneApplicationPlugin,
    ActionListener {

  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(TestSelection.class
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

    JMenuItem menuItem = new JMenuItem("test selection quasi cercles");

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
      TestSelection.LOGGER
          .error("You need to select one (and only one) layer.");
      return;
    }
    Layer layer = selectedLayers.iterator().next();

    // On construit une population de DefaultFeature
    Population<DefaultFeature> pop = new Population<DefaultFeature>(
        "Quasi-cercles(comp > 0.97) " + layer.getName());
    pop.setClasse(DefaultFeature.class);
    pop.setPersistant(false);
    List<Arc> listeArcs = new ArrayList<Arc>();
    for (IFeature f : layer.getFeatureCollection()) {
      double comp = 4 * Math.PI * f.getGeom().area()
          / (f.getGeom().length() * f.getGeom().length());
      if (comp >= 0.97) {
        LOGGER.debug("face " + f.getId() + " - compacite : " + comp);
        LOGGER.debug("FeatureType " + f.getFeatureType());
        LOGGER.debug("Geometry " + f.getGeom());
        // LOGGER.debug("Topology " + f.getTopo());
        Face k = (Face) f;
        for (Arc a : k.arcs())
          listeArcs.add(a);
        for (Arc a : (List<Arc>) k.arcsExterieursClasses().get(0))
          listeArcs.add(a);
        pop.nouvelElement(f.getGeom());
      }
    }
    // for(List<Arc> a: listeArcs)
    // LOGGER.debug("Nombre arc pour face : " + a.size());
    // Créer les métadonnées du jeu correspondant et on l'ajoute à la population
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setGeometryType(layer.getFeatureCollection().get(0)
        .getGeom().getClass());
    pop.setFeatureType(newFeatureType);

    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    project.getDataSet().addPopulation(pop);
    project.addFeatureCollection(pop, pop.getNom(), layer.getCRS());

    // On construit une population de DefaultFeature pour les arcs
    // pop = null;
    pop = new Population<DefaultFeature>("Arc Entourants " + layer.getName());
    pop.setClasse(DefaultFeature.class);
    pop.setPersistant(false);

    for (Arc a : listeArcs) {
      IFeature f = (IFeature) a;
      pop.nouvelElement(f.getGeom());
    }

    newFeatureType = new FeatureType();
    newFeatureType
        .setGeometryType(listeArcs.get(0).getGeom().getClass());
    pop.setFeatureType(newFeatureType);

    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    project.getDataSet().addPopulation(pop);
    project.addFeatureCollection(pop, pop.getNom(), layer.getCRS());

  }
}
